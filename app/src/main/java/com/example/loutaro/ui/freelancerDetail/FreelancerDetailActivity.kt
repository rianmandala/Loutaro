package com.example.loutaro.ui.freelancerDetail

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.adapter.ListStandardSkillAdapter
import com.example.loutaro.databinding.ActivityFreelancerDetailBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.profile.ProfileViewModel
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FreelancerDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityFreelancerDetailBinding
    private lateinit var listStandardSkillAdapter: ListStandardSkillAdapter

    companion object{
        val EXTRA_ID_FREELANCER="extra id freelancer"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFreelancerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarTitle(getString(R.string.freelancer_details))
        activatedToolbarBackButton()
        initTinyDB()
        val idFreelancer = intent.getStringExtra(EXTRA_ID_FREELANCER)

        listStandardSkillAdapter = ListStandardSkillAdapter()
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.flexDirection = FlexDirection.ROW
        binding.run {
            rvTagSkillUser.layoutManager = layoutManager
            rvTagSkillUser.adapter = listStandardSkillAdapter
        }

        val freelancerProfileViewModel = ViewModelProvider(this, ViewModelFactory.getInstance()).get(FreelancerDetailViewModel::class.java)
        freelancerProfileViewModel.responseGetMyProfileFreelancer.observe(this){ profile->
            CoroutineScope(Dispatchers.Main).launch {
                binding.run{

                    if(profile.photo == null) {
                        imgAvatarUser.setImageResource(R.drawable.ic_baseline_person_24)
                        imgAvatarUser.layoutParams.width = 250
                        imgAvatarUser.layoutParams.height = 250
                    }else{
                        Glide.with(this@FreelancerDetailActivity)
                            .load(profile?.photo)
                            .into(imgAvatarUser)
                        imgAvatarUser.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                        imgAvatarUser.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    }

                    tvNameAndJobUser.text = getString(R.string.name_and_job, profile.name, profile.service)
                    if(profile.country==null) tvCountryUser.visibility = View.GONE
                    else {
                        tvCountryUser.text = profile.country
                        tvCountryUser.visibility = View.VISIBLE
                    }

                    if(profile.bio==null){
                        tvBioUser.text = getString(R.string.please_update_your_profile, "bio")
                        tvBioUser.setTextColor(resources.getColor(R.color.placeholder))
                    }else {
                        tvBioUser.text = profile.bio
                        tvBioUser.setTextColor(resources.getColor(R.color.text_color))
                    }

                    if(profile.portofolio==null){
                        tvPortofolioUser.text = getString(R.string.please_update_your_profile, getString(R.string.portofolio))
                        tvPortofolioUser.setTextColor(resources.getColor(R.color.placeholder))
                    }else{
                        tvPortofolioUser.text = profile.portofolio
                        tvPortofolioUser.setTextColor(resources.getColor(R.color.text_color))
                    }

                    if(profile.skills ==null || profile.skills?.size==0){
                        tvReplaceSkillUser.text = getString(R.string.please_update_your_profile, getString(R.string.skill))
                        setViewToInvisible(rvTagSkillUser)
                        tvReplaceSkillUser.visibility = View.VISIBLE
                    }else{
                        setViewToVisible(rvTagSkillUser)
                        tvReplaceSkillUser.visibility = View.GONE
                    }

                    if(profile.contact==null){
                        tvReplaceContactInfoUser.text = getString(R.string.please_update_your_profile, getString(R.string.contact))
                        setViewToVisible(tvReplaceContactInfoUser)
                        setViewToInvisible(tvContactCallUser)
                        setViewToInvisible(tvContactEmailUser)
                        setViewToInvisible(tvContactTelegramUser)
                    }else{
                        setViewToInvisible(tvReplaceContactInfoUser)
                        if(profile.contact?.telephone==null) setViewToInvisible(tvContactCallUser)
                        else {
                            setViewToVisible(tvContactCallUser)
                            tvContactCallUser.text = profile.contact?.telephone.toString()
                        }

                        if(profile.email == null) setViewToInvisible(tvContactEmailUser)
                        else{
                            setViewToVisible(tvContactEmailUser)
                            tvContactEmailUser.text = profile.email.toString()
                        }

                        if(profile.contact?.telegram == null) setViewToInvisible(tvContactTelegramUser)
                        else{
                            setViewToVisible(tvContactTelegramUser)
                            tvContactTelegramUser.text = profile.contact?.telegram.toString()
                        }
                    }

                    listStandardSkillAdapter.submitList(profile.skills)
                    listStandardSkillAdapter.notifyDataSetChanged()
                }
            }
        }
        freelancerProfileViewModel.getMyProfileFreelancer(idFreelancer.toString())

//        if(getUserTypeLogin()== getString(R.string.value_freelancer)){
//            freelancerProfileViewModel.responseGetMyProfileFreelancer.observe(this){ profile->
//                CoroutineScope(Dispatchers.Main).launch {
//                    binding.run{
//
//                        if(profile.photo == null) {
//                            imgAvatarUser.setImageResource(R.drawable.ic_baseline_person_24)
//                            imgAvatarUser.layoutParams.width = 250
//                            imgAvatarUser.layoutParams.height = 250
//                        }else{
//                            Glide.with(this@FreelancerDetailActivity)
//                                    .load(profile?.photo)
//                                    .into(imgAvatarUser)
//                            imgAvatarUser.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
//                            imgAvatarUser.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
//                        }
//
//                        tvNameAndJobUser.text = getString(R.string.name_and_job, profile.name, profile.service)
//                        if(profile.country==null) tvCountryUser.visibility = View.GONE
//                        else {
//                            tvCountryUser.text = profile.country
//                            tvCountryUser.visibility = View.VISIBLE
//                        }
//
//                        if(profile.bio==null){
//                            tvBioUser.text = getString(R.string.please_update_your_profile, "bio")
//                            tvBioUser.setTextColor(resources.getColor(R.color.placeholder))
//                        }else {
//                            tvBioUser.text = profile.bio
//                            tvBioUser.setTextColor(resources.getColor(R.color.text_color))
//                        }
//
//                        if(profile.portofolio==null){
//                            tvPortofolioUser.text = getString(R.string.please_update_your_profile, getString(R.string.portofolio))
//                            tvPortofolioUser.setTextColor(resources.getColor(R.color.placeholder))
//                        }else{
//                            tvPortofolioUser.text = profile.portofolio
//                            tvPortofolioUser.setTextColor(resources.getColor(R.color.text_color))
//                        }
//
//                        if(profile.skills ==null || profile.skills?.size==0){
//                            tvReplaceSkillUser.text = getString(R.string.please_update_your_profile, getString(R.string.skill))
//                            setViewToInvisible(rvTagSkillUser)
//                            tvReplaceSkillUser.visibility = View.VISIBLE
//                        }else{
//                            setViewToVisible(rvTagSkillUser)
//                            tvReplaceSkillUser.visibility = View.GONE
//                        }
//
//                        if(profile.contact==null){
//                            tvReplaceContactInfoUser.text = getString(R.string.please_update_your_profile, getString(R.string.contact))
//                            setViewToVisible(tvReplaceContactInfoUser)
//                            setViewToInvisible(tvContactCallUser)
//                            setViewToInvisible(tvContactEmailUser)
//                            setViewToInvisible(tvContactTelegramUser)
//                        }else{
//                            setViewToInvisible(tvReplaceContactInfoUser)
//                            if(profile.contact?.telephone==null) setViewToInvisible(tvContactCallUser)
//                            else {
//                                setViewToVisible(tvContactCallUser)
//                                tvContactCallUser.text = profile.contact?.telephone.toString()
//                            }
//
//                            if(profile.email == null) setViewToInvisible(tvContactEmailUser)
//                            else{
//                                setViewToVisible(tvContactEmailUser)
//                                tvContactEmailUser.text = profile.email.toString()
//                            }
//
//                            if(profile.contact?.telegram == null) setViewToInvisible(tvContactTelegramUser)
//                            else{
//                                setViewToVisible(tvContactTelegramUser)
//                                tvContactTelegramUser.text = profile.contact?.telegram.toString()
//                            }
//                        }
//
//                        listStandardSkillAdapter.submitList(profile.skills)
//                        listStandardSkillAdapter.notifyDataSetChanged()
//                    }
//                }
//            }
//            freelancerProfileViewModel.getMyProfileFreelancer(idFreelancer.toString())
//        }
//        else if(getUserTypeLogin()== getString(R.string.value_business_man)){
//
//            freelancerProfileViewModel.responseGetMyProfileBusinessMan.observe(this){ profile->
//                CoroutineScope(Dispatchers.Main).launch {
//                    binding.run{
//
//                        if(profile.photo == null) {
//                            imgAvatarUser.setImageResource(R.drawable.ic_baseline_person_24)
//                            imgAvatarUser.layoutParams.width = 250
//                            imgAvatarUser.layoutParams.height = 250
//                        }else{
//                            Glide.with(this@FreelancerDetailActivity)
//                                    .load(profile?.photo)
//                                    .into(imgAvatarUser)
//                            imgAvatarUser.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
//                            imgAvatarUser.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
//                        }
//
//                        tvNameAndJobUser.text = profile.name
//                        if(profile.country==null) tvCountryUser.visibility = View.GONE
//                        else {
//                            tvCountryUser.text = profile.country
//                            tvCountryUser.visibility = View.VISIBLE
//                        }
//
//                        if(profile.bio==null){
//                            tvBioUser.text = getString(R.string.please_update_your_profile, "bio")
//                            tvBioUser.setTextColor(resources.getColor(R.color.placeholder))
//                        }else {
//                            tvBioUser.text = profile.bio
//                            tvBioUser.setTextColor(resources.getColor(R.color.text_color))
//                        }
//
//                        if(profile.portofolio==null){
//                            tvPortofolioUser.text = getString(R.string.please_update_your_profile, getString(R.string.portofolio))
//                            tvPortofolioUser.setTextColor(resources.getColor(R.color.placeholder))
//                        }else{
//                            tvPortofolioUser.text = profile.portofolio
//                            tvPortofolioUser.setTextColor(resources.getColor(R.color.text_color))
//                        }
//
//                        parentRvSkillProfile.visibility = View.GONE
//                        tvTagSkillUser.visibility = View.GONE
//                        dividerRvSkillUser.visibility= View.GONE
//
//                        if(profile.contact==null){
//                            tvReplaceContactInfoUser.text = getString(R.string.please_update_your_profile, getString(R.string.contact))
//                            setViewToVisible(tvReplaceContactInfoUser)
//                            setViewToInvisible(tvContactCallUser)
//                            setViewToInvisible(tvContactEmailUser)
//                            setViewToInvisible(tvContactTelegramUser)
//                        }else{
//                            setViewToInvisible(tvReplaceContactInfoUser)
//                            if(profile.contact?.telephone==null) setViewToInvisible(tvContactCallUser)
//                            else {
//                                setViewToVisible(tvContactCallUser)
//                                tvContactCallUser.text = profile.contact?.telephone.toString()
//                            }
//
//                            if(profile.email == null) setViewToInvisible(tvContactEmailUser)
//                            else{
//                                setViewToVisible(tvContactEmailUser)
//                                tvContactEmailUser.text = profile.email.toString()
//                            }
//
//                            if(profile.contact?.telegram == null) setViewToInvisible(tvContactTelegramUser)
//                            else{
//                                setViewToVisible(tvContactTelegramUser)
//                                tvContactTelegramUser.text = profile.contact?.telegram.toString()
//                            }
//                        }
//                    }
//                }
//            }
//            freelancerProfileViewModel.getMyProfileBusineMan(idFreelancer.toString())
//        }

    }
}