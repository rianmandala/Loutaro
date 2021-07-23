package com.example.loutaro.ui.project

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.loutaro.R
import com.example.loutaro.adapter.ProjectPagerAdapter
import com.example.loutaro.databinding.FragmentProjectBinding
import com.example.loutaro.ui.baseActivity.BaseActivity

class ProjectFragment : Fragment() {
    private lateinit var binding: FragmentProjectBinding
    private val baseActivity = BaseActivity()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentProjectBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        baseActivity.initTinyDB(requireActivity())
        val isBusinessMan = baseActivity.getUserTypeLogin(requireActivity()) == getString(R.string.value_business_man)
        binding.viewPagerProject.adapter = ProjectPagerAdapter(requireActivity(), requireActivity().supportFragmentManager, isBusinessMan)
        binding.tabLayoutProject.setupWithViewPager(binding.viewPagerProject)
    }

}