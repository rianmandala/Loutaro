package com.example.loutaro.ui.project.pendingProject

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
import com.example.loutaro.databinding.FragmentPendingProjectBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.createProject.CreateProjectActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class PendingProjectFragment : Fragment() {
    private lateinit var binding: FragmentPendingProjectBinding
    private lateinit var listTitleProjectAdapter: ListTitleProjectAdapter
    private val baseActivity= BaseActivity()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPendingProjectBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pendingProjectViewModel = ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(PendingProjectViewModel::class.java)

        showProgressActiveProject()

        pendingProjectViewModel.responseGetPendingProject.observe(requireActivity()){ projects->
            Log.d("hasil_get_project","pending project ${projects.toList()}")
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

        binding.run {
            listTitleProjectAdapter = ListTitleProjectAdapter(paymentProject = true)
            rvPendingProject.layoutManager = LinearLayoutManager(requireActivity())
            rvPendingProject.adapter = listTitleProjectAdapter
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
                        pendingProjectViewModel.deleteProject(idProject).await()
                        withContext(Dispatchers.Main){
                            baseActivity.closeProgressDialog()
                            baseActivity.showSnackbar(message = getString(R.string.project_succes_to_delete), activity = requireActivity())

                        }
                    }
                }
                .show()
        }

        pendingProjectViewModel.getPendingProjectForBusinessMan()
    }

    private fun showProgressActiveProject(){
        baseActivity.setViewToVisible(binding.includeLayoutPendingProject.layoutProgress)
        baseActivity.setViewToInvisible(binding.rvPendingProject)
        baseActivity.setViewToInvisible(binding.parentLayoutInfoPendingProject)
    }

    private fun showImageNoData(){
        binding.imgSearchInfoPendingProject.setImageResource(R.drawable.ic_undraw_no_data_re_kwbl)
        binding.tvSearchInfoPendingProject.text = getString(R.string.dont_have_pending_project)
        baseActivity.setViewToVisible(binding.parentLayoutInfoPendingProject)
        baseActivity.setViewToInvisible(binding.rvPendingProject)
        baseActivity.setViewToInvisible(binding.includeLayoutPendingProject.layoutProgress)
    }

    private fun showActiveProject(){
        baseActivity.setViewToVisible(binding.rvPendingProject)
        baseActivity.setViewToInvisible(binding.parentLayoutInfoPendingProject)
        baseActivity.setViewToInvisible(binding.includeLayoutPendingProject.layoutProgress)
    }

}