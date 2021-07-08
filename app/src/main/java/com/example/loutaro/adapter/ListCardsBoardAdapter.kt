package com.example.loutaro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.loutaro.R
import com.example.loutaro.data.entity.Card
import com.example.loutaro.data.entity.ListCard
import com.example.loutaro.databinding.ItemRowListCardBoardBinding

class ListCardsBoardAdapter: ListAdapter<ListCard, ListCardsBoardAdapter.ListViewHolder>(DiffCallback()) {
    private var listCardBoardAdapter: ListCardBoardAdapter?=null
    lateinit var pullToRefresh: SwipeRefreshLayout

    inner class ListViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRowListCardBoardBinding.bind(itemView)
        fun bind(listCard: ListCard) {
            pullToRefresh= SwipeRefreshLayout(itemView.context)
            binding.run {
                val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                itemTouchHelper.attachToRecyclerView(rvCardBoard)

                tvListCardsName.text = listCard.name
                rvCardBoard.layoutManager= LinearLayoutManager(itemView.context)
                rvCardBoard.adapter= listCardBoardAdapter
                listCardBoardAdapter?.submitList(mutableListOf(
                        Card(idCard = "1",name = "satu"),
                        Card(idCard = "2",name = "dua"),
                        Card(idCard = "3",name = "tiga")
                ))
                listCardBoardAdapter?.notifyDataSetChanged()


            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<ListCard>() {
        override fun areItemsTheSame(oldItem: ListCard, newItem: ListCard): Boolean {
            return oldItem.idListCard==newItem.idListCard
        }

        override fun areContentsTheSame(oldItem: ListCard, newItem: ListCard): Boolean {
            return oldItem==newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.item_row_list_card_board, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        listCardBoardAdapter=null
        listCardBoardAdapter = ListCardBoardAdapter()
        val listCard= getItem(holder.adapterPosition)
        holder.bind(listCard)
    }

    private val itemTouchHelperCallback = object: ItemTouchHelper.Callback() {
        override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
        ): Int {
            // Specify the directions of movement
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
        ): Boolean {
            // Notify your adapter that an item is moved from x position to y position
            listCardBoardAdapter?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            // true: if you want to start dragging on long press
            // false: if you want to handle it yourself
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            // Hanlde action state changes
            val swiping = actionState == ItemTouchHelper.ACTION_STATE_DRAG
            pullToRefresh.isEnabled = !swiping
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            // Called by the ItemTouchHelper when the user interaction with an element is over and it also completed its animation
            // This is a good place to send update to your backend about changes
        }
    }
}