package com.example.loutaro.ui.createProject

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.example.loutaro.data.dummy.Categori
import com.example.loutaro.data.entity.*
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
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    private var firstLoading=true

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
        activatedToolbarBackButton()

        val idProject = intent.getStringExtra(EXTRA_ID_PROJECT)
        Log.d("idProject","$idProject")
        if(idProject==null){
            setToolbarTitle(getString(R.string.create_project))
            binding.btnSubmitCreateProject.text= getString(R.string.submit)
        }else{
            binding.btnSubmitCreateProject.text= getString(R.string.update_project)
            setToolbarTitle(getString(R.string.update_project))
        }
        binding.run {
            val layoutManager = FlexboxLayoutManager(this@CreateProjectActivity)
            layoutManager.flexDirection = FlexDirection.ROW
            rvSkillsCreateProject.layoutManager = layoutManager
            listSkillsAdapter= ListSkillsAdapter()
            rvSkillsCreateProject.adapter = listSkillsAdapter

            rvFreelancerNeeded.layoutManager= LinearLayoutManager(this@CreateProjectActivity)
            rvFreelancerNeeded.adapter= listFreelancerNeededAdapter

            inputCategoryProject.addTextChangedListener(object : TextWatcher {
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

            inputDurationInDaysProject.doAfterTextChanged {
                validateRequire(it.toString(),txtInputDurationInDaysProject, getString(R.string.duration_in_days))
            }

            listFreelancerNeededAdapter.onInputFeeChange={ position: Int, value: String ->
                taskFreelancer[position].price=value.toInt()

            }
            listFreelancerNeededAdapter.onInputTodoChange={ position: Int, todo: MutableList<String> ->
                taskFreelancer[position].todo=todo
            }
            listFreelancerNeededAdapter.onDeleteTodoCallback={ index: Int, todo: MutableList<String> ->
                taskFreelancer[index].todo=todo
                Log.d("hasil_get_need","ini kepanggil delete, hasilnya: ${taskFreelancer.toList()}")
            }

            inputNumberFreelancer.setOnEditorActionListener { v, actionId, event ->
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    val numberFreelancer = inputNumberFreelancer.text.toString()
                    if(numberFreelancer.trim().isNotEmpty()){
                        if(listFreelancerNeed.isEmpty()){
                            for(i in 0.until(numberFreelancer.toInt())){
                                listFreelancerNeed.add(ItemId(
                                    id = UUID.randomUUID().toString()))
                                taskFreelancer.add(Task())
                            }
                            Log.d("hasil_get_create","hasil: ${taskFreelancer.toList()}")
                            listFreelancerNeededAdapter.submitList(listFreelancerNeed)
                            listFreelancerNeededAdapter.notifyDataSetChanged()
                            listFreelancerNeededAdapter.onInputFeeChange={ position: Int, value: String ->
                                var budgetTotal=0
                                taskFreelancer[position].price=value.toInt()
                                taskFreelancer.forEach {task->
                                    if(task.price!=null){
                                        budgetTotal+= task.price!!
                                    }
                                }
                                inputBudgetProject.setText(budgetTotal.toString())
                                Log.d("fee_freelancer","fee 1: $value")
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
                                    var budgetTotal=0
                                    taskFreelancer[position].price=value.toInt()
                                    Log.d("fee_freelancer","fee 2: $value")
                                    taskFreelancer.forEach {task->
                                        if(task.price!=null){
                                            budgetTotal+= task.price!!
                                        }
                                    }
                                    inputBudgetProject.setText(budgetTotal.toString())
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
                                    var budgetTotal=0
                                    taskFreelancer[position].price=value.toInt()
                                    taskFreelancer.forEach {task->
                                        if(task.price!=null){
                                            budgetTotal+= task.price!!
                                        }
                                    }
                                    inputBudgetProject.setText(budgetTotal.toString())
                                    Log.d("fee_freelancer","fee3 : $value")

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

            inputCategoryProject.setText(resources.getStringArray(R.array.list_service)[0],false)

            firstLoading=false
            binding.cpAddSkillCreateProject.setOnClickListener {
                var originalSkill= when(binding.inputCategoryProject.text.toString()){
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
                val dialogInputSkill = MaterialAlertDialogBuilder(this@CreateProjectActivity)
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
                    val listBoardsCard= mutableListOf<BoardsCard?>()
                    if(dataProject.tasks!=null){
                        for(project in dataProject.tasks!!){
                            if(project.todo!=null){
                                for(todo in project.todo!!){
                                    listBoardsCard.add(
                                            BoardsCard(name = todo)
                                    )
                                }
                            }
                        }
                    }

                    createProjectViewModel.addDataBoards(
                            Boards(
                                    name = inputNameProject.text.toString(),
                                    columns = mutableListOf(
                                            BoardsColumn(
                                                    name = "Todo",
                                                    cards = listBoardsCard
                                            ),
                                            BoardsColumn(
                                                    name = "Doing"
                                            ),
                                            BoardsColumn(
                                                    name = "Review"
                                            ),
                                            BoardsColumn(
                                                    name = "Done"
                                            )
                                    ),
                                    createdBy = getCurrentUser()?.uid
                            )
                    )
                }
                else showErrorSnackbar(it.response.toString())
            }



            btnSubmitCreateProject.setOnClickListener {
                listFreelancerNeededAdapter.onSubmitClick?.invoke(true)
                if(completeValidate()){
                    var statusFee=true
                    var statusTask=true
                    Log.d("taskFreelancer","ini dia $taskFreelancer")
                    taskFreelancer.forEach {
                        if(it.price==null){
                            statusFee=false
                        }
                        if(it.todo==null){
                            statusTask=false
                        }
                        it.todo?.forEach { item->
                            if(item==null || item==""){
                                statusTask=false
                            }
                        }
                    }
                    if(!statusFee && !statusTask){
                        showWarningSnackbar(message = getString(R.string.required,getString(R.string.fee_and_todo)))
                    }
                    else if(!statusFee){
                        showWarningSnackbar(message = getString(R.string.fee_is_required_and_must_greather_than_zero,getString(R.string.fee)))
                    }else if(!statusTask){
                        showWarningSnackbar(message = getString(R.string.required,getString(R.string.task)))
                    }else{
                        binding.run {
                            dataProject.idBusinessMan= getCurrentUser()?.uid
                            dataProject.title= inputNameProject.text.toString()
                            dataProject.description= inputDescriptionProject.text.toString()
                            dataProject.category= inputCategoryProject.text.toString()
                            dataProject.durationInDays = inputDurationInDaysProject.text.toString().toInt()
                            dataProject.skills = dataSkills
                            if(inputBudgetProject.text.toString().isNotEmpty()){
                                dataProject.budget= inputBudgetProject.text.toString().toInt()
                            }
                            if(inputNumberFreelancer.text.toString().isNotEmpty()){
                                dataProject.num_freelancer= inputNumberFreelancer.text.toString().toInt()
                            }
                            dataProject.tasks=taskFreelancer
                            dataProject.statusCompleted=false
                        }
                        Log.d("hasil_get_create", "$dataProject")
                        showProgressDialog(message = getString(R.string.please_wait))
                        if(idProject!=null){
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    createProjectViewModel.updateProject(idProject, dataProject).await()
                                    Log.d("hasil_get_create", "berhasil masih ke update $idProject")
                                    withContext(Dispatchers.Main){
                                        closeProgressDialog()
                                        finish()
                                    }
                                }catch (e: Exception){
                                    Log.e("error_update","Error when try to update project")
                                }
                            }
                        }else{
                            createProjectViewModel.addDataProject(dataProject)
                        }
                    }

                }else{
                    showWarningSnackbar(message = getString(R.string.still_have_data_not_fill))
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
                            inputDurationInDaysProject.setText(project.durationInDays.toString())
                            inputCategoryProject.setText(project.category.toString(), false)
                            dataSkills= project.skills as MutableList<String>
                            updateDataSkills()
                            inputBudgetProject.setText(project.budget.toString())
                            inputNumberFreelancer.setText(project.num_freelancer.toString())

                            if(project.tasks!=null){
                                for(task in project.tasks!!){
                                    listFreelancerNeed.add(ItemId(
                                        id = UUID.randomUUID().toString(),
                                        applyers = task.applyers,
                                        price = task.price,
                                        todo = task.todo
                                    ))
                                    taskFreelancer.add(Task(
                                        applyers = task.applyers,
                                        price = task.price,
                                        todo = task.todo
                                    ))
                                }
                                listFreelancerNeededAdapter.submitList(listFreelancerNeed)
                            }
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
        binding.run {
            if(dataSkills.isEmpty()){
                setViewToVisible(tvErrorSkill)
                tvErrorSkill.text = getString(R.string.required,"Skill")
            }else{
                setViewToGone(tvErrorSkill)
            }
        }
        listSkillsAdapter?.submitList(dataSkills)
        listSkillsAdapter?.notifyDataSetChanged()
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
            tempStatusComplete= validateRequire(inputDurationInDaysProject.text.toString(), txtInputDurationInDaysProject, getString(R.string.duration_in_days))
            if(!tempStatusComplete) statusCompleteValidate=false
            if(dataSkills.isEmpty()){
                setViewToVisible(tvErrorSkill)
                tvErrorSkill.text = getString(R.string.required,"Skill")
                statusCompleteValidate=false
            }else{
                setViewToGone(tvErrorSkill)
            }
        }
        Log.d("hasil_get_create","statusCompleteValidate: $statusCompleteValidate")
        return statusCompleteValidate
    }
}