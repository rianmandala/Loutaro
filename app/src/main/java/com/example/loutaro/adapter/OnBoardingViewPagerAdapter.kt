package com.example.loutaro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.data.entity.OnBoarding
import com.example.loutaro.databinding.ItemOnboardingViewPagerBinding

class OnBoardingViewPagerAdapter(val listBoard: List<OnBoarding>): RecyclerView.Adapter<OnBoardingViewPagerAdapter.ViewPagerViewHolder>() {
    inner class ViewPagerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val binding = ItemOnboardingViewPagerBinding.bind(itemView)

        fun bind(boarding: OnBoarding){
            Glide.with(itemView.context)
                .load(boarding.image)
                .into(binding.imgOnboarding)
            binding.tvTitleOnboarding.text = boarding.title
            binding.tvSubtitleOnboarding.text = boarding.subtitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding_view_pager,parent,false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        val board = listBoard[position]

        holder.bind(board)

    }

    override fun getItemCount()=listBoard.size

}