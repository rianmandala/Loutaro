package com.example.loutaro.ui.project.activeProject.applyers

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListFreelancerApplyersAdapter
import com.example.loutaro.data.entity.*
import com.example.loutaro.databinding.ActivityApplyersBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ApplyersActivity : BaseActivity() {
    private lateinit var binding: ActivityApplyersBinding
    private val listFreelancerApplyersAdapter= ListFreelancerApplyersAdapter(canConfirm = true)
    private var detailProject= Project()
    private val applyersViewModel: ApplyersViewModel by viewModels {
        ViewModelFactory.getInstance()
    }

    companion object{
        const val EXTRA_ID_PROJECT= "EXTRA_ID_PROJECT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApplyersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idProject = intent.getStringExtra(EXTRA_ID_PROJECT)

        showProgressLoading()
        setToolbarTitle(getString(R.string.applyers))
        activatedToolbarBackButton()

        binding.run {
            rvApplier.layoutManager= LinearLayoutManager(this@ApplyersActivity)
            rvApplier.adapter = listFreelancerApplyersAdapter
        }

        applyersViewModel.responseGetDetailProject.observe(this){ project->
            if(project!=null){
                detailProject = project
                binding.run {
                    val listTasks = detailProject.tasks
                    var isHaveApplierBefore=false
                    if (listTasks != null && listTasks.isNotEmpty()) {
                        var freelancerApplyers = mutableListOf<TempFreelancerApplyers>()
                        for (i in 0.until(listTasks.size)){
                            if(listTasks[i].applyers != null){
                                for(idApplyer in listTasks[i].applyers!!){
                                    freelancerApplyers.add(TempFreelancerApplyers(idFreelancer = idApplyer, applyAsFreelancer = i+1))
                                }
                            }
                        }
                        Log.d("hasil_applyer","ini dia haha ${freelancerApplyers}")
                        val listApplier = freelancerApplyers.map {
                            it.idFreelancer.toString()
                        }
                        var listFreelancer = mutableListOf<FreelancerApplyers?>()
                        if(listApplier.isNotEmpty()){
                            CoroutineScope(Dispatchers.IO).launch{
                                listApplier.forEachIndexed { index, idApplyer->
                                    val response = applyersViewModel.getDetailFreelancer(idApplyer).await()
                                    val convertToFreelancer = response.toObject(FreelancerApplyers::class.java)
                                    if(convertToFreelancer!=null){
                                        convertToFreelancer.idFreelancer = idApplyer
                                        convertToFreelancer.applyAsFreelancer = freelancerApplyers[index].applyAsFreelancer
                                        listFreelancer.add(convertToFreelancer)
                                    }
                                }
                                Log.d("hasil_applyer","listFreelancer: ${listFreelancer}")
                                withContext(Dispatchers.Main){
                                    listFreelancerApplyersAdapter.submitList(listFreelancer)
                                    listFreelancerApplyersAdapter.notifyDataSetChanged()
                                    showApplierFreelancer()
                                }

                            }
                        }else{
                            listFreelancerApplyersAdapter.submitList(listFreelancer)
                            listFreelancerApplyersAdapter.notifyDataSetChanged()
                            showInfoNoNotification()
                        }

                    }else{
                        Log.d("hasil_get_ongooing","di percabangan pertama")
                        showInfoNoNotification()
                    }

                }
            }
        }

        applyersViewModel.getDetailProject(idProject.toString())


        listFreelancerApplyersAdapter.onContactClickCallback={ typeContact: String, resource: String ->
            when(typeContact){
                getString(R.string.phone) -> {
                    callPhone(resource)
                }
                getString(R.string.email) -> {
                    contactEmail(resource)
                }
                getString(R.string.telegram) -> {
                    contactByTelegram(resource)
                }
            }
        }

        listFreelancerApplyersAdapter.onConfirmApplyersClickCallback={ idApplyers, positionApplyer->
            CoroutineScope(Dispatchers.IO).launch{
                try {
                    if(detailProject.tasks!=null){
                        Log.d("hasil_applyer","idApplyer: ${idApplyers} dan list task ${detailProject.tasks}")
                        val indexFound = detailProject.tasks!!.indexOfFirst {
                            it.applyers?.contains(idApplyers) == true
                        }
                        if(indexFound != -1){
                            if(detailProject.tasks!![indexFound].selectedApplyers==null){
                                Log.d("hasil_applyer","ini masuk 1 applyer ${detailProject.tasks!![indexFound].applyers}")
                                detailProject.tasks!![indexFound].selectedApplyers=idApplyers
                                detailProject.tasks!![indexFound].applyers?.remove(idApplyers)
                                Log.d("hasil_applyer","ini masuk 1 applyer ${detailProject.tasks!![indexFound].applyers}")
                                if(detailProject.idFreelancer!=null){
                                    if(detailProject.idFreelancer!!.contains(idApplyers)==false){
                                        detailProject.idFreelancer?.add(idApplyers)
                                    }
                                }else{
                                    detailProject.idFreelancer = mutableListOf(idApplyers)
                                }

                                applyersViewModel.confirmApplyers(idProject.toString(), detailProject).await()

                                val response = applyersViewModel.getDetailBoard(detailProject.idBoards.toString()).get().await()
                                val convertToBoards = response.toObject(Boards::class.java)
                                Log.d("hasil_applyer","hasil board member ${convertToBoards}")

                                if (convertToBoards != null) {
                                    convertToBoards.columns?.forEach { column->
                                        column?.cards?.forEach {card->
                                            if(detailProject.tasks?.get(positionApplyer.toInt())?.todo?.contains(card?.name.toString()) == true){
                                                card?.member = idApplyers
                                            }
                                        }
                                    }
                                    if(convertToBoards.members!=null){
                                        if(convertToBoards.members!!.contains(idApplyers) == false){
                                            convertToBoards.members!!.add(idApplyers)
                                        }
                                    }else{
                                        convertToBoards.members= mutableListOf(idApplyers)
                                    }

                                    applyersViewModel.updateBoards(detailProject.idBoards.toString(), convertToBoards).await()
                                }
                                withContext(Dispatchers.Main){
                                    showSnackbar(message = getString(R.string.success_add_applyers_to_project), activity = this@ApplyersActivity)
                                }
                            }else{
                                withContext(Dispatchers.Main){
                                    val applyerExist = detailProject.tasks!![indexFound].selectedApplyers
                                    MaterialAlertDialogBuilder(this@ApplyersActivity)
                                            .setMessage(getString(R.string.there_is_already_a_freelancer_for_this_task))
                                            .setPositiveButton(getString(R.string.ok)){ dialogInterface: DialogInterface, i: Int ->
                                                Log.d("hasil_applyer","ini masuk 2 applyer ${detailProject.tasks!![indexFound].applyers}")
                                                detailProject.tasks!![indexFound].selectedApplyers=idApplyers
                                                detailProject.tasks!![indexFound].applyers?.remove(idApplyers)
                                                Log.d("hasil_applyer","ini masuk 3 applyer ${detailProject.tasks!![indexFound].applyers}")
                                                if(detailProject.idFreelancer!=null){
                                                    if(detailProject.idFreelancer!!.contains(idApplyers) == false){
                                                        detailProject.idFreelancer?.remove(applyerExist)
                                                        detailProject.idFreelancer?.add(idApplyers)
                                                    }
                                                }else{
                                                    detailProject.idFreelancer = mutableListOf()
                                                }
                                                Log.d("hasil_applyer","idApplyer: ${idApplyers} dan list task ${detailProject.tasks}")

                                                CoroutineScope(Dispatchers.IO).launch{
                                                    applyersViewModel.confirmApplyers(idProject.toString(), detailProject).await()

                                                    val response = applyersViewModel.getDetailBoard(detailProject.idBoards.toString()).get().await()
                                                    val convertToBoards = response.toObject(Boards::class.java)
                                                    Log.d("hasil_applyer","hasil board member ${convertToBoards}")

                                                    if (convertToBoards != null) {
                                                        convertToBoards.columns?.forEach { column->
                                                            column?.cards?.forEach {card->
                                                                if(detailProject.tasks?.get(positionApplyer.toInt())?.todo?.contains(card?.name.toString()) == true){
                                                                    card?.member = idApplyers
                                                                }
                                                            }
                                                        }
                                                        if(convertToBoards.members!=null){
                                                            if(convertToBoards.members!!.contains(idApplyers) == false){
                                                                convertToBoards.members!!.remove(applyerExist)
                                                                convertToBoards.members!!.add(idApplyers)
                                                            }
                                                        }else{
                                                            convertToBoards.members= mutableListOf()
                                                        }

                                                        applyersViewModel.updateBoards(detailProject.idBoards.toString(), convertToBoards).await()
                                                    }

                                                    withContext(Dispatchers.Main){
                                                        showSnackbar(message = getString(R.string.success_add_applyers_to_project), activity = this@ApplyersActivity)
                                                    }

                                                }
                                            }
                                            .setNegativeButton(getString(R.string.cancel)){ dialogInterface: DialogInterface, i: Int ->

                                            }
                                            .show()
                                }

                            }

                        }
                        Log.d("hasil_applyer","idApplyer: ${idApplyers} dan list task ${detailProject.tasks}")
                    }
                }catch (e: Exception){
                    Log.e("error","Error when try to confirm applyers")

                }
            }
        }

    }

    fun callPhone(numberPhone: String){
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numberPhone"))
        startActivity(intent)
    }

    fun contactEmail(email: String){
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/plain"
        intent.data = Uri.parse("mailto:$email")
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

    fun contactByTelegram(username: String){
        try {
            val telegram = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$username"))
            telegram.setPackage("org.telegram.messenger")
            startActivity(telegram)
        } catch (e: Exception) {
            showSnackbar("Telegram app is not installed")
        }
    }

    fun showProgressLoading(){
        binding.run {
            setViewToVisible(includeLayoutApplyers.layoutProgress)
            setViewToGone(rvApplier, includeLayoutInfo.parentLayoutInfo)
        }
    }

    fun showApplierFreelancer(){
        binding.run {
            setViewToGone(includeLayoutApplyers.layoutProgress, includeLayoutInfo.parentLayoutInfo)
            setViewToVisible(rvApplier)
        }
    }

    fun showInfoNoNotification(){
        binding.run {
            setViewToGone(includeLayoutApplyers.layoutProgress, rvApplier)
            setViewToVisible(includeLayoutInfo.parentLayoutInfo)
            includeLayoutInfo.imgInfo.setImageResource(R.drawable.ic_undraw_no_data_re_kwbl)
            includeLayoutInfo.tvInfo.text = getString(R.string.dont_have_any_message_yet)
        }
    }
}