package com.example.loutaro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.loutaro.R
import com.example.loutaro.data.entity.ItemId
import com.example.loutaro.data.entity.Task
import com.example.loutaro.databinding.ItemRowFreelancerNeededTaskBinding
import com.google.android.material.textfield.TextInputLayout

class ListFreelancerNeededTaskAdapter(private var taskFreelancer: MutableList<Task>): ListAdapter<ItemId, ListFreelancerNeededTaskAdapter.ListViewHolder>(DiffCallback()) {

    var onBtnDeleteClick: ((String)-> Unit)?=null

    var onTodoChange: ((Int, String)-> Unit)?=null

    var onSubmitNeedClick: ((Boolean) -> Unit)?=null


    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowFreelancerNeededTaskBinding.bind(itemView)
    }

    class DiffCallback: DiffUtil.ItemCallback<ItemId>() {
        override fun areItemsTheSame(oldItem: ItemId, newItem: ItemId): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemId, newItem: ItemId): Boolean {
            return oldItem==newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_freelancer_needed_task, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val task = getItem(holder.adapterPosition)
        holder.binding.run{
            inputFreelancerTask.setText("")

            onSubmitNeedClick={
                validateRequire(inputFreelancerTask.text.toString(),txtInputFreelancerTask, holder.itemView.context.getString(R.string.task), holder.itemView.context)
            }

            inputFreelancerTask.doAfterTextChanged {
                validateRequire(it.toString(),txtInputFreelancerTask, holder.itemView.context.getString(R.string.task), holder.itemView.context)
                if(it?.trim()?.isNotEmpty() == true){
                    onTodoChange?.invoke(holder.adapterPosition, it.toString())
                }
            }

            btnDeleteTask.setOnClickListener {
                onBtnDeleteClick?.invoke(task.id)
            }
        }
    }

    fun validateRequire(field: String, input: TextInputLayout, inputName: String, context: Context): Boolean {
        return if(field.trim().isEmpty() && field.trim().isBlank()){
            input.isErrorEnabled=true
            input.error = context.getString(R.string.required, inputName)
            false
        }else{
            input.isErrorEnabled=false
            input.error = null
            true
        }
    }
}