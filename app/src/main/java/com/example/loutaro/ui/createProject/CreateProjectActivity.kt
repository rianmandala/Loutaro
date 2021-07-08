package com.example.loutaro.ui.createProject

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListFreelancerNeededAdapter
import com.example.loutaro.adapter.ListSkillsAdapter
import com.example.loutaro.data.entity.Boards
import com.example.loutaro.data.entity.ItemId
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.entity.Task
import com.example.loutaro.databinding.ActivityCreateProjectBinding
import com.example.loutaro.databinding.ItemInputSkillBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class CreateProjectActivity : BaseActivity() {
    private lateinit var binding: ActivityCreateProjectBinding

    private var dataProject= Project()
    private var taskFreelancer= mutableListOf<Task>()
    private var listSkillsAdapter: ListSkillsAdapter?=null
    private lateinit var listFreelancerNeededAdapter: ListFreelancerNeededAdapter
    private var dataSkills= mutableListOf<String>()
    private var listFreelancerNeed = mutableListOf<ItemId>()
    private var statusCompleteValidate= false

    private var idProjectAfterUpload: String?=null

    private val createProjectViewModel: CreateProjectViewModel by viewModels {
        ViewModelFactory.getInstance()
    }

    companion object{
        val EXTRA_ID_PROJECT= "EXTRA ID PROJECT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listFreelancerNeededAdapter= ListFreelancerNeededAdapter(taskFreelancer)
        initTinyDB()
        setToolbarTitle(getString(R.string.create_project))
        activatedToolbarBackButton()

        val idProject = intent.getStringExtra(EXTRA_ID_PROJECT)

        binding.run {
            val layoutManager = FlexboxLayoutManager(this@CreateProjectActivity)
            layoutManager.flexDirection = FlexDirection.ROW
            rvSkillsCreateProject.layoutManager = layoutManager
            listSkillsAdapter= ListSkillsAdapter()
            rvSkillsCreateProject.adapter = listSkillsAdapter

            rvFreelancerNeeded.layoutManager= LinearLayoutManager(this@CreateProjectActivity)
            rvFreelancerNeeded.adapter= listFreelancerNeededAdapter

            inputNameProject.doAfterTextChanged {
                validateRequire(it.toString(),txtInputNameProject, getString(R.string.name))
            }

            inputDescriptionProject.doAfterTextChanged {
                validateRequire(it.toString(),txtInputDescriptionProject, getString(R.string.description))
            }

            inputBudgetProject.doAfterTextChanged {
                validateRequire(it.toString(),txtInputBudgetProject, getString(R.string.just_budget))
            }

            inputNumberFreelancer.doAfterTextChanged {
                validateRequire(it.toString(),txtInputFreelancerNeeded, getString(R.string.freelancer_needed))
            }

            inputNumberFreelancer.setOnEditorActionListener { v, actionId, event ->
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    val numberFreelancer = inputNumberFreelancer.text.toString()
                    if(numberFreelancer.trim().isNotEmpty()){
                        if(listFreelancerNeed.isEmpty()){
                            for(i in 0.until(numberFreelancer.toInt())){
                                listFreelancerNeed.add(ItemId(id = UUID.randomUUID().toString()))
                                taskFreelancer.add(Task())
                            }
                            Log.d("hasil_get_create","hasil: ${taskFreelancer.toList()}")
                            listFreelancerNeededAdapter.submitList(listFreelancerNeed)
                            listFreelancerNeededAdapter.notifyDataSetChanged()
                            listFreelancerNeededAdapter.onInputFeeChange={ position: Int, value: String ->
                                taskFreelancer[position].price=value.toInt()
                            }
                            listFreelancerNeededAdapter.onInputTodoChange={ position: Int, todo: MutableList<String> ->
                                taskFreelancer[position].todo=todo
                            }
                        }else{
                            if((numberFreelancer.toInt()-1) > (listFreelancerNeed.size-1)){
                                val oldSize= listFreelancerNeed.size
                                val newSize = (numberFreelancer.toInt()-1) - (listFreelancerNeed.size-1)
                                for(i in 0.until(newSize)){
                                    listFreelancerNeed.add(ItemId(id = UUID.randomUUID().toString()))
                                    taskFreelancer.add(Task())
                                }
                                listFreelancerNeededAdapter.submitList(listFreelancerNeed)
                                listFreelancerNeededAdapter.notifyItemRangeInserted(oldSize,newSize)
                                listFreelancerNeededAdapter.onInputFeeChange={ position: Int, value: String ->
                                    taskFreelancer[position].price=value.toInt()
                                }
                                listFreelancerNeededAdapter.onInputTodoChange={ position: Int, todo: MutableList<String> ->
                                    taskFreelancer[position].todo=todo
                                }
                            }else{
                                val deleteSize = (listFreelancerNeed.size-1) - (numberFreelancer.toInt()-1)
                                for(i in (listFreelancerNeed.size-1) downTo (numberFreelancer.toInt())){
                                    Log.d("hasil_get_need", "index ke $i")
                                    listFreelancerNeed.removeAt(i)
                                    taskFreelancer.removeAt(i)
                                }
                                listFreelancerNeededAdapter.submitList(listFreelancerNeed)
                                listFreelancerNeededAdapter.notifyItemRangeRemoved(numberFreelancer.toInt(), deleteSize)
                                listFreelancerNeededAdapter.onInputFeeChange={ position: Int, value: String ->
                                    taskFreelancer[position].price=value.toInt()
                                }
                                listFreelancerNeededAdapter.onInputTodoChange={ position: Int, todo: MutableList<String> ->
                                    taskFreelancer[position].todo=todo
                                }
                            }
                        }
                        hideSoftKeyboard(this@CreateProjectActivity)
                        return@setOnEditorActionListener true
                    }
                }
                return@setOnEditorActionListener false
            }

            val adapterCategoryProject = ArrayAdapter(
                this@CreateProjectActivity, R.layout.list_item,
                resources.getStringArray(R.array.list_service))
            inputCategoryProject.setAdapter(adapterCategoryProject)

            cpAddSkillCreateProject.setOnClickListener {
                val itemInputSkilView = ItemInputSkillBinding.inflate(layoutInflater)
                itemInputSkilView.addSkill.requestFocus()
                val dialogInputSkill = MaterialAlertDialogBuilder(this@CreateProjectActivity)
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
                    if(actionId== EditorInfo.IME_ACTION_DONE){
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

            createProjectViewModel.responseUpdateIdBoardProject.observe(this@CreateProjectActivity){
                if(it.status){
                    closeProgressDialog()
                    finish()
                }else showErrorSnackbar(it.response.toString())
            }

            createProjectViewModel.responseAddDataBoards.observe(this@CreateProjectActivity){
                if(it.status){
                    idProjectAfterUpload?.let {idProjectAfter->
                        createProjectViewModel.updateIdBoardProject(idProjectAfter, it.response!!)
                    }
                    closeProgressDialog()
                    finish()
                }else showErrorSnackbar(it.response.toString())
            }

            createProjectViewModel.responseAddDataProject.observe(this@CreateProjectActivity){
                if(it.status) {
                    idProjectAfterUpload=it.response
                    createProjectViewModel.addDataBoards(Boards(name = inputNameProject.text.toString(), createdBy = getCurrentUser()?.uid))
                }
                else showErrorSnackbar(it.response.toString())
            }

            btnSubmitCreateProject.setOnClickListener {
//                listFreelancerNeededAdapter.onSubmitClick?.invoke(true)
                if(completeValidate()){
                    binding.run {
                        dataProject.idBusinessMan= getCurrentUser()?.uid
                        dataProject.title= inputNameProject.text.toString()
                        dataProject.description= inputDescriptionProject.text.toString()
                        dataProject.category= inputCategoryProject.text.toString()
                        dataProject.skills = dataSkills
                        if(inputBudgetProject.text.toString().isNotEmpty()){
                            dataProject.budget= inputBudgetProject.text.toString().toInt()
                        }
                        if(inputNumberFreelancer.text.toString().isNotEmpty()){
                            dataProject.num_freelancer= inputNumberFreelancer.text.toString().toInt()
                        }
                        dataProject.tasks=taskFreelancer
                        dataProject.isSaved=false
                        dataProject.statusCompleted=false
                    }
                    Log.d("hasil_get_create", "$dataProject")
                    showProgressDialog(message = getString(R.string.please_wait))
                    createProjectViewModel.addDataProject(dataProject)
                }
            }
        }

        if(idProject!=null){
            createProjectViewModel.responseGetDataProject.observe(this){ project->
                CoroutineScope(Dispatchers.Main).launch{
                    if(project!=null){
                        binding.run {
                            inputNameProject.setText(project.title.toString())
                            inputDescriptionProject.setText(project.description.toString())
                            inputCategoryProject.setText(project.category.toString())
                            listSkillsAdapter?.submitList(project.skills)
                            listSkillsAdapter?.notifyDataSetChanged()
                            inputBudgetProject.setText(project.budget.toString())
                            inputNumberFreelancer.setText(project.num_freelancer.toString())
//                            listFreelancerNeededAdapter.submitList(project.tasks)
                        }
                        closeProgressDialog()

                    }else{
                        showErrorSnackbar(resources.getStringArray(R.array.external_error)[1])
                    }
                }
            }
            showProgressDialog(message = getString(R.string.please_wait))
            createProjectViewModel.getDetailProject(idProject)
        }


        listSkillsAdapter?.onItemClick = {
            dataSkills.removeAt(it)
            updateDataSkills()
        }
    }

    private fun updateDataSkills(){
        listSkillsAdapter?.submitList(dataSkills)
    }

    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager: InputMethodManager = activity.getSystemService(
                INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken,
                0
        )
    }

    private fun completeValidate(): Boolean{
        statusCompleteValidate=true
        var tempStatusComplete: Boolean
        binding.run {
            tempStatusComplete= validateRequire(inputNameProject.text.toString(), txtInputNameProject, getString(R.string.name))
            if(!tempStatusComplete) statusCompleteValidate=false
            tempStatusComplete= validateRequire(inputDescriptionProject.text.toString(), txtInputDescriptionProject, getString(R.string.description))
            if(!tempStatusComplete) statusCompleteValidate=false
            tempStatusComplete= validateRequire(inputBudgetProject.text.toString(), txtInputBudgetProject, getString(R.string.just_budget))
            if(!tempStatusComplete) statusCompleteValidate=false
            tempStatusComplete= validateRequire(inputNumberFreelancer.text.toString(), txtInputFreelancerNeeded, getString(R.string.freelancer_needed))
            if(!tempStatusComplete) statusCompleteValidate=false
            tempStatusComplete= validateRequire(inputCategoryProject.text.toString(), txtInputCategoryProject, getString(R.string.category))
            if(!tempStatusComplete) statusCompleteValidate=false
        }
        Log.d("hasil_get_create","statusCompleteValidate: $statusCompleteValidate")
        return statusCompleteValidate
    }
}