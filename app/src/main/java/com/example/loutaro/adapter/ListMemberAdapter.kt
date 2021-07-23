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
import com.example.loutaro.databinding.ItemRowMemberBinding

class ListMemberAdapter(private val isFreelancer: Boolean=false): ListAdapter<Freelancer, ListMemberAdapter.ListViewHolder>(DiffCallback()) {

    var onClickCallbak: ((String)-> Unit)?=null

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowMemberBinding.bind(itemView)
        fun bind(freelancer: Freelancer) {
            val drawable = TextDrawable.builder()
                    .buildRect("${freelancer.name?.get(0)}", ContextCompat.getColor(itemView.context, R.color.secondary))
            binding.run {
                if(freelancer.photo!=null){
                    Glide.with(itemView.context)
                            .load(freelancer.photo)
                            .into(imgAvatarMember)
                }else{
                    imgAvatarMember.setImageDrawable(drawable)
                }

                tvNameMember.text = freelancer.name
                tvEmailMember.text = freelancer.email

                if(isFreelancer){
                    imgBtnDeleteMember.visibility = View.GONE
                }else{
                    imgBtnDeleteMember.visibility = View.VISIBLE
                }
                imgBtnDeleteMember.setOnClickListener {
                    onClickCallbak?.invoke(freelancer.idFreelancer.toString())
                }
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Freelancer>() {
        override fun areItemsTheSame(oldItem: Freelancer, newItem: Freelancer): Boolean {
            return oldItem.idFreelancer==newItem.idFreelancer
        }

        override fun areContentsTheSame(oldItem: Freelancer, newItem: Freelancer): Boolean {
            return newItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_member, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val freelancer = getItem(holder.adapterPosition)
        holder.bind(freelancer)
    }
}