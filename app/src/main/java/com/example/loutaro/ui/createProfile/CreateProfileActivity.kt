package com.example.loutaro.ui.createProfile

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
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.adapter.ListSkillsAdapter
import com.example.loutaro.data.dummy.Categori
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.databinding.ActivityCreateProfileBinding
import com.example.loutaro.databinding.ItemInputSkillBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.verifyEmail.VerifyEmailActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.IOException
import java.util.*


class CreateProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateProfileBinding
    private var adapter: ListSkillsAdapter?=null
    private var dataSkills= mutableListOf<String>()
    private var statusCompleteValidate= false

    private var filePath: Uri? = null

    private val createProfileViewModel: CreateProfileViewModel by viewModels {
        ViewModelFactory.getInstance()
    }

    companion object{
        val PICK_IMAGE_REQUEST = 27
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarTitle(getString(R.string.create_profile))
        activatedToolbarBackButton()

        createProfileViewModel.statusSetDataFreelancer.observe(this){
            if(it.status){
                createProfileViewModel.sendEmailVerification()?.addOnFailureListener { email->
                    showErrorSnackbar(email.message.toString())
                }
                navigateForward(VerifyEmailActivity::class.java)
            }else{
                showErrorSnackbar(it.message.toString())
            }
        }

        createProfileViewModel.statusPutDataAndFile.observe(this){ result->
            if(result.status){
                createProfileViewModel.updateDataFreelancer(getCurrentUser()?.uid.toString(), Freelancer(
                        photo = result.response,
                        service = binding.inputService.text.toString(),
                        skills = dataSkills,
                        bio = binding.inputAbout.text.toString()
                ))
                closeProgressDialog()
            }else{
                closeProgressDialog()
                showErrorSnackbar(result.response.toString())
            }
        }

        createProfileViewModel.statusProgressPutFile.observe(this){ progress->
            setProgressUploadDialog(getString(R.string.uploaded, progress))
        }

        binding.run{
            val layoutManager = FlexboxLayoutManager(this@CreateProfileActivity)
            layoutManager.flexDirection = FlexDirection.ROW
            rvSkills.layoutManager = layoutManager
            adapter= ListSkillsAdapter()
            rvSkills.adapter = adapter

            inputAbout.doAfterTextChanged {
                validateRequire(it.toString(),txtInputAbout, getString(R.string.about))
            }

            val adapter = ArrayAdapter(
                this@CreateProfileActivity, R.layout.list_item,
                resources.getStringArray(R.array.list_service))
            inputService.setAdapter(adapter)
            inputService.setText(resources.getStringArray(R.array.list_service)[0],false)

            inputService.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    dataSkills.clear()
                    updateDataSkills()
                }

            })

            btnSubmitProfile.setOnClickListener {
                if(completeValidate()){
                    uploadData()
                }else{
                    showWarningSnackbar(message = getString(R.string.still_have_data_not_fill))
                }
            }
        }
        adapter?.onItemClick = {
            dataSkills.removeAt(it)
            updateDataSkills()
        }

        binding.btnUploadPhoto.setOnClickListener {
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

        binding.cpAddSkill.setOnClickListener {
            var originalSkill= when(binding.inputService.text.toString()){
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
            val dialogInputSkill = MaterialAlertDialogBuilder(this)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== PICK_IMAGE_REQUEST){
            if(resultCode== RESULT_OK && data!=null && data.data != null){
                filePath = data.data
                try{
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                    Glide.with(this)
                        .load(bitmap)
                        .into(binding.imgPhotoUser)
                    binding.run{
                        imgPhotoUser.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        imgPhotoUser.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                }catch (e: IOException){
                    showErrorSnackbar(e.message.toString())
                }
            }
        }
    }

    fun updateDataSkills(){
        binding.run {
            if(dataSkills.isEmpty()){
                setViewToVisible(tvErrorSkillCreateProfile)
                tvErrorSkillCreateProfile.text = getString(R.string.required,"Skill")
            }else{
                setViewToGone(tvErrorSkillCreateProfile)
            }
        }
        adapter?.submitList(dataSkills)
        adapter?.notifyDataSetChanged()
    }

    private fun uploadData() {
        if (filePath != null) {
            showProgressDialog(title = getString(R.string.uploading))
            createProfileViewModel.putDataAndFile(filePath!!)
        }else{
            createProfileViewModel.updateDataFreelancer(getCurrentUser()?.uid.toString(), Freelancer(
                service = binding.inputService.text.toString(),
                skills = dataSkills,
                bio = binding.inputAbout.text.toString()
            ))
        }
    }

    private fun completeValidate(): Boolean{
        statusCompleteValidate=true
        var tempStatusComplete: Boolean
        binding.run {
            tempStatusComplete= validateRequire(inputAbout.text.toString(), txtInputAbout, getString(R.string.about))
            if(!tempStatusComplete) statusCompleteValidate=false
            if(dataSkills.isEmpty()){
                setViewToVisible(tvErrorSkillCreateProfile)
                tvErrorSkillCreateProfile.text = getString(R.string.required,"Skill")
                statusCompleteValidate=false
            }else{
                setViewToGone(tvErrorSkillCreateProfile)
            }
        }
        Log.d("hasil_get_create","statusCompleteValidate: $statusCompleteValidate")
        return statusCompleteValidate
    }
}