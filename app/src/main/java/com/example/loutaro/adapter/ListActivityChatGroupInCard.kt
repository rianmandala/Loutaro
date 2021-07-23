package com.example.loutaro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.data.entity.Activity
import com.example.loutaro.data.entity.AttachLink
import com.example.loutaro.databinding.ActivityChatGroupInCardBinding

class ListActivityChatGroupInCard(private val idCurrentUser: String): ListAdapter<Activity, ListActivityChatGroupInCard.ListViewHolder>(DiffCallback()) {

    var onEditClickCallback:((Activity)-> Unit)?=null
    var onDeleteClickCallback:((Activity)-> Unit)?=null

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ActivityChatGroupInCardBinding.bind(itemView)

        fun bind(activity: Activity) {
            binding.run {
                val drawable = TextDrawable.builder()
                        .buildRect("${activity.name?.get(0)}", ContextCompat.getColor(itemView.context, R.color.secondary))
                if(activity.photo==null){
                    imgAvatarUserInChat.setImageDrawable(drawable)
                }else{
                    Glide.with(itemView.context)
                            .load(activity.photo)
                            .into(imgAvatarUserInChat)
                }

                tvNameUserInChat.text = activity.name
                tvMessageInChat.text = activity.message
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Activity>() {
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.idUser==newItem.idUser && oldItem.message==newItem.message && oldItem.name==newItem.name
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_chat_group_in_card,parent,false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val activity= getItem(holder.adapterPosition)
        holder.bind(activity)

        holder.binding.imgBtnOptionInChat.setOnClickListener {
            showMenu(it,R.menu.attachments_options,holder.itemView.context, activity)
        }

        if(idCurrentUser!=activity.idUser){
            holder.binding.imgBtnOptionInChat.visibility = View.GONE
        }else{
            holder.binding.imgBtnOptionInChat.visibility = View.VISIBLE
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, activity: Activity) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.edit_attachment_menu -> {
                    onEditClickCallback?.invoke(activity)
                    true
                }
                R.id.delete_attachment_menu->{
                    onDeleteClickCallback?.invoke(activity)
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