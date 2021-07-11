package com.example.loutaro.ui.notification

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListApplierNotificationAdapter
import com.example.loutaro.databinding.ActivityNotificationBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory


class NotificationActivity : BaseActivity() {
    private lateinit var binding: ActivityNotificationBinding
    private val listApplierNotificationAdapter= ListApplierNotificationAdapter()
    private val notificationViewModel: NotificationViewModel by viewModels {
        ViewModelFactory.getInstance()
    }
    private var firstLoading=true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showProgressLoading()
        setToolbarTitle(getString(R.string.notification))
        activatedToolbarBackButton()

        notificationViewModel.responseGetProjectNotCompleteYet.observe(this){ listProjectOngoing->
            val listNameProject = mutableListOf<String>()
            for (project in listProjectOngoing){
                project.title?.let { listNameProject.add(it) }
            }

            binding.run {
                val adapterProjectOngoing = ArrayAdapter(
                    this@NotificationActivity, R.layout.list_item,
                    listNameProject
                )
                inputProjectNotification.setAdapter(adapterProjectOngoing)

                rvApplierNotification.layoutManager= LinearLayoutManager(this@NotificationActivity)
                rvApplierNotification.adapter = listApplierNotificationAdapter

                inputProjectNotification.setOnItemClickListener { parent, view, position, id ->
                    val listTasks = listProjectOngoing[position].tasks
                    var isHaveApplierBefore=false
                    if (listTasks != null && listTasks.isNotEmpty()) {
                        for (task in listTasks){
                            val listApplier = task.applyers
                            if(listApplier !=null && listApplier.isNotEmpty()){
                                isHaveApplierBefore=true
                                notificationViewModel.getApplyerFromFreelancer(listApplier)
                                showApplierFreelancer()
                            }else{
                                Log.d("hasil_get_ongooing","di percabangan kedua")
                                if(!isHaveApplierBefore){
                                    showInfoNoNotification()
                                }
                            }
                        }
                    }else{
                        Log.d("hasil_get_ongooing","di percabangan pertama")
                        showInfoNoNotification()
                    }
                }

                if(listProjectOngoing.size>0 && firstLoading){
                    firstLoading=false
                    inputProjectNotification.setText(listNameProject[0], false)
                    val listTasks = listProjectOngoing[0].tasks
                    var isHaveApplierBefore=false
                    if (listTasks != null && listTasks.isNotEmpty()) {
                        for (task in listTasks){
                            val listApplier = task.applyers
                            if(listApplier !=null && listApplier.isNotEmpty()){
                                isHaveApplierBefore=true
                                notificationViewModel.getApplyerFromFreelancer(listApplier)
                                showApplierFreelancer()
                            }else{
                                Log.d("hasil_get_ongooing","di percabangan kedua")
                                if(!isHaveApplierBefore){
                                    showInfoNoNotification()
                                }
                            }
                        }
                    }else{
                        Log.d("hasil_get_ongooing","di percabangan pertama")
                        showInfoNoNotification()
                    }

                }
            }
        }

        listApplierNotificationAdapter.onContactClickCallback={ typeContact: String, resource: String ->
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

        notificationViewModel.responseGetApplyerFromFreelancer.observe(this){ listFreelancer->
            listApplierNotificationAdapter.submitList(listFreelancer)
            listApplierNotificationAdapter.notifyDataSetChanged()
        }

        notificationViewModel.getProjectNotCompletedYet(getCurrentUser()?.uid.toString())
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
            setViewToVisible(includeLayoutNotification.layoutProgress)
            setViewToGone(rvApplierNotification, includeLayoutInfo.parentLayoutInfo)
        }
    }

    fun showApplierFreelancer(){
        binding.run {
            setViewToGone(includeLayoutNotification.layoutProgress, includeLayoutInfo.parentLayoutInfo)
            setViewToVisible(rvApplierNotification)
        }
    }

    fun showInfoNoNotification(){
        binding.run {
            setViewToGone(includeLayoutNotification.layoutProgress, rvApplierNotification)
            setViewToVisible(includeLayoutInfo.parentLayoutInfo)
            includeLayoutInfo.imgInfo.setImageResource(R.drawable.ic_undraw_no_data_re_kwbl)
            includeLayoutInfo.tvInfo.text = getString(R.string.dont_have_any_message_yet)
        }
    }
}