package com.example.loutaro.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.loutaro.R
import com.example.loutaro.data.entity.Project
import com.example.loutaro.databinding.ItemRowTitleProjectBinding
import com.example.loutaro.ui.boardKanban.BoardKanbanActivity
import com.example.loutaro.ui.createProject.CreateProjectActivity
import com.example.loutaro.ui.paymentProject.PaymentProjectActivity
import com.example.loutaro.ui.project.activeProject.applyers.ApplyersActivity
import com.example.loutaro.ui.projectDetail.ProjectDetailActivity

class ListTitleProjectAdapter(private val isBusinessMan: Boolean=false, private val paymentProject:Boolean=false, private val isProjectCompleted:Boolean=false): ListAdapter<Project, ListTitleProjectAdapter.ListViewHolder>(DiffCallback()) {

    var onClickDeleteProjectCallback:((String)-> Unit)?=null
    var onClickProjectCompletedCallback:((String)-> Unit)?=null
    var onClickStartProjectCallback:((String, Int)-> Unit)?=null

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowTitleProjectBinding.bind(itemView)
    }

    class DiffCallback: DiffUtil.ItemCallback<Project>() {
        override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem.idProject == newItem.idProject
        }

        override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_title_project, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val project = getItem(holder.adapterPosition)
        holder.binding.run {
            tvRowTitleProject.text = project.title
            imgBtnMoreOption.setOnClickListener {v: View ->
                showMenu(v, R.menu.popup_menu_project_option, holder.itemView.context, project)
            }
            parentLayoutTitleProject.setOnClickListener {
                val detailProjectIntent = Intent(holder.itemView.context, ProjectDetailActivity::class.java)
                detailProjectIntent.putExtra(ProjectDetailActivity.EXTRA_ID_PROJECT,project?.idProject)
                holder.itemView.context.startActivity(detailProjectIntent)
            }
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, project: Project?) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.menu.findItem(R.id.menu_detail_project).isVisible = false
        popup.menu.findItem(R.id.menu_update_project).isVisible = isBusinessMan
        popup.menu.findItem(R.id.menu_delete_project).isVisible = isBusinessMan
        popup.menu.findItem(R.id.menu_applyers).isVisible = isBusinessMan
        if (project != null) {
            popup.menu.findItem(R.id.menu_start_project).isVisible = isBusinessMan && project.projectDueDate==null
            popup.menu.findItem(R.id.menu_project_completed).isVisible = isBusinessMan && project.projectDueDate!=null
        }

        popup.menu.findItem(R.id.menu_payment_project).isVisible = paymentProject

        if(paymentProject){
            popup.menu.findItem(R.id.menu_delete_project).isVisible = true
            popup.menu.findItem(R.id.menu_update_project).isVisible = false
            popup.menu.findItem(R.id.menu_board_kanban).isVisible = false
        }

        if(isProjectCompleted){
            popup.menu.findItem(R.id.menu_delete_project).isVisible = true
        }

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.menu_board_kanban->{
                    val boardKanbanIntent = Intent(context, BoardKanbanActivity::class.java)
                    boardKanbanIntent.putExtra(BoardKanbanActivity.EXTRA_ID_BOARDS,project?.idBoards)
                    boardKanbanIntent.putExtra(BoardKanbanActivity.EXTRA_ID_PROJECT,project?.idProject)
                    context.startActivity(boardKanbanIntent)
                    true
                }
                R.id.menu_detail_project->{
                   val detailProjectIntent = Intent(context, ProjectDetailActivity::class.java)
                    detailProjectIntent.putExtra(ProjectDetailActivity.EXTRA_ID_PROJECT,project?.idProject)
                    context.startActivity(detailProjectIntent)
                    true
                }
                R.id.menu_applyers->{
                    val applyersIntent = Intent(context, ApplyersActivity::class.java)
                    applyersIntent.putExtra(ApplyersActivity.EXTRA_ID_PROJECT,project?.idProject)
                    context.startActivity(applyersIntent)
                    true
                }
                R.id.menu_update_project->{
                    val createProjectIntent = Intent(context, CreateProjectActivity::class.java)
                    createProjectIntent.putExtra(CreateProjectActivity.EXTRA_ID_PROJECT,project?.idProject)
                    context.startActivity(createProjectIntent)
                    true
                }
                R.id.menu_delete_project->{
                    onClickDeleteProjectCallback?.invoke(project?.idProject.toString())
                    true
                }
                R.id.menu_payment_project->{
                    var pricePayment = 0
                    if(project?.tasks!=null){
                        for(item in project.tasks!!){
                            pricePayment+= item.price!!
                        }
                    }

                    val paymentProjectIntent = Intent(context, PaymentProjectActivity::class.java)
                    paymentProjectIntent.putExtra(PaymentProjectActivity.EXTRA_STATUS_PAYMENT, project?.paymentStatus)
                    paymentProjectIntent.putExtra(PaymentProjectActivity.EXTRA_PRICE_PAYMENT, pricePayment)
                    paymentProjectIntent.putExtra(PaymentProjectActivity.EXTRA_ID_PROJECT, project?.idProject)
                    context.startActivity(paymentProjectIntent)
                    true
                }
                R.id.menu_project_completed->{
                    onClickProjectCompletedCallback?.invoke(project?.idProject.toString())
                    true
                }
                R.id.menu_start_project -> {
                    onClickStartProjectCallback?.invoke(project?.idProject.toString(), project?.durationInDays!!)
                    true
                }
                else->{
                    false
                }
            }
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }
}