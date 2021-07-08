package com.example.loutaro.ui.projectDetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListStandardSkillAdapter
import com.example.loutaro.adapter.ListTaskPriceAdapter
import com.example.loutaro.databinding.ActivityProjectDetailBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityProjectDetailBinding
    private lateinit var listStandardSkillAdapter: ListStandardSkillAdapter
    private lateinit var listTaskPriceAdapter: ListTaskPriceAdapter
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
        listTaskPriceAdapter = ListTaskPriceAdapter()
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

        projectDetailViewModel.statusGetDetailProject.observe(this){ project->
                if(project!=null){
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