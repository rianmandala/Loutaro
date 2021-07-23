package com.example.loutaro.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.loutaro.R
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.entity.ProjectRecommendation
import com.example.loutaro.databinding.ItemRowProjectBinding
import com.example.loutaro.ui.projectDetail.ProjectDetailActivity
import com.example.loutaro.ui.projectDetail.ProjectDetailActivity.Companion.EXTRA_ID_PROJECT
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager

class ListProjectRecommendationAdapter : ListAdapter<ProjectRecommendation, ListProjectRecommendationAdapter.ListViewHolder>(DiffCalback()) {
    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowProjectBinding.bind(itemView)
    }

    class DiffCalback: DiffUtil.ItemCallback<ProjectRecommendation>() {
        override fun areItemsTheSame(
            oldItem: ProjectRecommendation,
            newItem: ProjectRecommendation
        ): Boolean {
            return oldItem.idProject==newItem.idProject
        }

        override fun areContentsTheSame(
            oldItem: ProjectRecommendation,
            newItem: ProjectRecommendation
        ): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_project, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val project = getItem(holder.adapterPosition)
        val adapter = ListStandardSkillAdapter()
        holder.binding.run {
            val timePosted = android.text.format.DateUtils.getRelativeTimeSpanString(project.timeStamp?.toDate()?.time!!.toLong())
            tvTitlePreviewProject.text = project.title
            tvDescriptionPreviewProject.text = project.description
//            tvTimePostedPreviewProject.text = holder.itemView.resources.getString(R.string.posted_time, timePosted)
            tvTimePostedPreviewProject.text = holder.itemView.resources.getString(R.string.match_to_your_project, project.recommendationInPercent?.toInt())

            tvBudgetPreviewProject.text = holder.itemView.resources.getString(R.string.budget, project.budget)
            rvStandardSkillPreviewProject.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            rvStandardSkillPreviewProject.adapter = adapter
            adapter.submitList(project.skills)

            parentLayoutProject.setOnClickListener {
                goToDetailProject(holder.itemView.context, project.idProject.toString())
            }
        }
    }

    fun goToDetailProject(context: Context, idProject: String){
        val detailIntent = Intent(context, ProjectDetailActivity::class.java)
        detailIntent.putExtra(EXTRA_ID_PROJECT, idProject)
        context.startActivity(detailIntent)
    }
}