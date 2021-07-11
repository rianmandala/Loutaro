package com.example.loutaro.ui.project.activeProject

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListTitleProjectAdapter
import com.example.loutaro.databinding.FragmentActiveProjectBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.createProject.CreateProjectActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ActiveProjectFragment : Fragment() {
    private lateinit var binding: FragmentActiveProjectBinding
    private lateinit var listTitleProjectAdapter: ListTitleProjectAdapter
    private val baseActivity= BaseActivity()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentActiveProjectBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity.initTinyDB(requireActivity())
        val activeProjectViewModel = ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(ActiveProjectViewModel::class.java)
        showProgressActiveProject()

        activeProjectViewModel.statusGetActiveProject.observe(requireActivity()){ projects->
            Log.d("hasil_get_project","${projects.toList()}")
            if(projects.isNotEmpty()){
                listTitleProjectAdapter.submitList(projects)
                listTitleProjectAdapter.notifyDataSetChanged()
                CoroutineScope(Dispatchers.Main).launch {
                    showActiveProject()
                }
            }else{
                showImageNoData()
            }
        }
        if(baseActivity.isUserFreelancer(requireActivity())){
            binding.run {
                listTitleProjectAdapter = ListTitleProjectAdapter(false)
                rvActiveProject.layoutManager = LinearLayoutManager(requireActivity())
                rvActiveProject.adapter = listTitleProjectAdapter
            }
            activeProjectViewModel.getActiveProjectForFreelancer()
        }else if(baseActivity.isUserBusinessMan(requireActivity())){
            binding.run {
                listTitleProjectAdapter = ListTitleProjectAdapter(true)
                rvActiveProject.layoutManager = LinearLayoutManager(requireActivity())
                rvActiveProject.adapter = listTitleProjectAdapter
            }
            baseActivity.setViewToVisible(binding.btnAddProject)
            binding.btnAddProject.setOnClickListener{
                baseActivity.navigateForward(CreateProjectActivity::class.java, requireActivity())
            }
            activeProjectViewModel.getActiveProjectForBusinessMan()
        }

        listTitleProjectAdapter.onClickDeleteProjectCallback={ idProject->
            MaterialAlertDialogBuilder(requireActivity())
                .setMessage(getString(R.string.are_you_sure_want_to_detele))
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                    baseActivity.showProgressDialog(message = getString(R.string.please_wait), context = requireActivity())
                    CoroutineScope(Dispatchers.IO).launch {
                        activeProjectViewModel.deleteProject(idProject).await()
                        withContext(Dispatchers.Main){
                            baseActivity.closeProgressDialog()
                        }
                    }
                }
                .show()
        }

    }

    private fun showProgressActiveProject(){
        baseActivity.setViewToVisible(binding.includeLayoutActiveProject.layoutProgress)
        baseActivity.setViewToInvisible(binding.rvActiveProject)
        baseActivity.setViewToInvisible(binding.parentLayoutInfoActiveProject)
    }

    private fun showImageNoData(){
        binding.imgSearchInfoActiveProject.setImageResource(R.drawable.ic_undraw_no_data_re_kwbl)
        binding.tvSearchInfoActiveProject.text = getString(R.string.dont_have_active_project)
        baseActivity.setViewToVisible(binding.parentLayoutInfoActiveProject)
        baseActivity.setViewToInvisible(binding.rvActiveProject)
        baseActivity.setViewToInvisible(binding.includeLayoutActiveProject.layoutProgress)
    }

    private fun showActiveProject(){
        baseActivity.setViewToVisible(binding.rvActiveProject)
        baseActivity.setViewToInvisible(binding.parentLayoutInfoActiveProject)
        baseActivity.setViewToInvisible(binding.includeLayoutActiveProject.layoutProgress)
    }

}