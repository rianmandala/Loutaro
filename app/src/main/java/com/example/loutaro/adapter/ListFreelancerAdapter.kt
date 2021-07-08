package com.example.loutaro.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.databinding.ItemRowFreelancersBinding
import com.example.loutaro.ui.freelancerDetail.FreelancerDetailActivity
import com.example.loutaro.ui.freelancerDetail.FreelancerDetailActivity.Companion.EXTRA_ID_FREELANCER
import com.example.loutaro.ui.profile.ProfileFragment

class ListFreelancerAdapter: ListAdapter<Freelancer, ListFreelancerAdapter.ListViewHolder>(DiffCallback()) {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowFreelancersBinding.bind(itemView)
    }

    class DiffCallback: DiffUtil.ItemCallback<Freelancer>() {
        override fun areItemsTheSame(oldItem: Freelancer, newItem: Freelancer): Boolean {
            return oldItem.idFreelancer==oldItem.idFreelancer
        }

        override fun areContentsTheSame(oldItem: Freelancer, newItem: Freelancer): Boolean {
            return oldItem==oldItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_freelancers, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val freelancer = getItem(holder.adapterPosition)
        val adapter = ListStandardSkillAdapter()
        holder.binding.run {
            Glide.with(holder.itemView.context)
                .load(freelancer.photo)
                .error(R.drawable.ic_baseline_person_24)
                .into(imgAvatarFreelancer)

            tvNameFreelancer.text = freelancer.name
            tvWorkFreelancer.text = freelancer.service
            tvCountryFreelancer.text = freelancer.country
            if(freelancer.bio.isNullOrEmpty()){
                tvBioFreelancer.visibility = View.GONE
            }else{
                tvBioFreelancer.visibility = View.VISIBLE
                tvBioFreelancer.text = freelancer.bio
            }

            rvStandardSkillPreviewProject.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            rvStandardSkillPreviewProject.adapter = adapter
            adapter.submitList(freelancer.skills)

            parentLayoutListFreelancer.setOnClickListener {
                val profileIntent = Intent(holder.itemView.context, FreelancerDetailActivity::class.java)
                profileIntent.putExtra(EXTRA_ID_FREELANCER, freelancer.idFreelancer)
                holder.itemView.context.startActivity(profileIntent)
            }
        }
    }
}