package com.example.loutaro.adapter

import android.content.Context
import android.content.Intent
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
import com.example.loutaro.data.entity.Contact
import com.example.loutaro.data.entity.FreelancerRecommendation
import com.example.loutaro.databinding.ItemRowApplierNotificationBinding
import com.example.loutaro.ui.freelancerDetail.FreelancerDetailActivity

class ListFreelancerRecommendationAdapter: ListAdapter<FreelancerRecommendation, ListFreelancerRecommendationAdapter.ListViewHolder>(DiffCallback()) {

    var onContactClickCallback:((String, String)->Unit)?=null

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowApplierNotificationBinding.bind(itemView)

        fun bind(freelancer: FreelancerRecommendation) {
            val drawable = TextDrawable.builder()
                .buildRect("${freelancer.name?.get(0)}", ContextCompat.getColor(itemView.context, R.color.secondary))
            binding.run {
                if(freelancer.photo!=null){
                    Glide.with(itemView.context)
                        .load(freelancer.photo)
                        .into(imgAvatarApplier)
                }else{
                    imgAvatarApplier.setImageDrawable(drawable)
                }

                tvNameApplier.text = itemView.context.getString(R.string.match_to_your_project, freelancer.recommendationInPercent?.toInt())
                tvNameApplier.setTextColor(ContextCompat.getColor(itemView.context, R.color.primary))
                tvEmailApplier.text = freelancer.email
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<FreelancerRecommendation>() {
        override fun areItemsTheSame(
            oldItem: FreelancerRecommendation,
            newItem: FreelancerRecommendation
        ): Boolean {
            return oldItem.idFreelancer==newItem.idFreelancer
        }

        override fun areContentsTheSame(
            oldItem: FreelancerRecommendation,
            newItem: FreelancerRecommendation
        ): Boolean {
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
        holder.binding.tvApplyAsFreelancer.text = freelancer.skills?.joinToString(",")

        holder.binding.imgBtnOptionContact.setOnClickListener {v: View ->
            if(freelancer.contact==null){
                freelancer.contact= Contact(email = freelancer.email)
            }
            showMenu(v, R.menu.popup_menu_contact, holder.itemView.context,
                    freelancer.contact!!
            )
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
        popup.menu.findItem(R.id.menu_confirm_applyers).isVisible = false

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