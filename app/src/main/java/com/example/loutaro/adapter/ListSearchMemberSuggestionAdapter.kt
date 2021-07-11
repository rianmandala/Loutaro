package com.example.loutaro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.databinding.SearchMemberSuggestionBinding

class ListSearchMemberSuggestionAdapter: ListAdapter<Freelancer, ListSearchMemberSuggestionAdapter.ListViewHolder>(DiffCallback()) {

    var onClickCallback: ((Freelancer)-> Unit)?=null

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = SearchMemberSuggestionBinding.bind(itemView)
        fun bind(freelancer: Freelancer) {
            binding.run {
                Glide.with(itemView.context)
                        .load(freelancer.photo)
                        .into(imgSearchMemberSuggestion)

                tvSearchNameMemberSuggestion.text = freelancer.name
                tvSearchEmailMemberSuggestion.text = freelancer.email
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Freelancer>()  {
        override fun areItemsTheSame(oldItem: Freelancer, newItem: Freelancer): Boolean {
            return oldItem.idFreelancer==newItem.idFreelancer
        }

        override fun areContentsTheSame(oldItem: Freelancer, newItem: Freelancer): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_member_suggestion, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val freelancer = getItem(holder.adapterPosition)
        holder.binding.parentLayoutSearchMemberSuggestion.setOnClickListener {
            onClickCallback?.invoke(freelancer)
        }
        holder.bind(freelancer)
    }
}