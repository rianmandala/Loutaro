package com.example.loutaro.ui.projectDetail

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListStandardSkillAdapter
import com.example.loutaro.adapter.ListTaskPriceAdapter
import com.example.loutaro.data.entity.Project
import com.example.loutaro.databinding.ActivityProjectDetailBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class ProjectDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityProjectDetailBinding
    private lateinit var listStandardSkillAdapter: ListStandardSkillAdapter
    private lateinit var listTaskPriceAdapter: ListTaskPriceAdapter
    private var detailProjectGlobal= Project()
    private var isSaved=false
    private val projectDetailViewModel: ProjectDetailViewModel by viewModels {
        ViewModelFactory.getInstance()
    }
    companion object{
        val EXTRA_ID_PROJECT= "EXTRA ID PROJECT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initTinyDB(this)

        listStandardSkillAdapter = ListStandardSkillAdapter()
        listTaskPriceAdapter = ListTaskPriceAdapter(getCurrentUser()?.uid.toString())
        setToolbarTitle(getString(R.string.detail_project))
        activatedToolbarBackButton()
        val idProject = intent.getStringExtra(EXTRA_ID_PROJECT)
        Log.d("idProject","ini dia $idProject")
        binding.run {
            setViewToInvisible(parentLayoutDetailProject)
            setViewToVisible(includeLayoutProjectDetail.layoutProgress)
            setViewToInvisible(btnAddToFavoriteProject)
            btnAddToFavoriteProject.setOnClickListener {
                isSaved = !isSaved
                projectDetailViewModel.updateSaveProject(idProject.toString(),isSaved)
                checkSaveProject(isSaved)
            }
        }

        listTaskPriceAdapter.onClickCallback={ position->
            if(isUserFreelancer()){
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if(detailProjectGlobal.tasks?.get(position)?.applyers==null){
                            detailProjectGlobal.tasks?.get(position)?.applyers= mutableListOf()
                        }
                        val isApplyBefore = detailProjectGlobal.tasks?.get(position)?.applyers?.contains(getCurrentUser()?.uid)
                        Log.d("hasil_index","index isApplyBefore $isApplyBefore")
                        if(isApplyBefore == false){
                            detailProjectGlobal.tasks?.get(position)?.applyers?.add(getCurrentUser()?.uid.toString())
                            projectDetailViewModel.applyAsFreelancerToProject(idProject.toString(), detailProjectGlobal).await()
                            withContext(Dispatchers.Main){
                                showSnackbar(message = getString(R.string.success_apples_as_freelancer, position+1))
                                listTaskPriceAdapter.onApplyCallback?.invoke(true,position)
                            }
                        }else{
                            detailProjectGlobal.tasks?.get(position)?.applyers?.remove(getCurrentUser()?.uid.toString())
                            projectDetailViewModel.applyAsFreelancerToProject(idProject.toString(), detailProjectGlobal).await()
                            withContext(Dispatchers.Main){
                                showSnackbar(message = getString(R.string.cancel_apply_as_freelancer, position+1))
                                listTaskPriceAdapter.onApplyCallback?.invoke(false,position)
                            }
                        }
                    }catch (e: Exception){
                        Log.e("error","Error when try to apply freelancer to project")
                    }
                }
            }else if(isUserBusinessMan()){
                showWarningSnackbar(message = getString(R.string.only_freelancer_can_apply_project))
            }
        }

        projectDetailViewModel.statusGetDetailProject.observe(this){ project->
                if(project!=null){
                    detailProjectGlobal= project
                    CoroutineScope(Dispatchers.Main).launch{
                        binding.run{
                            setViewToVisible(binding.parentLayoutDetailProject)
                            setViewToInvisible(binding.includeLayoutProjectDetail.layoutProgress)
                            tvTitleDetailProject.text = project.title
                            tvTimePostedDetailProject.text = getString(R.string.posted_time, getRelativeTimeFromNow(project.timeStamp!!.toDate().time))
                            tvCategoryDetailProject.text = project.category
                            tvDescriptionDetailProject.text = project.description
                            tvDescriptionDetailProject.text = tvDescriptionDetailProject.text.toString().replace("\\r\\n","\r\n")

                            val layoutManager = FlexboxLayoutManager(this@ProjectDetailActivity)
                            layoutManager.flexDirection = FlexDirection.ROW
                            rvStandardSkillDetailProject.layoutManager = layoutManager
                            rvStandardSkillDetailProject.adapter = listStandardSkillAdapter
                            listStandardSkillAdapter.submitList(project.skills)

                            rvTaskPrice.layoutManager = LinearLayoutManager(this@ProjectDetailActivity)
                            rvTaskPrice.adapter = listTaskPriceAdapter
                            listTaskPriceAdapter.submitList(project.tasks)


                            if(project.isSaved!=null) {
                                isSaved = project.isSaved!!
                                checkSaveProject(isSaved)
                            }
                            else checkSaveProject(false)
                            setViewToVisible(btnAddToFavoriteProject)
                        }
                    }
                }
        }
        projectDetailViewModel.getDetailProject(idProject.toString())
    }

    fun checkSaveProject(isSaved: Boolean){
        if(isSaved){
            binding.btnAddToFavoriteProject.setImageResource(R.drawable.ic_baseline_favorite_24)
        }else{
            binding.btnAddToFavoriteProject.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }
}