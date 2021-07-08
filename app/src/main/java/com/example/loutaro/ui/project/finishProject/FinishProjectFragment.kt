package com.example.loutaro.ui.project.finishProject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListTitleProjectAdapter
import com.example.loutaro.databinding.FragmentFinishProjectBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FinishProjectFragment : Fragment() {
    private lateinit var binding: FragmentFinishProjectBinding
    private lateinit var listTitleProjectAdapter: ListTitleProjectAdapter
    private val baseActivity= BaseActivity()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentFinishProjectBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity.initTinyDB(requireActivity())
        val finishProjectViewModel = ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(
            FinishProjectViewModel::class.java)
        listTitleProjectAdapter = ListTitleProjectAdapter()
        showProgressFinishProject()
        binding.run {
            rvFinishProject.layoutManager = LinearLayoutManager(requireActivity())
            rvFinishProject.adapter = listTitleProjectAdapter
            baseActivity.setViewToInvisible(rvFinishProject)
            baseActivity.setViewToVisible(includeLayoutFinishProject.layoutProgress)
        }
        finishProjectViewModel.statusGetFinishProject.observe(requireActivity()){ projects->
            listTitleProjectAdapter.submitList(projects)
            listTitleProjectAdapter.notifyDataSetChanged()
            CoroutineScope(Dispatchers.Main).launch {
                if(projects.size>0){
                    showFinishProject()
                }else{
                    showImageNoData()
                }
            }
        }
        if(baseActivity.isUserFreelancer(requireActivity())){
            finishProjectViewModel.getFinishProjectForFreelancer()
        }else if(baseActivity.isUserBusinessMan(requireActivity())){
            finishProjectViewModel.getFinishProjectForBusinessMan()
        }
    }

    private fun showProgressFinishProject(){
        baseActivity.setViewToVisible(binding.includeLayoutFinishProject.layoutProgress)
        baseActivity.setViewToInvisible(binding.rvFinishProject)
        baseActivity.setViewToInvisible(binding.parentLayoutInfoFinishProject)
    }

    private fun showImageNoData(){
        binding.imgSearchInfoFinishProject.setImageResource(R.drawable.ic_undraw_no_data_re_kwbl)
        binding.tvSearchInfoFinishProject.text = getString(R.string.dont_have_finished_project)
        baseActivity.setViewToVisible(binding.parentLayoutInfoFinishProject)
        baseActivity.setViewToInvisible(binding.rvFinishProject)
        baseActivity.setViewToInvisible(binding.includeLayoutFinishProject.layoutProgress)
    }

    private fun showFinishProject(){
        baseActivity.setViewToVisible(binding.rvFinishProject)
        baseActivity.setViewToInvisible(binding.parentLayoutInfoFinishProject)
        baseActivity.setViewToInvisible(binding.includeLayoutFinishProject.layoutProgress)
    }

}