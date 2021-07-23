package com.example.loutaro.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListFreelancerAdapter
import com.example.loutaro.adapter.ListProjectRecommendationAdapter
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.entity.ProjectRecommendation
import com.example.loutaro.databinding.FragmentHomeBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.notification.NotificationActivity
import com.example.loutaro.ui.settings.SettingsActivity
import com.example.loutaro.utils.TFIDF
import com.example.loutaro.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var listProjectadapter: ListProjectRecommendationAdapter
    private lateinit var listFreelancerAdapter: ListFreelancerAdapter
    private lateinit var listProject: MutableList<ProjectRecommendation>
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
            listProjectadapter = ListProjectRecommendationAdapter()
            listFreelancerAdapter = ListFreelancerAdapter()

            baseActivity.setViewToGone(rvProjects, tvTagRecommendationForYou, bottomBorder)
            baseActivity.setViewToVisible(includeLayout.layoutProgress)
        }

        baseActivity.initTinyDB(requireActivity())
        if(baseActivity.getUserTypeLogin(requireActivity())==requireActivity().resources.getString(R.string.value_freelancer)){
            binding.rvProjects.adapter = listProjectadapter
            binding.tvTagRecommendationForYou.text =getString(R.string.recommendation_for_you)

            homeViewModel.responseGetProfile.observe(requireActivity()){profile->
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if(profile?.skills != null){
                            Log.d("project","profile skill ${profile.skills}")
                            Log.d("project","list project ${listProject}")
                            var projectRecommendation = TFIDF().getProjectRecommendation(profile.skills!!.toMutableList(), listProject)
                            for(project in projectRecommendation){
                                Log.d("project","${project.title} dengan score ${project.recommendationScore}")
                            }
                            withContext(Dispatchers.Main){
                                listProjectadapter.submitList(projectRecommendation)
                                listProjectadapter.notifyDataSetChanged()
                                baseActivity.setViewToVisible(binding.rvProjects, binding.tvTagRecommendationForYou,binding.bottomBorder)
                                baseActivity.setViewToInvisible(binding.includeLayout.layoutProgress)
                            }

                        }
                    }catch (e: Exception){
                        Log.e("error","Error when try to get data project ${e.message}")
                    }

                }
            }

            homeViewModel.statusGetDataProjects.observe(requireActivity()){ projects->
                if(projects!=null){
                   homeViewModel.getProfile(baseActivity.getCurrentUser()?.uid.toString())
                    listProject= projects.toMutableList()
                }
            }

            homeViewModel.getDataProjects()
        }
        else if(baseActivity.getUserTypeLogin(requireActivity())==requireActivity().resources.getString(R.string.value_business_man)){
            binding.rvProjects.adapter = listFreelancerAdapter
            binding.tvTagRecommendationForYou.text =getString(R.string.list_all_freelancer)
            Log.d("tv_rekomendasi", "ini di tv rekomendasi")
            homeViewModel.statusGetAllFreelancer.observe(requireActivity()){ freelancers->
                Log.d("hasil_get_freelancer", "${freelancers?.toList()}")
                if(freelancers!=null){
                    listFreelancerAdapter.submitList(freelancers)
                    listFreelancerAdapter.notifyDataSetChanged()
                    baseActivity.setViewToVisible(binding.rvProjects, binding.tvTagRecommendationForYou,binding.bottomBorder)
                    baseActivity.setViewToInvisible(binding.includeLayout.layoutProgress)
                }
            }
            homeViewModel.getAllFreelancer()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        if(baseActivity.isUserBusinessMan(requireActivity())){
//            menu.clear()
//            inflater.inflate(R.menu.notification_menu, menu)
//        }
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