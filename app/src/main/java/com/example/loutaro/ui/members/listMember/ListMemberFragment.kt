package com.example.loutaro.ui.members.listMember

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListMemberAdapter
import com.example.loutaro.databinding.FragmentListMemberBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ListMemberFragment : Fragment() {
    private lateinit var binding: FragmentListMemberBinding
    private lateinit var listMemberViewModel: ListMemberViewModel
    private lateinit var listMemberAdapter: ListMemberAdapter
    private val baseActivity= BaseActivity()
    // newInstance constructor for creating fragment with arguments
    companion object{
        fun newInstance(idBoards: String?, idProject: String): ListMemberFragment {
            val fragmentFirst = ListMemberFragment()
            val args = Bundle()
            args.putString("idBoards", idBoards)
            args.putString("idProject", idProject)
            fragmentFirst.arguments = args
            return fragmentFirst
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listMemberAdapter = ListMemberAdapter()
        binding= FragmentListMemberBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idBoards = arguments?.getString("idBoards");
        val idProject = arguments?.getString("idProject")

        binding.run {
            rvListMember.layoutManager = LinearLayoutManager(requireActivity())
            rvListMember.adapter = listMemberAdapter
        }

        listMemberAdapter.onClickCallbak={idMember->
            MaterialAlertDialogBuilder(requireActivity())
                    .setMessage(getString(R.string.are_you_sure_want_to_remove_this_member))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                listMemberViewModel.deleteMemberFromBoard(idBoards.toString(), idMember).await()
                                if (idProject != null) {
                                    listMemberViewModel.deleteMemberFromProject(idProject, idMember).await()
                                }
                            }catch (e: Exception){
                                Log.e("error","Error when try to delete list card")
                            }
                        }
                    }
                    .show()
        }

        listMemberViewModel = ViewModelProvider(this, ViewModelFactory.getInstance()).get(
            ListMemberViewModel::class.java
        )

        if (idBoards != null) {
            listMemberViewModel.responseGetListMember.observe(requireActivity()){ listFreelancer->
                if(listFreelancer.size>0){
                    binding.run {
                        showListMember()
                        listMemberAdapter.submitList(listFreelancer)
                        listMemberAdapter.notifyDataSetChanged()

                    }
                }else{
                    showInfoNoData()
                }
            }

            listMemberViewModel.responseGetDetailBoards.observe(requireActivity()){
                if(it.members!=null && it.members!!.isNotEmpty()){
                    showListMember()
                    listMemberViewModel.getListMember(idBoards, it.members!!)
                }else{
                    showInfoNoData()
                }
            }

            listMemberViewModel.getDetailBoards(idBoards)
        }
    }

    private fun showListMember(){
        binding.run {
            baseActivity.setViewToVisible(rvListMember)
            baseActivity.setViewToInvisible(parentLayoutInfoListMember)
        }
    }

    private fun showInfoNoData(){
        binding.run {
            baseActivity.setViewToVisible(parentLayoutInfoListMember)
            baseActivity.setViewToInvisible(rvListMember)
        }
    }

}