package com.example.loutaro.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.adapter.ListStandardSkillAdapter
import com.example.loutaro.databinding.FragmentPofileBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.profile.updateProfile.UpdateProfileActivity
import com.example.loutaro.ui.settings.SettingsActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentPofileBinding
    private lateinit var listStandardSkillAdapter: ListStandardSkillAdapter
    private val baseActivity = BaseActivity()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPofileBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        baseActivity.initTinyDB(requireActivity())
        listStandardSkillAdapter = ListStandardSkillAdapter()
        val layoutManager = FlexboxLayoutManager(requireActivity())
        layoutManager.flexDirection = FlexDirection.ROW
        binding.run {
            rvTagSkillUser.layoutManager = layoutManager
            rvTagSkillUser.adapter = listStandardSkillAdapter

            btnEditProfile.setOnClickListener {
                val updateIntent = Intent(requireActivity(), UpdateProfileActivity::class.java)
                startActivity(updateIntent)
            }
        }

        val profileViewModel = ViewModelProvider(requireActivity(), ViewModelFactory.getInstance()).get(ProfileViewModel::class.java)
        if(baseActivity.getUserTypeLogin(requireActivity())== requireActivity().getString(R.string.value_freelancer)){
            profileViewModel.responseGetMyProfileFreelancer.observe(requireActivity()){ profile->
                CoroutineScope(Dispatchers.Main).launch {
                    binding.run{

                        if(profile.photo == null) {
                            imgAvatarUser.setImageResource(R.drawable.ic_baseline_person_24)
                            imgAvatarUser.layoutParams.width = 250
                            imgAvatarUser.layoutParams.height = 250
                        }else{
                            Glide.with(requireActivity())
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
                            baseActivity.setViewToInvisible(rvTagSkillUser)
                            tvReplaceSkillUser.visibility = View.VISIBLE
                        }else{
                            baseActivity.setViewToVisible(rvTagSkillUser)
                            tvReplaceSkillUser.visibility = View.GONE
                        }

                        if(profile.contact==null){
                            tvReplaceContactInfoUser.text = getString(R.string.please_update_your_profile, getString(R.string.contact))
                            baseActivity.setViewToVisible(tvReplaceContactInfoUser)
                            baseActivity.setViewToInvisible(tvContactCallUser)
                            baseActivity.setViewToInvisible(tvContactEmailUser)
                            baseActivity.setViewToInvisible(tvContactTelegramUser)
                        }else{
                            baseActivity.setViewToInvisible(tvReplaceContactInfoUser)
                            if(profile.contact?.telephone==null) baseActivity.setViewToInvisible(tvContactCallUser)
                            else {
                                baseActivity.setViewToVisible(tvContactCallUser)
                                tvContactCallUser.text = profile.contact?.telephone.toString()
                            }

                            if(profile.email == null) baseActivity.setViewToInvisible(tvContactEmailUser)
                            else{
                                baseActivity.setViewToVisible(tvContactEmailUser)
                                tvContactEmailUser.text = profile.email.toString()
                            }

                            if(profile.contact?.telegram == null) baseActivity.setViewToInvisible(tvContactTelegramUser)
                            else{
                                baseActivity.setViewToVisible(tvContactTelegramUser)
                                tvContactTelegramUser.text = profile.contact?.telegram.toString()
                            }
                        }

                        listStandardSkillAdapter.submitList(profile.skills)
                        listStandardSkillAdapter.notifyDataSetChanged()
                    }
                }
            }
            profileViewModel.getMyProfileFreelancer()
        }
        else if(baseActivity.getUserTypeLogin(requireActivity())== requireActivity().getString(R.string.value_business_man)){

            profileViewModel.responseGetMyProfileBusinessMan.observe(requireActivity()){ profile->
                CoroutineScope(Dispatchers.Main).launch {
                    binding.run{

                        if(profile.photo == null) {
                            imgAvatarUser.setImageResource(R.drawable.ic_baseline_person_24)
                            imgAvatarUser.layoutParams.width = 250
                            imgAvatarUser.layoutParams.height = 250
                        }else{
                            Glide.with(requireActivity())
                                .load(profile?.photo)
                                .into(imgAvatarUser)
                            imgAvatarUser.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                            imgAvatarUser.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                        }

                        tvNameAndJobUser.text = profile.name
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

                        parentRvSkillProfile.visibility = View.GONE
                        tvTagSkillUser.visibility = View.GONE
                        dividerRvSkillUser.visibility=View.GONE

                        if(profile.contact==null){
                            tvReplaceContactInfoUser.text = getString(R.string.please_update_your_profile, getString(R.string.contact))
                            baseActivity.setViewToVisible(tvReplaceContactInfoUser)
                            baseActivity.setViewToInvisible(tvContactCallUser)
                            baseActivity.setViewToInvisible(tvContactEmailUser)
                            baseActivity.setViewToInvisible(tvContactTelegramUser)
                        }else{
                            baseActivity.setViewToInvisible(tvReplaceContactInfoUser)
                            if(profile.contact?.telephone==null) baseActivity.setViewToInvisible(tvContactCallUser)
                            else {
                                baseActivity.setViewToVisible(tvContactCallUser)
                                tvContactCallUser.text = profile.contact?.telephone.toString()
                            }

                            if(profile.email == null) baseActivity.setViewToInvisible(tvContactEmailUser)
                            else{
                                baseActivity.setViewToVisible(tvContactEmailUser)
                                tvContactEmailUser.text = profile.email.toString()
                            }

                            if(profile.contact?.telegram == null) baseActivity.setViewToInvisible(tvContactTelegramUser)
                            else{
                                baseActivity.setViewToVisible(tvContactTelegramUser)
                                tvContactTelegramUser.text = profile.contact?.telegram.toString()
                            }
                        }
                    }
                }
            }
            profileViewModel.getMyProfileBusineMan()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.setting_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.setting_menu->{
                val settingIntent = Intent(requireActivity(), SettingsActivity::class.java)
                startActivity(settingIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}