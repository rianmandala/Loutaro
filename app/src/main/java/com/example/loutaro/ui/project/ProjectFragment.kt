package com.example.loutaro.ui.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListTitleProjectAdapter
import com.example.loutaro.adapter.SectionPagerAdapter
import com.example.loutaro.databinding.FragmentActiveProjectBinding
import com.example.loutaro.databinding.FragmentFinishProjectBinding
import com.example.loutaro.databinding.FragmentProjectBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.project.activeProject.ActiveProjectViewModel
import com.example.loutaro.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProjectFragment : Fragment() {
    private lateinit var binding: FragmentProjectBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentProjectBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewPagerProject.adapter = SectionPagerAdapter(requireActivity(), requireActivity().supportFragmentManager)
        binding.tabLayoutProject.setupWithViewPager(binding.viewPagerProject)
    }

}