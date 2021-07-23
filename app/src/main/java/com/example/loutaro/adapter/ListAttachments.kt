package com.example.loutaro.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.loutaro.data.entity.AttachLink
import com.example.loutaro.data.entity.Project
import com.example.loutaro.databinding.AttachALinkBinding
import com.example.loutaro.databinding.ItemRowAttachmentBinding
import com.example.loutaro.ui.boardKanban.BoardKanbanActivity
import com.example.loutaro.ui.createProject.CreateProjectActivity
import com.example.loutaro.ui.paymentProject.PaymentProjectActivity
import com.example.loutaro.ui.project.activeProject.applyers.ApplyersActivity
import com.example.loutaro.ui.projectDetail.ProjectDetailActivity

class ListAttachments(private val idUser: String): ListAdapter<AttachLink, ListAttachments.ListViewHolder>(DiffCallback()) {

    var onEditClickCallback:((AttachLink)-> Unit)?=null
    var onDeleteClickCallback:((AttachLink)-> Unit)?=null

    class DiffCallback: DiffUtil.ItemCallback<AttachLink>() {
        override fun areItemsTheSame(oldItem: AttachLink, newItem: AttachLink): Boolean {
            return oldItem.name==newItem.name && oldItem.link==newItem.link
        }

        override fun areContentsTheSame(oldItem: AttachLink, newItem: AttachLink): Boolean {
            return oldItem==newItem
        }

    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowAttachmentBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_attachment, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val attachments = getItem(holder.adapterPosition)
        holder.binding.tvAttachmentLinkName.setText(attachments.name)
        holder.binding.tvAttachmentLinkURL.setText(attachments.link)

        holder.binding.parentLayoutListAttachment.setOnClickListener {
            var url = attachments.link
            if(url!=null){
                if (!url.startsWith("http://") && !url.startsWith("https://")){
                    url = "http://" + url;
                }
                val intentGoToInternet = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                holder.itemView.context.startActivity(intentGoToInternet)
            }

        }

        if(idUser==attachments.attachedBy){
            holder.binding.optionAttachments.visibility = View.VISIBLE
            holder.binding.optionAttachments.setOnClickListener { v->
                showMenu(v, R.menu.attachments_options, holder.itemView.context, attachments)
            }
        }else{
            holder.binding.optionAttachments.visibility = View.GONE
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, attachLink: AttachLink) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId){
              R.id.edit_attachment_menu -> {
                  onEditClickCallback?.invoke(attachLink)
                  true
              }
              R.id.delete_attachment_menu->{
                  onDeleteClickCallback?.invoke(attachLink)
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