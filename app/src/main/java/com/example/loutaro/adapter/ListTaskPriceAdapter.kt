package com.example.loutaro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.loutaro.R
import com.example.loutaro.data.entity.Task
import com.example.loutaro.databinding.ItemRowTaskPriceBinding

class ListTaskPriceAdapter: ListAdapter<Task, ListTaskPriceAdapter.ListViewHolder>(DiffCalback()) {

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowTaskPriceBinding.bind(itemView)
    }

    class DiffCalback: DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.todo==newItem.todo
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_task_price, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val adapter = ListStandardSkillAdapter()
        val task = getItem(holder.adapterPosition)
        holder.binding.run {
            tvTagPrice.text=holder.itemView.context.getString(R.string.task_price_freelancer,holder.adapterPosition+1, task.price)
            btnApplyAsFreelancer.text =holder.itemView.context.getString(R.string.apply_as_freelancer, holder.adapterPosition+1)
            rvTodoTask.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            rvTodoTask.adapter = adapter
            adapter.submitList(task.todo)
        }
    }
}