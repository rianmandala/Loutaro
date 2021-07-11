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
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.example.loutaro.data.entity.Contact
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.databinding.ItemRowApplierNotificationBinding
import com.example.loutaro.ui.boardKanban.BoardKanbanActivity
import com.example.loutaro.ui.createProject.CreateProjectActivity
import com.example.loutaro.ui.freelancerDetail.FreelancerDetailActivity
import com.example.loutaro.ui.projectDetail.ProjectDetailActivity

class ListApplierNotificationAdapter: ListAdapter<Freelancer, ListApplierNotificationAdapter.ListViewHolder>(DiffCallback()) {

    var onContactClickCallback:((String, String)->Unit)?=null

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowApplierNotificationBinding.bind(itemView)

        fun bind(freelancer: Freelancer) {
            binding.run {
                Glide.with(itemView.context)
                    .load(freelancer.photo)
                    .into(imgAvatarApplier)

                tvNameApplier.text = freelancer.name
                tvEmailApplier.text = freelancer.email
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Freelancer>() {
        override fun areItemsTheSame(oldItem: Freelancer, newItem: Freelancer): Boolean {
            return oldItem.idFreelancer==newItem.idFreelancer
        }

        override fun areContentsTheSame(oldItem: Freelancer, newItem: Freelancer): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row_applier_notification,parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val freelancer = getItem(holder.adapterPosition)
        holder.bind(freelancer)
        holder.binding.tvApplyAsFreelancer.text = holder.itemView.context.getString(R.string.apply_as_freelancer,holder.adapterPosition+1)

        holder.binding.imgBtnOptionContact.setOnClickListener {v: View ->
            freelancer.contact?.email= freelancer.email
            freelancer.contact?.let {
                showMenu(v, R.menu.popup_menu_contact, holder.itemView.context,
                    it
                )
            }
        }

        holder.binding.parentLayoutApplierNotification.setOnClickListener {
            val profileIntent = Intent(holder.itemView.context, FreelancerDetailActivity::class.java)
            profileIntent.putExtra(FreelancerDetailActivity.EXTRA_ID_FREELANCER, freelancer.idFreelancer)
            holder.itemView.context.startActivity(profileIntent)
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, context: Context, contact: Contact) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.menu.findItem(R.id.menu_contact_phone).isVisible = contact.telephone!=null
        popup.menu.findItem(R.id.menu_contact_email).isVisible = contact.email!=null
        popup.menu.findItem(R.id.menu_contact_telegram).isVisible = contact.telegram!=null

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.menu_contact_phone->{
                    onContactClickCallback?.invoke(context.getString(R.string.phone), contact.telephone.toString())
                    true
                }
                R.id.menu_contact_email->{
                    onContactClickCallback?.invoke(context.getString(R.string.email), contact.email.toString())
                    true
                }
                R.id.menu_contact_telegram->{
                    onContactClickCallback?.invoke(context.getString(R.string.telegram), contact.telegram.toString())
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