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
import com.example.loutaro.ui.projectDetail.ProjectDetailActivity

class ListTitleProjectAdapter(private val isBusinessMan: Boolean=false): ListAdapter<Project, ListTitleProjectAdapter.ListViewHolder>(DiffCallback()) {

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
                showMenu(v, R.menu.popup_menu_project_option, holder.itemView.context, project.idProject, project.idBoards)
            }
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, idProject: String?, idBoards: String?) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.menu.findItem(R.id.menu_update_project).isVisible = isBusinessMan

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.menu_board_kanban->{
                    val boardKanbanIntent = Intent(context, BoardKanbanActivity::class.java)
                    boardKanbanIntent.putExtra(BoardKanbanActivity.EXTRA_ID_BOARDS, idBoards)
                    context.startActivity(boardKanbanIntent)
                    true
                }
                R.id.menu_detail_project->{
                   val detailProjectIntent = Intent(context, ProjectDetailActivity::class.java)
                    detailProjectIntent.putExtra(ProjectDetailActivity.EXTRA_ID_PROJECT, idProject)
                    context.startActivity(detailProjectIntent)
                    true
                }
                R.id.menu_update_project->{
                    val createProjectIntent = Intent(context, CreateProjectActivity::class.java)
                    createProjectIntent.putExtra(CreateProjectActivity.EXTRA_ID_PROJECT, idProject)
                    context.startActivity(createProjectIntent)
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