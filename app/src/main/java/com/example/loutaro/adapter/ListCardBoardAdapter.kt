package com.example.loutaro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.loutaro.R
import com.example.loutaro.data.entity.Card
import com.example.loutaro.databinding.ItemRowCardBoardBinding

class ListCardBoardAdapter: ListAdapter<Card, ListCardBoardAdapter.ListViewHolder>(DiffCallback()) {
    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowCardBoardBinding.bind(itemView)
    }

    class DiffCallback: DiffUtil.ItemCallback<Card>() {
        override fun areItemsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem.idCard==newItem.idCard
        }

        override fun areContentsTheSame(oldItem: Card, newItem: Card): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_card_board, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val card = getItem(holder.adapterPosition)
        holder.binding.run {
            tvNameCard.text= card.name
        }
    }
}