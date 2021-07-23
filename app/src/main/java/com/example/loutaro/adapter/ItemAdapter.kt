package com.example.loutaro.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import com.amulyakhare.textdrawable.TextDrawable
import com.bumptech.glide.Glide
import com.example.loutaro.R
import com.woxthebox.draglistview.DragItemAdapter
import java.util.*

internal class ItemAdapter(list: ArrayList<Pair<Long?, String?>?>?, private val mLayoutId: Int, private val mGrabHandleId: Int, private val mDragOnLongPress: Boolean) : DragItemAdapter<Pair<Long?, String?>?, ItemAdapter.ViewHolder>() {

    var onCardClickCallback:((Int)-> Unit)?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val text = mItemList[position]!!.second!!
        val indexName = text.indexOf("#%name%#")
        val indexPhoto = text.indexOf("#%photo%#")
        if(indexName!=-1){
            val nameFreelancer = text.substring(indexName+8,text.length)
            val nameCard = text.substring(0,indexName)
            val drawable = TextDrawable.builder()
                    .buildRect("${nameFreelancer[0]}", ContextCompat.getColor(holder.itemView.context, R.color.secondary))
            holder.mPhoto.visibility=View.VISIBLE
            holder.mCvPhoto.visibility = View.VISIBLE
            holder.mPhoto.setImageDrawable(drawable)
            holder.mText.text = nameCard
        }
        if(indexPhoto!=-1){
            val photoUrl = text.substring(indexPhoto+9, text.length)
            Log.d("hasil_photo","ini link photo $photoUrl")
            var nameCard = text.substring(0, indexPhoto)
            holder.mPhoto.visibility=View.VISIBLE
            holder.mCvPhoto.visibility = View.VISIBLE
            Glide.with(holder.itemView.context)
                    .load(photoUrl)
                    .into(holder.mPhoto)
            holder.mText.text = nameCard
        }
        if(indexName==-1 && indexPhoto==-1){
            holder.mText.text = text
            holder.mPhoto.visibility = View.GONE
            holder.mCvPhoto.visibility = View.GONE
        }
        holder.itemView.tag = mItemList[position]
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position]!!.first!!
    }

    internal inner class ViewHolder(itemView: View) : DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {
        var mText: TextView
        var mPhoto: ImageView
        var mCvPhoto: CardView
        override fun onItemClicked(view: View) {
            onCardClickCallback?.invoke(adapterPosition)
        }

        override fun onItemLongClicked(view: View): Boolean {
//            Toast.makeText(view.context, "Item long clicked", Toast.LENGTH_SHORT).show()
            return true
        }

        init {
            mText = itemView.findViewById<View>(R.id.text) as TextView
            mPhoto = itemView.findViewById<View>(R.id.img_avatar_member_card) as ImageView
            mCvPhoto = itemView.findViewById<View>(R.id.cv_img_avatar_member_card) as CardView
        }
    }

    init {
        itemList = list
    }
}