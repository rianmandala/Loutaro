package com.example.loutaro.ui.profile.updateProfile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.adapter.ListSkillsAdapter
import com.example.loutaro.data.dummy.Categori
import com.example.loutaro.data.entity.BusinessMan
import com.example.loutaro.data.entity.Contact
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.databinding.ActivityUpdateProfileBinding
import com.example.loutaro.databinding.ItemInputSkillBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.country.SelectCountryActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class UpdateProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityUpdateProfileBinding
    private val updateProfileViewModel: UpdateProfileViewModel by viewModels {
        ViewModelFactory.getInstance()
    }
    private var statusCompleteValidate= false

    private var listSkillsAdapter: ListSkillsAdapter?=null
    private var dataSkills= mutableListOf<String>()

    private var filePath: Uri? = null
    private var firstLoading=true
    companion object{
        val PICK_IMAGE_REQUEST = 27
        val REQUEST_CODE_FOR_COUNTRY = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTinyDB()
        setToolbarTitle(getString(R.string.update_profile))
        activatedToolbarBackButton()

        binding.run{
            btnSubmitProfileUpdate.setOnClickListener {
                if(completeValidate()){
                    uploadData()
                }else{
                    showWarningSnackbar(message = getString(R.string.still_have_data_not_fill))
                }
            }

            inputServiceUpdate.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if(!firstLoading){
                        dataSkills.clear()
                        updateDataSkills()
                    }
                }

            })

            inputCountryUpdate.setOnClickListener{
                var value= ""
                if(getString(R.string.select_country)!=binding.inputCountryUpdate.text.toString()){
                    value=binding.inputCountryUpdate.text.toString()
                }
                val selectCountryIntent = Intent(this@UpdateProfileActivity, SelectCountryActivity::class.java)
                selectCountryIntent.putExtra(SelectCountryActivity.EXTRA_SELECTED_VALUE, value)
                startActivityForResult(selectCountryIntent, REQUEST_CODE_FOR_COUNTRY)
            }

            inputNameUpdate.doAfterTextChanged {
                validateRequire(it.toString(),txtInputNameUpdate, getString(R.string.name))
            }
            inputAboutUpdate.doAfterTextChanged {
                if(!firstLoading){
                    validateRequire(it.toString(),txtInputAboutUpdate, getString(R.string.about))
                }
            }
        }

        if(getUserTypeLogin()==getString(R.string.value_freelancer)){
            initUserFreelancer()
        }
        else if(getUserTypeLogin()==getString(R.string.value_business_man)){
            initUserBusinessMan()
        }


        updateProfileViewModel.statusPutDataAndFile.observe(this){ result->
            if(result.status){
                if(getUserTypeLogin()==getString(R.string.value_freelancer))
                    updateProfileViewModel.updateFullDataFreelancer(getCurrentUser()?.uid.toString(), Freelancer(
                            photo = result.response,
                            name = binding.inputNameUpdate.text.toString(),
                            country = binding.inputCountryUpdate.text.toString(),
                            address = binding.inputAddressUpdate.text.toString(),
                            contact = Contact(
                                telephone = binding.inputTelephoneUpdate.text.toString(),
                                telegram = binding.inputTelegramUpdate.text.toString()
                            ),
                            portofolio = binding.inputPortofolioUpdate.text.toString(),
                            service = binding.inputServiceUpdate.text.toString(),
                            skills = dataSkills,
                            bio = binding.inputAboutUpdate.text.toString()
                    )
                )else if(getUserTypeLogin()==getString(R.string.value_business_man)){
                    updateProfileViewModel.updateFullDataBusinessMan(getCurrentUser()?.uid.toString(), BusinessMan(
                            photo = result.response,
                            name = binding.inputNameUpdate.text.toString(),
                            country = binding.inputCountryUpdate.text.toString(),
                            address = binding.inputAddressUpdate.text.toString(),
                            contact = Contact(
                                    telephone = binding.inputTelephoneUpdate.text.toString(),
                                    telegram = binding.inputTelegramUpdate.text.toString()
                            ),
                            portofolio = binding.inputPortofolioUpdate.text.toString(),
                            bio = binding.inputAboutUpdate.text.toString()
                        )
                    )
                }
                closeProgressDialog()
            }else{
                closeProgressDialog()
                showErrorSnackbar(result.response.toString())
            }
        }

        updateProfileViewModel.statusProgressPutFile.observe(this){ progress->
            setProgressUploadDialog(getString(R.string.uploaded, progress))
        }

        binding.btnUploadPhotoUpdate.setOnClickListener {
            val choosePhotoIntent = Intent()
            choosePhotoIntent.setType("image/*")
            choosePhotoIntent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(
                Intent.createChooser(
                    choosePhotoIntent,
                    "Select Your Professional Photo"
                ),
                PICK_IMAGE_REQUEST
            )
        }
    }

    private fun initUserBusinessMan() {
        binding.run {
            setViewToGone(txtInputServiceUpdate, inputServiceUpdate, tagSelectSkillsUpdate, cgSkillsUpdate, rvSkillsUpdate, cpAddSkillUpdate)
        }
        updateProfileViewModel.responseGetMyProfileBusinessMan.observe(this){ businessMan->
            binding.inputNameUpdate.setText(businessMan.name?:"")
            binding.inputCountryUpdate.setText(businessMan.country?:"")
            binding.inputAddressUpdate.setText(businessMan.address?:"")
            binding.inputTelephoneUpdate.setText(businessMan.contact?.telephone?:"")
            binding.inputTelegramUpdate.setText(businessMan.contact?.telegram?:"")
            binding.inputPortofolioUpdate.setText(businessMan.portofolio?:"")
            binding.inputAboutUpdate.setText(businessMan.bio?:"")

            val drawable = TextDrawable.builder()
                    .buildRect("${businessMan.name?.toUpperCase()?.get(0)}", ContextCompat.getColor(this@UpdateProfileActivity, R.color.secondary))
            if(businessMan.photo == null) {
                binding.imgPhotoUserUpdate.setImageDrawable(drawable)
            }else{
                Glide.with(this@UpdateProfileActivity)
                        .load(businessMan.photo)
                        .into(binding.imgPhotoUserUpdate)
            }
            firstLoading=false
        }

        updateProfileViewModel.getMyProfileBusinessMan()

        updateProfileViewModel.statusSetDataBusinessMan.observe(this){
            if(it.status){
                finish()
            }else{
                showErrorSnackbar(it.message.toString())
            }
        }
    }

    private fun initUserFreelancer() {
        binding.run {
            val layoutManager = FlexboxLayoutManager(this@UpdateProfileActivity)
            layoutManager.flexDirection = FlexDirection.ROW
            rvSkillsUpdate.layoutManager = layoutManager
            listSkillsAdapter= ListSkillsAdapter()
            rvSkillsUpdate.adapter = listSkillsAdapter

            val adapter = ArrayAdapter(
                    this@UpdateProfileActivity, R.layout.list_item,
                    resources.getStringArray(R.array.list_service))
            inputServiceUpdate.setAdapter(adapter)
            inputServiceUpdate.setText(resources.getStringArray(R.array.list_service)[0],false)

            cpAddSkillUpdate.setOnClickListener {
                var originalSkill= when(binding.inputServiceUpdate.text.toString()){
                    resources.getStringArray(R.array.list_service)[0]->{
                        Categori.skill.design.toTypedArray()
                    }
                    resources.getStringArray(R.array.list_service)[1]->{
                        Categori.skill.dataScience.toTypedArray()
                    }
                    resources.getStringArray(R.array.list_service)[2]->{
                        Categori.skill.desktopDevelopment.toTypedArray()
                    }
                    resources.getStringArray(R.array.list_service)[3]->{
                        Categori.skill.webDevelopment.toTypedArray()
                    }
                    resources.getStringArray(R.array.list_service)[4]->{
                        Categori.skill.mobileDevelopment.toTypedArray()
                    }else->{
                        arrayOf()
                    }
                }

                val listSkill= originalSkill.filter {
                    !dataSkills.contains(it)
                }.toTypedArray()

                val selectedList = ArrayList<Int>()
                val dialogInputSkill = MaterialAlertDialogBuilder(this@UpdateProfileActivity)
                        .setTitle(getString(R.string.alert_add_your_skill))
                        .setMultiChoiceItems(listSkill, null
                        ) { dialog, which, isChecked ->
                            if (isChecked) {
                                selectedList.add(which)
                            } else if (selectedList.contains(which)) {
                                selectedList.remove(Integer.valueOf(which))
                            }
                        }
                        .setPositiveButton(getString(R.string.ok)){ dialog, which ->

                            for (j in selectedList.indices) {
                                if(!dataSkills.contains(listSkill[selectedList[j]])){
                                    dataSkills.add(listSkill[selectedList[j]])
                                }
                            }
                            updateDataSkills()
                        }
                        .setNegativeButton(getString(R.string.cancel)){ dialog, which ->

                        }
                        .setCancelable(false)
                if(listSkill.isEmpty()){
                    dialogInputSkill.setMessage(getString(R.string.you_have_selected_all_skill))
                }
                dialogInputSkill.create()
                dialogInputSkill.show()
            }
        }

        listSkillsAdapter?.onItemClick = {
            dataSkills.removeAt(it)
            updateDataSkills()
        }

        updateProfileViewModel.responseGetMyProfileFreelancer.observe(this){ freelancer->
            binding.inputNameUpdate.setText(freelancer.name?:"")
            binding.inputCountryUpdate.setText(freelancer.country?:"")
            binding.inputAddressUpdate.setText(freelancer.address?:"")
            binding.inputTelephoneUpdate.setText(freelancer.contact?.telephone?:"")
            binding.inputTelegramUpdate.setText(freelancer.contact?.telegram?:"")
            binding.inputPortofolioUpdate.setText(freelancer.portofolio?:"")
            binding.inputAboutUpdate.setText(freelancer.bio?:"")

            if(freelancer.service!=null){
                binding.inputServiceUpdate.setText(freelancer.service, false)
            }

            val drawable = TextDrawable.builder()
                    .buildRect("${freelancer.name?.toUpperCase()?.get(0)}", ContextCompat.getColor(this@UpdateProfileActivity, R.color.secondary))
            if(freelancer.photo == null) {
                binding.imgPhotoUserUpdate.setImageDrawable(drawable)
            }else{
                Glide.with(this@UpdateProfileActivity)
                        .load(freelancer.photo)
                        .into(binding.imgPhotoUserUpdate)
            }
            if(freelancer.skills!=null){
                dataSkills.addAll(freelancer.skills!!)
                updateDataSkills()
            }
            firstLoading=false

        }

        updateProfileViewModel.getMyProfileFreelancer()

        updateProfileViewModel.statusSetDataFreelancer.observe(this){
            if(it.status){
                finish()
            }else{
                showErrorSnackbar(it.message.toString())
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== PICK_IMAGE_REQUEST){
            if(resultCode== RESULT_OK && data!=null && data.data != null){
                filePath = data.data
                try{
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    Glide.with(this)
                        .load(bitmap)
                        .into(binding.imgPhotoUserUpdate)
                    binding.run{
                        imgPhotoUserUpdate.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        imgPhotoUserUpdate.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                }catch (e: IOException){
                    showErrorSnackbar(e.message.toString())
                }
            }
        }

        if(requestCode== REQUEST_CODE_FOR_COUNTRY){
            if(resultCode==SelectCountryActivity.RESULT_CODE){
                val selectableValue = data?.getStringExtra(SelectCountryActivity.EXTRA_SELECTED_VALUE)
                binding.run {
                    inputCountryUpdate.text.clear()
                    inputCountryUpdate.text.insert(0,selectableValue)
                }
            }
        }
    }

    private fun updateDataSkills(){
        if(isUserFreelancer(this)){
            binding.run {
                if(dataSkills.isEmpty()){
                    setViewToVisible(tvErrorSkillUpdateProfile)
                    tvErrorSkillUpdateProfile.text = getString(R.string.required,getString(R.string.skill))
                }else{
                    setViewToGone(tvErrorSkillUpdateProfile)
                }
            }
        }
        listSkillsAdapter?.submitList(dataSkills)
        listSkillsAdapter?.notifyDataSetChanged()
    }

    private fun uploadData() {
        if (filePath != null) {
            Log.d("hasil_update","ini di upload data ${getCurrentUser()?.uid.toString()}")
            showProgressDialog(title = getString(R.string.uploading))
            updateProfileViewModel.putDataAndFile(filePath!!)
        }else{
            Log.d("hasil_update","ini diluar freelancer dan pelaku usaha ${getCurrentUser()?.uid.toString()}")
            if(isUserFreelancer(this)){
                Log.d("hasil_update","ini disini freelancer ${getCurrentUser()?.uid.toString()}")
                updateProfileViewModel.updateFullDataFreelancer(getCurrentUser()?.uid.toString(), Freelancer(
                    photo = updateProfileViewModel.responseGetMyProfileFreelancer.value?.photo,
                    name = binding.inputNameUpdate.text.toString(),
                    country = binding.inputCountryUpdate.text.toString(),
                    address = binding.inputAddressUpdate.text.toString(),
                    contact = Contact(
                        telephone = binding.inputTelephoneUpdate.text.toString(),
                        telegram = binding.inputTelegramUpdate.text.toString()
                    ),
                    portofolio = binding.inputPortofolioUpdate.text.toString(),
                    service = binding.inputServiceUpdate.text.toString(),
                    skills = dataSkills,
                    bio = binding.inputAboutUpdate.text.toString()
                ))
            }else if(isUserBusinessMan(this)){
                Log.d("hasil_update","ini disini pelaku usaha ${getCurrentUser()?.uid.toString()}")
                updateProfileViewModel.updateFullDataBusinessMan(getCurrentUser()?.uid.toString(), BusinessMan(
                    photo = updateProfileViewModel.responseGetMyProfileBusinessMan.value?.photo,
                    name = binding.inputNameUpdate.text.toString(),
                    country = binding.inputCountryUpdate.text.toString(),
                    address = binding.inputAddressUpdate.text.toString(),
                    contact = Contact(
                        telephone = binding.inputTelephoneUpdate.text.toString(),
                        telegram = binding.inputTelegramUpdate.text.toString()
                    ),
                    portofolio = binding.inputPortofolioUpdate.text.toString(),
                    bio = binding.inputAboutUpdate.text.toString()
                )
                )
            }

        }
    }

    private fun completeValidate(): Boolean{
        statusCompleteValidate=true
        var tempStatusComplete: Boolean
        binding.run {
            tempStatusComplete= validateRequire(inputNameUpdate.text.toString(), txtInputNameUpdate, getString(R.string.name))
            if(!tempStatusComplete) statusCompleteValidate=false
            tempStatusComplete= validateRequire(inputAboutUpdate.text.toString(), txtInputAboutUpdate, getString(R.string.about))
            if(!tempStatusComplete) statusCompleteValidate=false
            if(isUserFreelancer(this@UpdateProfileActivity)){
                if(dataSkills.isEmpty()){
                    setViewToVisible(tvErrorSkillUpdateProfile)
                    tvErrorSkillUpdateProfile.text = getString(R.string.required,getString(R.string.skill))
                    statusCompleteValidate=false
                }else{
                    setViewToGone(tvErrorSkillUpdateProfile)
                }
            }

        }
        Log.d("hasil_get_create","statusCompleteValidate: $statusCompleteValidate")
        return statusCompleteValidate
    }
}