package com.example.loutaro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.loutaro.R
import com.example.loutaro.databinding.ItemRowSkillsBinding

class ListSkillsAdapter: ListAdapter<String, ListSkillsAdapter.ListViewHolder>(DiffCallback()) {

    var onItemClick: ((Int)-> Unit)? = null

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowSkillsBinding.bind(itemView)
    }

    class DiffCallback: DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.length==newItem.length
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_skills, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val skill = getItem(holder.adapterPosition)
        holder.binding.chipSkill.text = skill
        holder.binding.chipSkill.setOnCloseIconClickListener {
            onItemClick?.invoke(holder.adapterPosition)
            notifyDataSetChanged()
        }
    }
}