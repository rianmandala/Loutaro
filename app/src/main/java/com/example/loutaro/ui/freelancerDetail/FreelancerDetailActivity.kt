package com.example.loutaro.ui.freelancerDetail

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.adapter.ListStandardSkillAdapter
import com.example.loutaro.databinding.ActivityFreelancerDetailBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
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

            setViewToInvisible(btnAddToFavoriteFreelancer)
            btnAddToFavoriteFreelancer.setOnClickListener {
                val listIdFreelancerSaved = getListIdFreelancerSaved(getCurrentUser()?.uid.toString())
                if(listIdFreelancerSaved!=null){
                    if(listIdFreelancerSaved.contains(idFreelancer.toString())){
                        listIdFreelancerSaved.remove(idFreelancer.toString())
                        savedIdFreelancer(getCurrentUser()?.uid.toString(), listIdFreelancerSaved)
                        checkSaveFreelancer(false)
                    }else{
                        listIdFreelancerSaved.add(idFreelancer.toString())
                        savedIdFreelancer(getCurrentUser()?.uid.toString(), listIdFreelancerSaved)
                        checkSaveFreelancer(true)
                    }
                }else{
                    savedIdFreelancer(getCurrentUser()?.uid.toString(), arrayListOf(idFreelancer.toString()))
                    checkSaveFreelancer(true)
                }
            }
        }

        val freelancerProfileViewModel = ViewModelProvider(this, ViewModelFactory.getInstance()).get(FreelancerDetailViewModel::class.java)
        freelancerProfileViewModel.responseGetMyProfileFreelancer.observe(this){ profile->
            binding.run{
                val drawable = TextDrawable.builder()
                        .buildRect("${profile.name?.get(0)}", ContextCompat.getColor(this@FreelancerDetailActivity, R.color.secondary))
                if(profile.photo!=null){
                    Glide.with(this@FreelancerDetailActivity)
                            .load(profile.photo)
                            .into(imgAvatarUser)
                }else{
                    imgAvatarUser.setImageDrawable(drawable)
                }

                tvNameAndJobUser.text = getString(R.string.name_and_job, profile.name, profile.service)
                if(profile.country==null || profile.country=="") tvCountryUser.visibility = View.GONE
                else {
                    tvCountryUser.text = profile.country
                    tvCountryUser.visibility = View.VISIBLE
                }

                if(profile.bio!=null && profile.bio!=""){
                    tvBioUser.text = profile.bio
                    tvBioUser.setTextColor(resources.getColor(R.color.text_color))
                }else {
                    setViewToGone(tvBioUser)
                }

                if(profile.portofolio!=null && profile.portofolio!=""){
                    tvPortofolioUser.text = profile.portofolio
                    tvPortofolioUser.setTextColor(resources.getColor(R.color.text_color))
                }else{
                    setViewToGone(tvPortofolioUser, tvTagPortofolioUser, dividerTvPortofolioUser)
                }

                if(profile.skills !=null){
                    setViewToVisible(rvTagSkillUser)
                    listStandardSkillAdapter.submitList(profile.skills)
                    listStandardSkillAdapter.notifyDataSetChanged()
                }else{
                    setViewToGone(rvTagSkillUser, dividerRvSkillUser)
                }

                if(profile.contact?.telephone==null || profile.contact?.telephone=="") setViewToGone(tvContactCallUser)
                else {
                    setViewToVisible(tvContactCallUser)
                    tvContactCallUser.text = profile.contact?.telephone.toString()
                }

                if(profile.email == null || profile.email=="") setViewToGone(tvContactEmailUser)
                else{
                    setViewToVisible(tvContactEmailUser)
                    tvContactEmailUser.text = profile.email.toString()
                }

                if(profile.contact?.telegram == null || profile.contact?.telegram=="") setViewToGone(tvContactTelegramUser)
                else{
                    setViewToVisible(tvContactTelegramUser)
                    tvContactTelegramUser.text = profile.contact?.telegram.toString()
                }

                setViewToVisible(btnAddToFavoriteFreelancer)
                val listIdFreelancerSaved = getListIdFreelancerSaved(getCurrentUser()?.uid.toString())
                if(listIdFreelancerSaved!=null){
                    if(listIdFreelancerSaved.contains(idFreelancer.toString())){
                        checkSaveFreelancer(true)
                    }else{
                        checkSaveFreelancer(false)
                    }
                }else{
                    checkSaveFreelancer(false)
                }

            }
        }
        freelancerProfileViewModel.getMyProfileFreelancer(idFreelancer.toString())

    }

    fun checkSaveFreelancer(isSaved: Boolean){
        if(isSaved){
            binding.btnAddToFavoriteFreelancer.setImageResource(R.drawable.ic_baseline_favorite_24)
        }else{
            binding.btnAddToFavoriteFreelancer.setImageResource(R.drawable.ic_baseline_favorite_border_24)
        }
    }
}