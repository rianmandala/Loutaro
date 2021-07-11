package com.example.loutaro.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListFreelancerAdapter
import com.example.loutaro.adapter.ListProjectAdapter
import com.example.loutaro.databinding.FragmentHomeBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.notification.NotificationActivity
import com.example.loutaro.ui.settings.SettingsActivity
import com.example.loutaro.viewmodel.ViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var listProjectadapter: ListProjectAdapter
    private lateinit var listFreelancerAdapter: ListFreelancerAdapter
    private val baseActivity = BaseActivity()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val homeViewModel = ViewModelProvider(requireActivity(),ViewModelFactory.getInstance()).get(HomeViewModel::class.java)
        binding.run{
            rvProjects.layoutManager = LinearLayoutManager(requireActivity())
            listProjectadapter = ListProjectAdapter()
            listFreelancerAdapter = ListFreelancerAdapter()
        }

        baseActivity.setViewToInvisible(binding.rvProjects)
        baseActivity.setViewToVisible(binding.includeLayout.layoutProgress)

        baseActivity.initTinyDB(requireActivity())
        if(baseActivity.getUserTypeLogin(requireActivity())==requireActivity().resources.getString(R.string.value_freelancer)){
            binding.rvProjects.adapter = listProjectadapter
            homeViewModel.statusGetDataProjects.observe(requireActivity()){ projects->
                if(projects!=null){
                    listProjectadapter.submitList(projects)
                    listProjectadapter.notifyDataSetChanged()
                    baseActivity.setViewToVisible(binding.rvProjects)
                    baseActivity.setViewToInvisible(binding.includeLayout.layoutProgress)
                }
            }

            homeViewModel.getDataProjects()
        }
        else if(baseActivity.getUserTypeLogin(requireActivity())==requireActivity().resources.getString(R.string.value_business_man)){
            binding.rvProjects.adapter = listFreelancerAdapter
            homeViewModel.statusGetAllFreelancer.observe(requireActivity()){ freelancers->
                Log.d("hasil_get_freelancer", "${freelancers?.toList()}")
                if(freelancers!=null){
                    listFreelancerAdapter.submitList(freelancers)
                    listFreelancerAdapter.notifyDataSetChanged()
                    baseActivity.setViewToVisible(binding.rvProjects)
                    baseActivity.setViewToInvisible(binding.includeLayout.layoutProgress)
                }
            }
            homeViewModel.getAllFreelancer()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(baseActivity.isUserBusinessMan(requireActivity())){
            menu.clear()
            inflater.inflate(R.menu.notification_menu, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.notification_tab->{
                val notificationIntent = Intent(requireActivity(), NotificationActivity::class.java)
                startActivity(notificationIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}