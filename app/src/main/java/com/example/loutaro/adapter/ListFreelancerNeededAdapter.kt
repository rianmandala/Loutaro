package com.example.loutaro.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.loutaro.R
import com.example.loutaro.data.entity.ItemId
import com.example.loutaro.data.entity.Task
import com.example.loutaro.data.entity.TodoId
import com.example.loutaro.databinding.ItemRowFreelancerNeededBinding
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class ListFreelancerNeededAdapter(private var taskFreelancer: MutableList<Task>): ListAdapter<ItemId, ListFreelancerNeededAdapter.ListViewHolder>(DiffCallback()) {

    var onInputFeeChange: ((Int, String)-> Unit)?=null
    var onInputTodoChange: ((Int, MutableList<String>) -> Unit)?=null
    var onDeleteTodoCallback:((Int, MutableList<String>)-> Unit)?=null
    var onSubmitClick: ((Boolean) -> Unit)?=null

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowFreelancerNeededBinding.bind(itemView)
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_freelancer_needed, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val adapter = ListFreelancerNeededTaskAdapter(taskFreelancer)
        val item= getItem(holder.adapterPosition)
        var listTodo= mutableListOf<TodoId>()
        if(item.todo!=null && item.todo!!.size>0){
            for (todo in item.todo!!){
                listTodo.add(TodoId(id = UUID.randomUUID().toString(), todo = todo))
            }
        }else{
            listTodo= mutableListOf(TodoId(id = UUID.randomUUID().toString(), todo = ""))
        }
        holder.binding.run{
            tvNumberFreelancer.text = holder.itemView.context.getString(R.string.number_of_freelancer, holder.adapterPosition+1)

            inputFreelancerFee.setText(item.price.toString())

            inputFreelancerFee.doAfterTextChanged {
                validateRequire(it.toString(),txtInputFreelancerFee, holder.itemView.context.getString(R.string.fee), holder.itemView.context)
            }

            rvFreelancerNeededTask.layoutManager = LinearLayoutManager(holder.itemView.context)
            rvFreelancerNeededTask.adapter = adapter
            adapter.submitList(listTodo)
            adapter.notifyDataSetChanged()

//            val todoFreelancer = mutableListOf<String>()
//            for (i in 0.until(listTask.size)){
//                todoFreelancer.add("")
//            }

            onSubmitClick= {
                if(it){
                    validateRequire(inputFreelancerFee.text.toString(),txtInputFreelancerFee, holder.itemView.context.getString(R.string.fee), holder.itemView.context)
                }

                adapter.onSubmitNeedClick?.invoke(true)
            }

            inputFreelancerFee.doAfterTextChanged {
                if(it?.trim()?.isNotEmpty() == true){
                    onInputFeeChange?.invoke(holder.adapterPosition, it.toString())
                }
            }

            adapter.onTodoChange={ position: Int, value: String ->
                listTodo[position].todo=value
                Log.d("hasil_get_create","todoFreelancer: ${listTodo.toList()}")
                val justListTodo = mutableListOf<String>()
                for (todos in listTodo){
                    justListTodo.add(todos.todo)
                }
                onInputTodoChange?.invoke(holder.adapterPosition, justListTodo)
            }

            btnAddNewTask.setOnClickListener {
                listTodo.add(TodoId(id=UUID.randomUUID().toString(),todo = ""))
//                todoFreelancer.add("")
                adapter.submitList(listTodo)
                adapter.notifyItemInserted(listTodo.size-1)
            }

            adapter.onBtnDeleteClick={ idTask->
                Log.d("hasil_get_need","id task: $idTask")
                Log.d("hasil_get_need","list task sebelum filter: ${listTodo.toList()}")
                val idPosition = listTodo.indexOfFirst {
                    it.id==idTask
                }
                listTodo.removeAt(idPosition)
                Log.d("hasil_get_need","list task setelah filter: ${listTodo.toList()}")
                val justListTodo = mutableListOf<String>()
                for (todos in listTodo){
                    justListTodo.add(todos.todo)
                }
                onDeleteTodoCallback?.invoke(holder.adapterPosition, justListTodo)
                adapter.submitList(listTodo)
                adapter.notifyItemRemoved(idPosition)
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