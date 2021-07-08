package com.example.loutaro.ui.boardKanban

import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.loutaro.R
import com.example.loutaro.adapter.ListCardBoardAdapter
import com.example.loutaro.adapter.ListCardsBoardAdapter
import com.example.loutaro.data.entity.ListCard
import com.example.loutaro.databinding.ActivityBoardKanbanBinding
import java.lang.Math.abs

class BoardKanbanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBoardKanbanBinding
    private lateinit var listCardsBoardAdapter: ListCardsBoardAdapter
    private lateinit var pullToRefresh: SwipeRefreshLayout
    companion object{
        val EXTRA_ID_BOARDS= "EXTRA ID BOARDS"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardKanbanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listCardsBoardAdapter= ListCardsBoardAdapter()
        binding.run {
            vpListCardsBoard.adapter= listCardsBoardAdapter
            listCardsBoardAdapter.submitList(mutableListOf(
                ListCard(idListCard = "1", name = "todo"),
                ListCard(idListCard = "2", name = "done")
            ))
            listCardsBoardAdapter.notifyDataSetChanged()


            // You need to retain one page on each side so that the next and previous items are visible
            vpListCardsBoard.offscreenPageLimit = 1

// Add a PageTransformer that translates the next and previous items horizontally
// towards the center of the screen, which makes them visible
            val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
            val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
            val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
            val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
                page.translationX = -pageTranslationX * position
                // Next line scales the item's height. You can remove it if you don't want this effect
                page.scaleY = 1F
                // If you want a fading effect uncomment the next line:
                // page.alpha = 0.25f + (1 - abs(position))
            }
            vpListCardsBoard.setPageTransformer(pageTransformer)

            // The ItemDecoration gives the current (centered) item horizontal margin so that
            // it doesn't occupy the whole screen width. Without it the items overlap
            val itemDecoration = HorizontalMarginItemDecoration(
                    this@BoardKanbanActivity,
                    R.dimen.viewpager_current_item_horizontal_margin
            )
            vpListCardsBoard.addItemDecoration(itemDecoration)


        }

//        pullToRefresh= SwipeRefreshLayout(this)
//        binding.run {
//            rvListCardsBoard.layoutManager = LinearLayoutManager(this@BoardKanbanActivity, LinearLayoutManager.HORIZONTAL,false)
//            rvListCardsBoard.adapter= listCardsBoardAdapter
//            listCardsBoardAdapter.submitList(mutableListOf(
//                ListCard(idListCard = "1", name = "todo"),
//                ListCard(idListCard = "2", name = "done")
//            ))
//            listCardsBoardAdapter.notifyDataSetChanged()
//
//            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
//            itemTouchHelper.attachToRecyclerView(rvListCardsBoard)
//        }
    }

//    private val itemTouchHelperCallback = object: ItemTouchHelper.Callback() {
//        override fun getMovementFlags(
//            recyclerView: RecyclerView,
//            viewHolder: RecyclerView.ViewHolder
//        ): Int {
//            // Specify the directions of movement
//            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
//            return makeMovementFlags(dragFlags, 0)
//        }
//
//        override fun onMove(
//            recyclerView: RecyclerView,
//            viewHolder: RecyclerView.ViewHolder,
//            target: RecyclerView.ViewHolder
//        ): Boolean {
//            // Notify your adapter that an item is moved from x position to y position
//            listCardsBoardAdapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
//            return true
//        }
//
//        override fun isLongPressDragEnabled(): Boolean {
//            // true: if you want to start dragging on long press
//            // false: if you want to handle it yourself
//            return true
//        }
//
//        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//
//        }
//
//        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
//            super.onSelectedChanged(viewHolder, actionState)
//            // Hanlde action state changes
////            val swiping = actionState == ItemTouchHelper.ACTION_STATE_DRAG
////            pullToRefresh.isEnabled = !swiping
//        }
//
//        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
//            super.clearView(recyclerView, viewHolder)
//            // Called by the ItemTouchHelper when the user interaction with an element is over and it also completed its animation
//            // This is a good place to send update to your backend about changes
//        }
//    }
}

class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) :
        RecyclerView.ItemDecoration() {

    private val horizontalMarginInPx: Int =
            context.resources.getDimension(horizontalMarginInDp).toInt()

    override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }

}