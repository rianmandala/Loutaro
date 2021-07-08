package com.example.loutaro.ui.search

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListFreelancerAdapter
import com.example.loutaro.adapter.ListProjectAdapter
import com.example.loutaro.databinding.FragmentSearchBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var listProjectAdapter: ListProjectAdapter
    private lateinit var listFreelancerAdapter: ListFreelancerAdapter
    private var userType: String=""
    private val baseActivity = BaseActivity()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listProjectAdapter = ListProjectAdapter()
        listFreelancerAdapter = ListFreelancerAdapter()
        baseActivity.initTinyDB(requireActivity())

        showImageInfoIntro()
        val searchProjectViewModel = ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(SearchProjectViewModel::class.java)

        userType = baseActivity.getUserTypeLogin(requireActivity()).toString()
        if(baseActivity.isUserFreelancer(requireActivity())){
            searchProjectViewModel.responseGetSearchProject.observe(requireActivity()){
                if(it.size>0){
                    listProjectAdapter.submitList(it)
                    listProjectAdapter.notifyDataSetChanged()
                    showSearchResult()
                }else{
                    showImageNotFoundInfo()
                }
            }
            binding.run {
                svUser.queryHint=getString(R.string.search_project)
                rvResultSearchProject.layoutManager = LinearLayoutManager(requireActivity())
                rvResultSearchProject.adapter = listProjectAdapter
            }
        } else if(baseActivity.isUserBusinessMan(requireActivity())){
            searchProjectViewModel.responseGetSearchFreelancer.observe(requireActivity()){
                if(it.isNotEmpty()){
                    listFreelancerAdapter.submitList(it)
                    listFreelancerAdapter.notifyDataSetChanged()
                    showSearchResult()
                }else{
                    showImageNotFoundInfo()
                }
            }
            binding.run {
                svUser.queryHint=getString(R.string.search_freelancer)
                rvResultSearchProject.layoutManager = LinearLayoutManager(requireActivity())
                rvResultSearchProject.adapter = listFreelancerAdapter
            }
        }


        binding.run {
            svUser.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(query.toString().trim().isNotEmpty() && query.toString().trim().isNotBlank()){
                        showProgressSearching()
                        if(baseActivity.isUserFreelancer(requireActivity())){
                            searchProjectViewModel.getSearchProject(query.toString())
                        }else if(baseActivity.isUserBusinessMan(requireActivity())){
                            searchProjectViewModel.getSearchFreelance(query.toString())
                        }
                    }else{
                        showImageInfoIntro()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText.toString().trim().isEmpty() && newText.toString().trim().isBlank()) {
                        showImageInfoIntro()
                    }
                    return true
                }

            })
        }
    }

    private fun showProgressSearching(){
        binding.run{
            baseActivity.setViewToVisible(binding.inclueLayoutSearchProject.layoutProgress)
            baseActivity.setViewToInvisible(parentLayoutInfoSearch, rvResultSearchProject)
        }
    }

    private fun showImageInfoIntro(){
        binding.run{
            imgSearchInfo.setImageResource(R.drawable.ic_undraw_file_searching_re_3evy)
            if(baseActivity.isUserFreelancer(requireActivity())){
                tvSearchInfo.text = resources.getString(R.string.let_s_discover_your_next_projects)
            }else if(baseActivity.isUserBusinessMan(requireActivity())){
                tvSearchInfo.text = resources.getString(R.string.let_s_discover_your_next_freelancer)
            }
            baseActivity.setViewToVisible(parentLayoutInfoSearch)
            baseActivity.setViewToInvisible(inclueLayoutSearchProject.layoutProgress, rvResultSearchProject)
        }
    }

    private fun showImageNotFoundInfo(){
        showImageInfoIntro()
        binding.run {
            imgSearchInfo.setImageResource(R.drawable.ic_undraw_no_data_re_kwbl)
            if(baseActivity.isUserFreelancer(requireActivity())){
                tvSearchInfo.text = resources.getString(R.string.search_result_not_found_project)
            }else if(baseActivity.isUserBusinessMan(requireActivity())){
                tvSearchInfo.text = resources.getString(R.string.search_result_not_found_freelancer)
            }
        }
    }

    private fun showSearchResult(){
        binding.run{
            baseActivity.setViewToVisible(rvResultSearchProject)
            baseActivity.setViewToInvisible(inclueLayoutSearchProject.layoutProgress, parentLayoutInfoSearch)
        }
    }

}