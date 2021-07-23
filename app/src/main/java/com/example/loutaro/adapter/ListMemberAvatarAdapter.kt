package com.example.loutaro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.databinding.ItemRowMemberAvatarBinding

class ListMemberAvatarAdapter: ListAdapter<Freelancer, ListMemberAvatarAdapter.ListViewHolder>(DiffCallback()) {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowMemberAvatarBinding.bind(itemView)
    }

    class DiffCallback: DiffUtil.ItemCallback<Freelancer>() {
        override fun areItemsTheSame(oldItem: Freelancer, newItem: Freelancer): Boolean {
            return oldItem.idFreelancer==newItem.idFreelancer
        }

        override fun areContentsTheSame(oldItem: Freelancer, newItem: Freelancer): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_member_avatar, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val freelancer = getItem(holder.adapterPosition)
        val drawable = TextDrawable.builder()
            .buildRect("${freelancer.name?.get(0)}", ContextCompat.getColor(holder.itemView.context, R.color.secondary))
        if(freelancer.photo!=null){
            Glide.with(holder.itemView.context)
                .load(freelancer.photo)
                .into(holder.binding.imgAvatarMember)
        }else{
            holder.binding.imgAvatarMember.setImageDrawable(drawable)
        }
    }
}