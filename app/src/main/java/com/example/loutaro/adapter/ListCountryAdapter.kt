package com.example.loutaro.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.loutaro.R
import com.example.loutaro.databinding.ItemRowCountryBinding


class ListCountryAdapter(val countryHasChoosen: String, val rgCountry: RadioGroup): ListAdapter<String, ListCountryAdapter.ListViewHolder>(
    DiffCallbask()
) {
    var onItemClick: ((String) -> Unit)? = null
    private var lastSelectedCountry = ""
    inner class ListViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowCountryBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_row_country,
            parent,
            false
        )
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val country = getItem(holder.adapterPosition)
        holder.binding.rbCountry.isChecked = lastSelectedCountry == country
        if(lastSelectedCountry == "" && countryHasChoosen == country){
            holder.binding.rbCountry.isChecked= true
            lastSelectedCountry = country
        }
        holder.binding.rbCountry.text = country
        holder.binding.rbCountry.setOnClickListener {
            lastSelectedCountry = country
            notifyDataSetChanged()
            onItemClick?.invoke(country)
        }
    }

    private class DiffCallbask: DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.length==newItem.length
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem==newItem
        }

    }
}