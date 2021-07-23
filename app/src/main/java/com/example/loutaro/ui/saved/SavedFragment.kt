package com.example.loutaro.ui.saved

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListFreelancerAdapter
import com.example.loutaro.adapter.ListProjectAdapter
import com.example.loutaro.databinding.FragmentSavedBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory

class SavedFragment : Fragment() {
    private lateinit var binding: FragmentSavedBinding
    private lateinit var listProjectAdapter: ListProjectAdapter
    private lateinit var listFreelancerAdapter: ListFreelancerAdapter
    private lateinit var savedProjectViewModel: SavedProjectViewModel
    private val baseActivity = BaseActivity()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedProjectViewModel = ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(SavedProjectViewModel::class.java)
        baseActivity.initTinyDB(requireActivity())
        baseActivity.setViewToInvisible(binding.rvSaveUser)
        baseActivity.setViewToInvisible(binding.parentLayoutInfoSavedProject)
        baseActivity.setViewToVisible(binding.includeLayoutSavedProject.layoutProgress)

    }

    override fun onResume() {
        super.onResume()
        Log.d("saved_fragment","ini di resume")
        if(baseActivity.getUserTypeLogin(requireActivity())==getString(R.string.value_freelancer)){
            binding.run{
                rvSaveUser.layoutManager = LinearLayoutManager(requireActivity())
                listProjectAdapter = ListProjectAdapter()
                rvSaveUser.adapter = listProjectAdapter

                savedProjectViewModel.statusGetSaveDataProjects.observe(requireActivity()){ projects->
                    if(projects!=null){
                        listProjectAdapter.submitList(projects)
                        listProjectAdapter.notifyDataSetChanged()
                        if(projects.isEmpty()){
                            tvSearchInfoSavedProject.text = getString(R.string.dont_have_saved_project_yet)
                            baseActivity.setViewToVisible(parentLayoutInfoSavedProject)
                            baseActivity.setViewToInvisible(includeLayoutSavedProject.layoutProgress, rvSaveUser)
                        }else{
                            baseActivity.setViewToVisible(rvSaveUser)
                            baseActivity.setViewToInvisible(parentLayoutInfoSavedProject, includeLayoutSavedProject.layoutProgress)
                        }
                    }else{
                        tvSearchInfoSavedProject.text = getString(R.string.dont_have_saved_project_yet)
                        baseActivity.setViewToVisible(parentLayoutInfoSavedProject)
                        baseActivity.setViewToInvisible(includeLayoutSavedProject.layoutProgress, rvSaveUser)
                    }

                }
                val listProjectIdSaved = baseActivity.getListIdProjectSaved(baseActivity.getCurrentUser()?.uid.toString())
                if(listProjectIdSaved!=null){
                    if(listProjectIdSaved.isNotEmpty()){
                        savedProjectViewModel.getDataSaveProjects(listProjectIdSaved)
                    }else{
                        tvSearchInfoSavedProject.text = getString(R.string.dont_have_saved_project_yet)
                        baseActivity.setViewToVisible(parentLayoutInfoSavedProject)
                        baseActivity.setViewToInvisible(includeLayoutSavedProject.layoutProgress, rvSaveUser)
                    }
                }else{
                    tvSearchInfoSavedProject.text = getString(R.string.dont_have_saved_project_yet)
                    baseActivity.setViewToVisible(parentLayoutInfoSavedProject)
                    baseActivity.setViewToInvisible(includeLayoutSavedProject.layoutProgress, rvSaveUser)
                }
            }

        } else if(baseActivity.getUserTypeLogin(requireActivity())==getString(R.string.value_business_man)){
            binding.run{
                rvSaveUser.layoutManager = LinearLayoutManager(requireActivity())
                listFreelancerAdapter = ListFreelancerAdapter()
                rvSaveUser.adapter = listFreelancerAdapter

                savedProjectViewModel.statusGetSaveDataFreelancer.observe(requireActivity()){ freelancer->
                    if(freelancer!=null){
                        listFreelancerAdapter.submitList(freelancer)
                        listFreelancerAdapter.notifyDataSetChanged()
                        if(freelancer.isEmpty()){
                            tvSearchInfoSavedProject.text = getString(R.string.dont_have_saved_freelancer_yet)
                            baseActivity.setViewToVisible(parentLayoutInfoSavedProject)
                            baseActivity.setViewToInvisible(includeLayoutSavedProject.layoutProgress, rvSaveUser)
                        }else{
                            baseActivity.setViewToVisible(rvSaveUser)
                            baseActivity.setViewToInvisible(includeLayoutSavedProject.layoutProgress, parentLayoutInfoSavedProject)
                        }
                    }else{
                        tvSearchInfoSavedProject.text = getString(R.string.dont_have_saved_freelancer_yet)
                        baseActivity.setViewToVisible(parentLayoutInfoSavedProject)
                        baseActivity.setViewToInvisible(includeLayoutSavedProject.layoutProgress, rvSaveUser)
                    }

                }
                val listFreelancerIdSaved = baseActivity.getListIdFreelancerSaved(baseActivity.getCurrentUser()?.uid.toString())
                Log.d("saved","ini dia ${listFreelancerIdSaved}")
                if(listFreelancerIdSaved!=null){
                    if(listFreelancerIdSaved.isNotEmpty()){
                        savedProjectViewModel.getDataSaveFreelancer(listFreelancerIdSaved)
                    }else{
                        tvSearchInfoSavedProject.text = getString(R.string.dont_have_saved_freelancer_yet)
                        baseActivity.setViewToVisible(parentLayoutInfoSavedProject)
                        baseActivity.setViewToInvisible(includeLayoutSavedProject.layoutProgress, rvSaveUser)
                    }
                }else{
                    tvSearchInfoSavedProject.text = getString(R.string.dont_have_saved_freelancer_yet)
                    baseActivity.setViewToVisible(parentLayoutInfoSavedProject)
                    baseActivity.setViewToInvisible(includeLayoutSavedProject.layoutProgress, rvSaveUser)
                }
            }
        }
    }
}