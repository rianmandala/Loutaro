package com.example.loutaro.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.adapter.ListStandardSkillAdapter
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.databinding.FragmentPofileBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.profile.updateProfile.UpdateProfileActivity
import com.example.loutaro.ui.settings.SettingsActivity
import com.example.loutaro.ui.withdrawFreelancer.WithdrawFreelancerActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentPofileBinding
    private lateinit var listStandardSkillAdapter: ListStandardSkillAdapter
    private var balanceFreelancer = 0.toLong()
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
                balanceFreelancer = profile.balance
                CoroutineScope(Dispatchers.Main).launch {
                    binding.run{
                        val drawable = TextDrawable.builder()
                            .buildRect("${profile.name?.get(0)}", ContextCompat.getColor(requireActivity(), R.color.secondary))
                        if(profile.photo == null) {
                            imgAvatarUser.setImageDrawable(drawable)
                        }else{
                            Glide.with(requireActivity())
                                    .load(profile?.photo)
                                    .into(imgAvatarUser)
                        }

                        tvNameAndJobUser.text = getString(R.string.name_and_job, profile.name, profile.service)
                        if(profile.country==null) tvCountryUser.visibility = View.GONE
                        else {
                            tvCountryUser.text = profile.country
                            tvCountryUser.visibility = View.VISIBLE
                        }

                        if(profile.bio==null || profile.bio==""){
                            tvBioUser.text = getString(R.string.please_update_your_profile, "bio")
                            tvBioUser.setTextColor(resources.getColor(R.color.placeholder))
                        }else {
                            tvBioUser.text = profile.bio
                            tvBioUser.setTextColor(resources.getColor(R.color.text_color))
                        }

                        if(profile.balance==null){
                            tvBalanceUser.text = "$0"
                        }else{
                            tvBalanceUser.text = "$${profile.balance}"
                        }

                        if(profile.portofolio==null || profile.portofolio==""){
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
                            listStandardSkillAdapter.submitList(profile.skills)
                            listStandardSkillAdapter.notifyDataSetChanged()
                        }

                        if(profile.contact?.telephone==null || profile.contact?.telephone=="") baseActivity.setViewToGone(tvContactCallUser)
                        else {
                            baseActivity.setViewToVisible(tvContactCallUser)
                            tvContactCallUser.text = profile.contact?.telephone.toString()
                        }

                        if(profile.email == null || profile.email=="") baseActivity.setViewToGone(tvContactEmailUser)
                        else{
                            baseActivity.setViewToVisible(tvContactEmailUser)
                            tvContactEmailUser.text = profile.email.toString()
                        }

                        if(profile.contact?.telegram == null || profile.contact?.telegram=="") baseActivity.setViewToGone(tvContactTelegramUser)
                        else{
                            baseActivity.setViewToVisible(tvContactTelegramUser)
                            tvContactTelegramUser.text = profile.contact?.telegram.toString()
                        }


                    }
                }
            }
            profileViewModel.getMyProfileFreelancer()
        }
        else if(baseActivity.getUserTypeLogin(requireActivity())== requireActivity().getString(R.string.value_business_man)){

            profileViewModel.responseGetMyProfileBusinessMan.observe(requireActivity()){ profile->
                CoroutineScope(Dispatchers.Main).launch {
                    binding.run{
                        val drawable = TextDrawable.builder()
                            .buildRect("${profile.name?.toUpperCase()?.get(0)}", ContextCompat.getColor(requireActivity(), R.color.secondary))
                        if(profile.photo == null) {
                            imgAvatarUser.setImageDrawable(drawable)
                        }else{
                            Glide.with(requireActivity())
                                .load(profile?.photo)
                                .into(imgAvatarUser)
                        }

                        tvNameAndJobUser.text = profile.name
                        if(profile.country==null) tvCountryUser.visibility = View.GONE
                        else {
                            tvCountryUser.text = profile.country
                            tvCountryUser.visibility = View.VISIBLE
                        }

                        if(profile.bio==null || profile.bio==""){
                            tvBioUser.text = getString(R.string.please_update_your_profile, "bio")
                            tvBioUser.setTextColor(resources.getColor(R.color.placeholder))
                        }else {
                            tvBioUser.text = profile.bio
                            tvBioUser.setTextColor(resources.getColor(R.color.text_color))
                        }

                        baseActivity.setViewToGone(tvTagBalanceUser, tvBalanceUser, dividerTvBalanceUser)

                        if(profile.portofolio==null || profile.portofolio==""){
                            tvPortofolioUser.text = getString(R.string.please_update_your_profile, getString(R.string.portofolio))
                            tvPortofolioUser.setTextColor(resources.getColor(R.color.placeholder))
                        }else{
                            tvPortofolioUser.text = profile.portofolio
                            tvPortofolioUser.setTextColor(resources.getColor(R.color.text_color))
                        }

                        parentRvSkillProfile.visibility = View.GONE
                        tvTagSkillUser.visibility = View.GONE
                        dividerRvSkillUser.visibility=View.GONE

                        if(profile.contact?.telephone==null || profile.contact?.telephone=="") baseActivity.setViewToGone(tvContactCallUser)
                        else {
                            baseActivity.setViewToVisible(tvContactCallUser)
                            tvContactCallUser.text = profile.contact?.telephone.toString()
                        }

                        if(profile.email == null || profile.email=="") baseActivity.setViewToGone(tvContactEmailUser)
                        else{
                            baseActivity.setViewToVisible(tvContactEmailUser)
                            tvContactEmailUser.text = profile.email.toString()
                        }

                        if(profile.contact?.telegram == null || profile.contact?.telegram=="") baseActivity.setViewToGone(tvContactTelegramUser)
                        else{
                            baseActivity.setViewToVisible(tvContactTelegramUser)
                            tvContactTelegramUser.text = profile.contact?.telegram.toString()
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
        menu.findItem(R.id.withdraw_menu).isVisible=baseActivity.isUserFreelancer(requireActivity())
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.setting_menu->{
                val settingIntent = Intent(requireActivity(), SettingsActivity::class.java)
                startActivity(settingIntent)
                return true
            }
            R.id.withdraw_menu->{
                val withdrawalIntent = Intent(requireActivity(), WithdrawFreelancerActivity::class.java)
                withdrawalIntent.putExtra(WithdrawFreelancerActivity.EXTRA_BALANCE_AMOUNT, balanceFreelancer)
                startActivity(withdrawalIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}