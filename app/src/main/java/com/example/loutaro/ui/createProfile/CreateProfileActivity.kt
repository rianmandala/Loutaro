package com.example.loutaro.ui.createProfile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.adapter.ListSkillsAdapter
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

            val adapter = ArrayAdapter(
                this@CreateProfileActivity, R.layout.list_item,
                resources.getStringArray(R.array.list_service))
            inputService.setAdapter(adapter)

            btnSubmitProfile.setOnClickListener {
                uploadData()
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
            val itemInputSkilView = ItemInputSkillBinding.inflate(layoutInflater)
            itemInputSkilView.addSkill.requestFocus()
            val dialogInputSkill = MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.alert_add_your_skill))
                .setView(itemInputSkilView.root)
                .setPositiveButton(getString(R.string.ok)){ dialog, which ->
                    val skill = itemInputSkilView.addSkill.text.toString()
                    if(skill.isNotEmpty() && skill.isNotBlank()){
                        updateDataSkills()
                        dataSkills.add(skill)
                    }
                }
                .setNegativeButton(getString(R.string.cancel)){ dialog, which ->

                }
                .setCancelable(false)
                .create()

            dialogInputSkill.show()
            itemInputSkilView.addSkill.setOnEditorActionListener { v, actionId, event ->
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    val skill = itemInputSkilView.addSkill.text.toString()
                    if(skill.isNotEmpty() && skill.isNotBlank()){
                        updateDataSkills()
                        dataSkills.add(itemInputSkilView.addSkill.text.toString())
                        dialogInputSkill.dismiss()
                        return@setOnEditorActionListener true
                    }
                }
                return@setOnEditorActionListener false
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
        adapter?.submitList(dataSkills)
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
}