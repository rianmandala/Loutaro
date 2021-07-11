package com.example.loutaro.ui.members.addMember

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListSearchMemberSuggestionAdapter
import com.example.loutaro.adapter.ListSkillsAdapter
import com.example.loutaro.data.entity.Boards
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.databinding.FragmentAddMemberBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class AddMemberFragment : Fragment() {
    private lateinit var binding: FragmentAddMemberBinding
    private lateinit var listSearchMemberSuggestionAdapter: ListSearchMemberSuggestionAdapter
    private lateinit var addMemberViewModel: AddMemberViewModel
    private var detailBoards = Boards()
    private var listFreelancer = mutableListOf<Freelancer>()
    private var listMember= mutableListOf<String>()
    private var listIdMember= mutableListOf<String>()
    private lateinit var listSkillsAdapter: ListSkillsAdapter
    private val baseActivity= BaseActivity()
    companion object{
        fun newInstance(idBoards: String?, idProject: String): AddMemberFragment {
            val addMemberFragment = AddMemberFragment()
            val args = Bundle()
            args.putString("idBoards", idBoards)
            args.putString("idProject", idProject)
            addMemberFragment.arguments = args
            return addMemberFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listSearchMemberSuggestionAdapter= ListSearchMemberSuggestionAdapter()
        binding = FragmentAddMemberBinding.inflate(inflater)
        addMemberViewModel= ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(AddMemberViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idBoards = arguments?.getString("idBoards")
        val idProject = arguments?.getString("idProject")
        hideSearchSuggestion()
        baseActivity.initTinyDB(requireActivity())
        binding.run {
            rvSearchMemberSuggestion.layoutManager = LinearLayoutManager(requireActivity())
            rvSearchMemberSuggestion.adapter = listSearchMemberSuggestionAdapter

            val layoutManager = FlexboxLayoutManager(requireActivity())
            layoutManager.flexDirection = FlexDirection.ROW
            rvSkillsCreateProject.layoutManager = layoutManager
            listSkillsAdapter= ListSkillsAdapter()
            rvSkillsCreateProject.adapter = listSkillsAdapter

            btnAddNewMember.setOnClickListener {
                if(baseActivity.isUserBusinessMan(requireActivity())){
                    if (idBoards != null) {
                        baseActivity.showProgressDialog(message = getString(R.string.please_wait),context = requireActivity())
                        CoroutineScope(Dispatchers.IO).launch {
                            for (idMember in listIdMember){
                                addMemberViewModel.addMemberToBoards(idBoards, idMember).await()
                                if (idProject != null) {
                                    addMemberViewModel.addMemberToProject(idProject, idMember).await()
                                }
                            }
                            withContext(Dispatchers.Main){
                                baseActivity.closeProgressDialog()
                                listMember= mutableListOf()
                                listIdMember= mutableListOf()
                                updateDataSkills()
                                baseActivity.showSnackbar(getString(R.string.member_add_succes), requireActivity())
                            }
                        }
                    }
                } else if(baseActivity.isUserFreelancer(requireActivity())){
                    baseActivity.showErrorSnackbar(getString(R.string.only_admin_can_add_member), requireActivity())
                }
            }
        }

        addMemberViewModel.responseGetDetailBoards.observe(requireActivity()){
            if(it!=null){
                detailBoards= it
            }
        }

        if (idBoards != null) {
            addMemberViewModel.getDetailBoards(idBoards)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = addMemberViewModel.getAllFreelancer().get().await()
                val data = mutableListOf<Freelancer>()
                for (document in response.documents){
                    val dataConvert = document.toObject(Freelancer::class.java)
                    dataConvert?.idFreelancer= document.id
                    if(dataConvert!=null){
                        data.add(dataConvert)
                    }
                }
                listFreelancer= data

                withContext(Dispatchers.Main){
                    binding.svMember.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            hideKeyboard()
                            return false
                        }

                        override fun onQueryTextChange(query: String?): Boolean {
                            var filterFreelancer = listOf<Freelancer>()
                            if(query?.trim()?.length!! > 2){
                                if(detailBoards.members!=null){
                                    filterFreelancer = listFreelancer.filter {
                                        it.name?.toUpperCase()?.contains(query.toString().toUpperCase()) == true &&
                                                !listIdMember.contains(it.idFreelancer) &&
                                                detailBoards.members?.contains(it.idFreelancer) == false
                                    }
                                }else{
                                    filterFreelancer = listFreelancer.filter {
                                        it.name?.toUpperCase()?.contains(query.toString().toUpperCase()) == true &&
                                                !listIdMember.contains(it.idFreelancer)
                                    }
                                }
                                Log.d("hasil_member","filter freelancer ${filterFreelancer.toList()}")
                                Log.d("hasil_member","list freelancer ${listFreelancer.toList()}")
                                if(filterFreelancer.size>0){
                                    showSearchSuggestion()
                                }else{
                                    hideSearchSuggestion()
                                }
                                listSearchMemberSuggestionAdapter.submitList(filterFreelancer)
                                listSearchMemberSuggestionAdapter.notifyDataSetChanged()
                            }else{
                                hideSearchSuggestion()
                                listSearchMemberSuggestionAdapter.submitList(mutableListOf<Freelancer>())
                                listSearchMemberSuggestionAdapter.notifyDataSetChanged()
                            }
                            return true
                        }
                    })
                }
            }catch (e: Exception){
                Log.e("error", "Error when try to get list freelancer")
            }

        }

        listSearchMemberSuggestionAdapter.onClickCallback={ freelancer ->
            listMember.add(freelancer.email.toString())
            freelancer.idFreelancer?.let { listIdMember.add(it) }
            binding.svMember.setQuery("", false);
            binding.svMember.clearFocus();
            listSearchMemberSuggestionAdapter.submitList(mutableListOf<Freelancer>())
            listSearchMemberSuggestionAdapter.notifyDataSetChanged()
            hideSearchSuggestion()
            updateDataSkills()
            hideKeyboard()
        }

        listSkillsAdapter.onItemClick = {
            listMember.removeAt(it)
            listIdMember.removeAt(it)
            updateDataSkills()
            hideKeyboard()
        }


    }

    private fun showSearchSuggestion(){
        binding.run {
            baseActivity.setViewToVisible(cvSearchMemberSuggestion)
        }
    }

    private fun hideSearchSuggestion(){
        binding.run {
            baseActivity.setViewToGone(cvSearchMemberSuggestion)
        }
    }

    private fun updateDataSkills(){
        binding.btnAddNewMember.isEnabled = listIdMember.size>0
        listSkillsAdapter.submitList(listMember)
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun Fragment.hideKeyboard() {
        view?.let {
            activity?.hideKeyboard(it)
        }
    }
}