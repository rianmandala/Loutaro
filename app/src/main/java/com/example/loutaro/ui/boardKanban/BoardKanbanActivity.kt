package com.example.loutaro.ui.boardKanban

import android.animation.ObjectAnimator
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loutaro.R
import com.example.loutaro.adapter.ItemAdapter
import com.example.loutaro.data.entity.*
import com.example.loutaro.databinding.ActivityBoardKanbanBinding
import com.example.loutaro.databinding.ColumnFooterBinding
import com.example.loutaro.databinding.ColumnHeaderBinding
import com.example.loutaro.databinding.ProjectDeadlineBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.boardKanban.detailCard.DetailCardActivity
import com.example.loutaro.ui.members.MembersActivity
import com.example.loutaro.viewmodel.ViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.BoardView.BoardCallback
import com.woxthebox.draglistview.BoardView.BoardListener
import com.woxthebox.draglistview.ColumnProperties
import com.woxthebox.draglistview.DragItem
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*


class BoardKanbanActivity : BaseActivity() {
    private lateinit var binding: ActivityBoardKanbanBinding
    private var sCreatedItems = 0
    private lateinit var mBoardView: BoardView
    private var mColumns = 0
    private val mGridLayout = false
    private var oldColumnPositionGlobal=0
    private var newColumnPositionGlobal=0
    var idBoardsGlobal=""
    var idProjectGlobal=""
    lateinit var detailProject: Project
    var listFreelancerInCard= mutableListOf<Freelancer>()
    var listBoardsColumnGlobal:MutableList<BoardsColumn?>? = mutableListOf()
    private val boardKanbanViewModel: BoardKanbanViewModel by viewModels {
        ViewModelFactory.getInstance()
    }
    companion object{
        var EXTRA_ID_BOARDS="EXTRA ID BOARDS"
        var EXTRA_ID_PROJECT="EXTRA ID PROJECT"
        var EXTRA_REQUEST=123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoardKanbanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activatedToolbarBackButton()

        initTinyDB(this)

        idBoardsGlobal = intent.getStringExtra(EXTRA_ID_BOARDS).toString()
        idProjectGlobal= intent.getStringExtra(EXTRA_ID_PROJECT).toString()

        boardKanbanViewModel.getDetailProject(idProjectGlobal).addSnapshotListener(this) { querySnapshot, error ->
            querySnapshot?.let {
                val response = it.toObject(Project::class.java)
                if(response!=null){
                    detailProject = response
                }
            }
        }

        boardKanbanViewModel.responseGetDetailDataBoards.observe(this){ boards->
            if(boards!=null){
                setToolbarTitle(getString(R.string.manage_kanban))
                binding.tvTitleProjectKanban.text = boards.name
                listBoardsColumnGlobal= boards.columns
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = boardKanbanViewModel.getDetailDataBoards(idBoardsGlobal).get().await()
                val dataBoards= response.toObject(Boards::class.java)

                val listIdFreelancerInCard = mutableListOf<String>()

                dataBoards?.columns?.forEach { column->
                    column?.cards?.forEach { card->
                        if(card?.member!=null){
                            listIdFreelancerInCard.add(card.member!!)
                        }
                    }
                }

                if(listIdFreelancerInCard.isNotEmpty()){
                    val responseListFreelancerInCard = boardKanbanViewModel.getListFreelancerInCard(listIdFreelancerInCard).get().await()
                    val data = mutableListOf<Freelancer>()
                    for(document in responseListFreelancerInCard.documents){
                        val result = document.toObject(Freelancer::class.java)
                        if(result!=null){
                            result.idFreelancer = document.id
                            data.add(result)
                        }
                    }

                    listFreelancerInCard = data
                }

                listBoardsColumnGlobal = dataBoards?.columns
                if(listBoardsColumnGlobal!=null){
                    for (column in listBoardsColumnGlobal!!){
                        Log.d("list_board", "list board $column")
                        val job = GlobalScope.launch {
                            addColumn(nameColumn = column?.name.toString(), column?.cards)
                        }
                        job.join()
                    }
                    if(getUserTypeLogin() == getString(R.string.value_business_man)){
                        addColumn()
                    }
                }else{
                    addColumn()
                }
            }catch (e: Exception){
                Log.e("error", "Error when try to get detail board ${e.message}")
            }
        }


        mBoardView = binding.boardView
        mBoardView.setColumnWidth(580)
        mBoardView.setColumnSpacing(24)
        mBoardView.setBoardEdge(48)
        mBoardView.setSnapToColumnsWhenScrolling(true)
        mBoardView.setSnapToColumnWhenDragging(true)
        mBoardView.setSnapDragItemToTouch(true)
        mBoardView.setSnapToColumnInLandscape(false)
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)
        mBoardView.setBoardListener(object : BoardListener {
            override fun onItemDragStarted(column: Int, row: Int) {
                //Toast.makeText(getContext(), "Start - column: " + column + " row: " + row, Toast.LENGTH_SHORT).show();
            }

            override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
                if (fromColumn != toColumn) {
//                    showSnackbar("End - column: $toColumn row: $toRow")
                    Log.d("hasil_get_item", "ini from column ke $fromColumn row ke $fromRow ${mBoardView.getAdapter(fromColumn).itemList}")
                    Log.d("hasil_get_item", "ini to column ke $toColumn row ke $toRow ${mBoardView.getAdapter(toColumn).itemList}")
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val cardMovement = listBoardsColumnGlobal?.get(fromColumn)?.cards?.get(fromRow)
                            listBoardsColumnGlobal?.get(fromColumn)?.cards?.removeAt(fromRow)
                            if (listBoardsColumnGlobal?.get(toColumn)?.cards == null) {
                                listBoardsColumnGlobal?.get(toColumn)?.cards = mutableListOf()
                            }
                            listBoardsColumnGlobal?.get(toColumn)?.cards?.add(toRow, cardMovement)
                            boardKanbanViewModel.updateBoardsColumn(idBoardsGlobal, listBoardsColumnGlobal).await()
                        } catch (e: Exception) {
                            Log.e("error", "error when try to save movement position card to another column ${e.message}")
                        }
                    }
                } else if (fromRow != toRow) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val itemList = mBoardView.getAdapter(fromColumn).itemList as ArrayList<Pair<Long?, String?>?>?
                            val newCard = itemList?.map {
                                it?.second
                            }
                            Log.d("hasil_get_item", "Hasil jerih payah: ${newCard}")
                            if (newCard != null) {
                                for (i in 0.until(newCard.size)) {
                                    val indexNewCard = listBoardsColumnGlobal?.get(fromColumn)?.cards?.indexOfFirst {
                                        val text = newCard[i]
                                        var filterCard = ""
                                        val indexName = text?.indexOf("#%name%#")
                                        val indexPhoto = text?.indexOf("#%photo%#")
                                        if (indexName != -1 && indexName != null) {
                                            filterCard = text.substring(0, indexName)
                                        }
                                        if (indexPhoto != -1 && indexPhoto != null) {
                                            filterCard = text.substring(0, indexPhoto)
                                        }
                                        if (indexName == -1 && indexPhoto == -1) {
                                            filterCard = text
                                        }
                                        it?.name == filterCard
                                    }
                                    Log.d("hasil_get_item", "indexNewCard $indexNewCard}")
                                    if (indexNewCard != null) {
                                        Collections.swap(listBoardsColumnGlobal?.get(fromColumn)?.cards, i, indexNewCard)
                                    }
                                }
                                Log.d("hasil_get_item", "hasil newCard ${listBoardsColumnGlobal?.get(fromColumn)?.cards?.toList()}")
                                boardKanbanViewModel.updateBoardsColumn(idBoardsGlobal, listBoardsColumnGlobal).await()
                            }

                        } catch (e: Exception) {
                            Log.e("error", "error when try to save movement position card ${e.message}")
                        }
                    }

                }
            }

            override fun onItemChangedPosition(oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int) {
//                showSnackbar("Position changed - column: $newColumn row: $newRow")
            }

            override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {
                val itemCount1 = mBoardView.getHeaderView(oldColumn).findViewById<TextView>(R.id.item_count)
                itemCount1.text = mBoardView.getAdapter(oldColumn).itemCount.toString()
                val itemCount2 = mBoardView.getHeaderView(newColumn).findViewById<TextView>(R.id.item_count)
                itemCount2.text = mBoardView.getAdapter(newColumn).itemCount.toString()
            }

            override fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int) {
                //Toast.makeText(getContext(), "Focused column changed from " + oldColumn + " to " + newColumn, Toast.LENGTH_SHORT).show();
            }

            override fun onColumnDragStarted(position: Int) {
                oldColumnPositionGlobal = position

                val tvTagListName = mBoardView.getHeaderView(position).findViewById<TextView>(R.id.tv_tag_list_card_name)
                val tvListName = mBoardView.getHeaderView(position).findViewById<TextView>(R.id.tv_list_card_name)
                val footerAddCard = mBoardView.getFooterView(position).findViewById<CardView>(R.id.item_layout)
                footerAddCard.visibility = View.GONE

                if (tvListName.text.trim().isEmpty()) {
                    tvTagListName.text = getString(R.string.add_list)
                    footerAddCard.visibility = View.GONE
                } else {
                    tvTagListName.text = tvListName.text
                    footerAddCard.visibility = View.VISIBLE
                }
                //Toast.makeText(getContext(), "Column drag started from " + position, Toast.LENGTH_SHORT).show();
            }

            override fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int) {
//                showSnackbar("Column changed from $oldPosition to $newPosition")
                Log.d("column_drag", "Column changed from $oldPosition to $newPosition")
            }

            override fun onColumnDragEnded(position: Int) {
                newColumnPositionGlobal = position

                val tvTagListName = mBoardView.getHeaderView(position).findViewById<TextView>(R.id.tv_tag_list_card_name)
                val tvListName = mBoardView.getHeaderView(position).findViewById<TextView>(R.id.tv_list_card_name)
                val footerAddCard = mBoardView.getFooterView(position).findViewById<CardView>(R.id.item_layout)
                if (tvListName.text.trim().isNotEmpty()) {
                    tvTagListName.text = tvListName.text
                    footerAddCard.visibility = View.VISIBLE
                } else {
                    tvTagListName.text = getString(R.string.add_list)
                    footerAddCard.visibility = View.GONE
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (oldColumnPositionGlobal != newColumnPositionGlobal) {
                            Collections.swap(listBoardsColumnGlobal, oldColumnPositionGlobal, newColumnPositionGlobal)
                            boardKanbanViewModel.updateBoardsColumn(idBoardsGlobal, listBoardsColumnGlobal).await()
                        }
                    } catch (e: Exception) {
                        Log.e("error", "Error when try to update position column boards")
                    }
                }
//                showSnackbar("Column drag ended from column $oldColumnPositionGlobal to column $newColumnPositionGlobal")
                Log.d("column_drag", "Column drag ended from column $oldColumnPositionGlobal to column $newColumnPositionGlobal")
            }
        })
        mBoardView.setBoardCallback(object : BoardCallback {
            override fun canDragItemAtPosition(column: Int, dragPosition: Int): Boolean {
                // Add logic here to prevent an item to be dragged
                if (getUserTypeLogin() == getString(R.string.value_business_man)) return true
                if (listBoardsColumnGlobal?.get(column)?.cards?.get(dragPosition)?.member == getCurrentUser()?.uid) return true
                showWarningSnackbar(message = getString(R.string.sorry_you_do_not_have_the_authority_to_move_this_task))
                return false
            }

            override fun canDropItemAtPosition(oldColumn: Int, oldRow: Int, newColumn: Int, newRow: Int): Boolean {
                // Add logic here to prevent an item to be dropped
                val isInit = mBoardView.getHeaderView(newColumn).findViewById<TextView>(R.id.tv_tag_list_card_name)
                if (isInit.visibility == View.GONE) return true
                return false
            }

        })

        resetBoard()
    }

    private fun resetBoard() {
        mBoardView.clearBoard()
        mBoardView.setCustomDragItem(if (mGridLayout) null else MyDragItem(this, R.layout.column_item))
        mBoardView.setCustomColumnDragItem(if (mGridLayout) null else MyColumnDragItem(this, R.layout.column_drag_layout))
    }

    private fun addColumn(nameColumn: String = "", listBoardsCard: MutableList<BoardsCard?>? = null) {
        Log.d("hasil_get_board_colum", "name column: $nameColumn")
        val header = ColumnHeaderBinding.inflate(layoutInflater)

        val footer = ColumnFooterBinding.inflate(layoutInflater)
        footer.itemLayout.visibility= View.GONE

        if(getUserTypeLogin() == getString(R.string.value_business_man)){
            header.itemLayout.setOnClickListener { v ->
                header.edtListCardName.visibility = View.VISIBLE
                header.edtListCardName.editText?.setText(header.tvListCardName.text)
                header.tvListCardName.visibility= View.GONE
                header.edtListCardName.editText?.setText(header.tvListCardName.text)
                header.imgBtnDeleteBoardColumn.visibility= View.GONE
                header.edtListCardName.requestFocus()
                //mBoardView.moveItem(4, 0, 0, true);
                //mBoardView.removeItem(column, 0);
                //mBoardView.moveItem(0, 0, 1, 3, false);
                //mBoardView.replaceItem(0, 0, item1, true);
            }
        }

        header.imgBtnDeleteBoardColumn.setOnClickListener {
            try {
                MaterialAlertDialogBuilder(this@BoardKanbanActivity)
                        .setMessage(getString(R.string.are_you_sure_want_to_detele))
                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                            // Respond to negative button press
                        }
                        .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                            showProgressDialog(message = getString(R.string.please_wait))
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    listBoardsColumnGlobal?.removeAt(mBoardView.focusedColumn)
                                    boardKanbanViewModel.updateBoardsColumn(idBoardsGlobal, listBoardsColumnGlobal).await()
                                    withContext(Dispatchers.Main){
                                        mBoardView.removeColumn(mBoardView.focusedColumn)
                                        closeProgressDialog()
                                    }
                                }catch (e: Exception){
                                    Log.e("error", "Error when try to delete list card")
                                }
                            }
                        }
                        .show()
            } catch (e: Exception){
                Log.e("error", "Error when try to delete boardColumn ${e.message}")
            }
        }

        header.edtListCardName.editText?.setOnEditorActionListener { v, actionId, event ->
            if(actionId== EditorInfo.IME_ACTION_DONE){
                if(header.edtListCardName.editText?.text?.trim()?.isNotEmpty() == true){
                    if(header.tvTagListCardName.visibility==View.VISIBLE){
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                boardKanbanViewModel.addBoardsColumn(idBoardsGlobal, BoardsColumn(
                                        name = header.edtListCardName.editText?.text.toString()
                                )).await()
                                withContext(Dispatchers.Main){
                                    addColumn()
                                }
                            }catch (e: Exception){
                                Log.e("error", "Error when try to add new boards column ${e.message}")
                            }
                        }
                    }else{
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                listBoardsColumnGlobal?.get(mBoardView.focusedColumn)?.name = header.edtListCardName.editText?.text.toString()
                                boardKanbanViewModel.updateBoardsColumn(idBoardsGlobal, listBoardsColumnGlobal).await()
                            }catch (e: Exception){
                                Log.e("error", "Error when try to update boards column ${e.message}")
                            }
                        }
                    }
                    header.tvListCardName.visibility = View.VISIBLE
                    header.tvTagListCardName.visibility=View.GONE
                    header.tvListCardName.setText(header.edtListCardName.editText?.text)
                    header.tvListCardName.setTextColor(resources.getColor(R.color.text_color))
//                    footer.itemLayout.visibility = View.VISIBLE
                    header.edtListCardName.visibility = View.INVISIBLE
                    header.tvListCardName.visibility= View.VISIBLE
                    header.imgBtnDeleteBoardColumn.visibility= View.VISIBLE
//                    hideSoftKeyboard(this@BoardKanbanActivity)
                }else{
                    header.edtListCardName.visibility = View.INVISIBLE
                    header.tvListCardName.visibility= View.VISIBLE
                    header.edtListCardName.editText?.setText("Add list")
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        header.edtListCardName.setStartIconOnClickListener {
            header.edtListCardName.visibility = View.INVISIBLE
            header.tvListCardName.visibility= View.VISIBLE
            if(header.tvListCardName.text.isNotEmpty()){
                header.imgBtnDeleteBoardColumn.visibility= View.VISIBLE
            }
            header.edtListCardName.editText?.setText("Add list")
//            hideSoftKeyboard(this@BoardKanbanActivity)

        }
        header.edtListCardName.setEndIconOnClickListener {
            if(header.edtListCardName.editText?.text?.trim()?.isNotEmpty() == true){
                if(header.tvTagListCardName.visibility==View.VISIBLE){
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            boardKanbanViewModel.addBoardsColumn(idBoardsGlobal, BoardsColumn(
                                    name = header.edtListCardName.editText?.text.toString()
                            )).await()
                            withContext(Dispatchers.Main){
                                addColumn()
                            }
                        }catch (e: Exception){
                            Log.e("error", "error when try to add new boards column")
                        }
                    }
                }else{
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            listBoardsColumnGlobal?.get(mBoardView.focusedColumn)?.name = header.edtListCardName.editText?.text.toString()
                            boardKanbanViewModel.updateBoardsColumn(idBoardsGlobal, listBoardsColumnGlobal).await()
                        }catch (e: Exception){
                            Log.e("error", "error when try to update boards column ${e.message}")
                        }
                    }
                }
                header.tvListCardName.visibility = View.VISIBLE
                header.tvTagListCardName.visibility=View.GONE
                header.tvListCardName.setText(header.edtListCardName.editText?.text)
                header.tvListCardName.setTextColor(resources.getColor(R.color.text_color))
//                footer.itemLayout.visibility = View.VISIBLE
                header.edtListCardName.visibility = View.INVISIBLE
                header.tvListCardName.visibility= View.VISIBLE
                header.imgBtnDeleteBoardColumn.visibility= View.VISIBLE

            }else{
                header.edtListCardName.visibility = View.INVISIBLE
                header.tvListCardName.visibility= View.VISIBLE
                header.edtListCardName.editText?.setText("Add list")
            }
//            hideSoftKeyboard(this@BoardKanbanActivity)

        }


        footer.itemLayout.setOnClickListener {
            footer.tvAddCard.visibility= View.INVISIBLE
            footer.edtAddCard.visibility = View.VISIBLE
            footer.edtAddCard.editText?.requestFocus()
        }

        footer.edtAddCard.editText?.setOnEditorActionListener { v, actionId, event ->
            if(actionId==EditorInfo.IME_ACTION_DONE){
                if(footer.edtAddCard.editText?.text?.trim()?.isNotEmpty() == true){
                    val id: Long = sCreatedItems++.toLong()
                    Log.d("hasil_get_board_colu", "focusColumn ${mBoardView.focusedColumn}")
                    Log.d("hasil_get_board_colu", "boardsColumnGlobal ${listBoardsColumnGlobal?.toList()}")
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            if((listBoardsColumnGlobal?.get(mBoardView.focusedColumn)?.cards) == null){
                                listBoardsColumnGlobal?.get(mBoardView.focusedColumn)?.cards = mutableListOf(BoardsCard(
                                        name = footer.edtAddCard.editText?.text.toString(),
                                ))
                            }else{
                                listBoardsColumnGlobal?.get(mBoardView.focusedColumn)?.cards?.add(BoardsCard(
                                        name = footer.edtAddCard.editText?.text.toString(),
                                ))
                            }
                            boardKanbanViewModel.updateBoardsColumn(idBoardsGlobal, listBoardsColumnGlobal).await()
                        }catch (e: Exception){
                            Log.e("error", "error when try to add new boards card")
                        }
                    }
                    val item: Pair<*, *> = Pair(id, footer.edtAddCard.editText!!.text.toString())
                    mBoardView.addItem(mBoardView.getColumnOfHeader(header.itemLayout), mBoardView.getAdapter(mBoardView.focusedColumn).itemCount, item, true)
                    footer.edtAddCard.editText?.setText("")
                }
                footer.tvAddCard.visibility= View.VISIBLE
                footer.edtAddCard.visibility = View.INVISIBLE
//                hideSoftKeyboard(this@BoardKanbanActivity)
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        footer.edtAddCard.setStartIconOnClickListener {
            footer.tvAddCard.visibility= View.VISIBLE
            footer.edtAddCard.visibility = View.INVISIBLE
//            hideSoftKeyboard(this@BoardKanbanActivity)
        }

        footer.edtAddCard.setEndIconOnClickListener {
            if(footer.edtAddCard.editText?.text?.trim()?.isNotEmpty() == true){
                val id: Long = sCreatedItems++.toLong()
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if((listBoardsColumnGlobal?.get(mBoardView.focusedColumn)?.cards) == null){
                            listBoardsColumnGlobal?.get(mBoardView.focusedColumn)?.cards = mutableListOf(BoardsCard(
                                    name = footer.edtAddCard.editText?.text.toString(),
                            ))
                        }else{
                            listBoardsColumnGlobal?.get(mBoardView.focusedColumn)?.cards?.add(BoardsCard(
                                    name = footer.edtAddCard.editText?.text.toString(),
                            ))
                        }
                        boardKanbanViewModel.updateBoardsColumn(idBoardsGlobal, listBoardsColumnGlobal).await()
                    }catch (e: Exception){
                        Log.e("error", "Error when try to add new boards card")
                    }
                }
                val item: Pair<*, *> = Pair(id, footer.edtAddCard.editText!!.text.toString())
                mBoardView.addItem(mBoardView.getColumnOfHeader(header.itemLayout), mBoardView.getAdapter(mBoardView.focusedColumn).itemCount, item, true)
                footer.edtAddCard.editText?.setText("")
            }
            footer.tvAddCard.visibility= View.VISIBLE
            footer.edtAddCard.visibility = View.INVISIBLE
//            hideSoftKeyboard(this@BoardKanbanActivity)

        }

        if(nameColumn!=""){
            header.tvListCardName.visibility=View.VISIBLE
            header.imgBtnDeleteBoardColumn.visibility= View.VISIBLE
            header.tvListCardName.text=nameColumn
            header.tvTagListCardName.visibility = View.GONE
//            footer.itemLayout.visibility = View.VISIBLE
        }

        if(getUserTypeLogin() == getString(R.string.value_business_man)){
            if(nameColumn!=""){
                setViewToVisible(header.imgBtnDeleteBoardColumn)
            }
            header.itemLayout.isClickable=true
        }else if(getUserTypeLogin() == getString(R.string.value_freelancer)){
            setViewToInvisible(header.imgBtnDeleteBoardColumn)
            header.itemLayout.isClickable=false
        }


        CoroutineScope(Dispatchers.IO).launch {
            try{
                Log.d("hasil_get_board_card", "listCard : ${listBoardsCard?.toList()}")
                if(listBoardsCard?.isNotEmpty() == true){
                    val mItemArray: java.util.ArrayList<Pair<Long?, String?>?> = ArrayList()
                    for (card in listBoardsCard) {
                        val id: Long = sCreatedItems++.toLong()
                        if(card?.member!=null){
                            var freelancer = Freelancer()
                            listFreelancerInCard.forEach {
                                if(it.idFreelancer==card.member){
                                    freelancer = it
                                }
                            }
                            if(freelancer.photo != null){
                                mItemArray.add(Pair(id, "${card.name}#%photo%#${freelancer.photo}"))
                            }else{
                                mItemArray.add(Pair(id, "${card.name}#%name%#${freelancer.name}"))
                            }
                        }else{
                            mItemArray.add(Pair(id, card?.name.toString()))
                        }
                    }
                    Log.d("hasil_get_board_card", "mItemArray ${mItemArray.toList()}")
                    withContext(Dispatchers.Main){
                        val listAdapter = ItemAdapter(mItemArray, R.layout.column_item, R.id.item_layout, true)
                        listAdapter.onCardClickCallback={ row->
                            Log.d("card_click", "column ke: ${mBoardView.focusedColumn} dan row : $row")
                            val detailCardIntent = Intent(this@BoardKanbanActivity, DetailCardActivity::class.java)
                            detailCardIntent.putExtra(DetailCardActivity.EXTRA_ID_BOARD, idBoardsGlobal)
                            detailCardIntent.putExtra(DetailCardActivity.EXTRA_ID_PROJECT, idProjectGlobal)
                            detailCardIntent.putExtra(DetailCardActivity.EXTRA_POSITION_COLUMN, mBoardView.focusedColumn)
                            detailCardIntent.putExtra(DetailCardActivity.EXTRA_POSITION_ROW, row)
                            startActivityForResult(detailCardIntent, EXTRA_REQUEST)
                        }
                        val layoutManager = if (mGridLayout) GridLayoutManager(this@BoardKanbanActivity, 4) else LinearLayoutManager(this@BoardKanbanActivity)
                        val columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
                                .setLayoutManager(layoutManager)
                                .setHasFixedItemSize(false)
                                .setColumnBackgroundColor(Color.TRANSPARENT)
                                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                                .setHeader(header.root)
                                .setFooter(footer.root)
                                .setColumnDragView(header.root)
                                .build()
                        mBoardView.addColumn(columnProperties)
                        mColumns++
                    }

                }else{
                    val mItemArray:java.util.ArrayList<Pair<Long?, String?>?>? = ArrayList()

                    withContext(Dispatchers.Main){
                        val listAdapter = ItemAdapter(mItemArray, R.layout.column_item, R.id.item_layout, true)
                        val layoutManager = if (mGridLayout) GridLayoutManager(this@BoardKanbanActivity, 4) else LinearLayoutManager(this@BoardKanbanActivity)
                        val columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
                                .setLayoutManager(layoutManager)
                                .setHasFixedItemSize(false)
                                .setColumnBackgroundColor(Color.TRANSPARENT)
                                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                                .setHeader(header.root)
                                .setFooter(footer.root)
                                .setColumnDragView(header.root)
                                .build()
                        mBoardView.addColumn(columnProperties)
                        mColumns++
                    }

                }
            }catch (e: Exception){
                Log.e("error", "Error when get data card: ${e.message}")
            }
        }

    }

    inner class MyColumnDragItem internal constructor(context: Context?, layoutId: Int) : DragItem(context, layoutId) {
        lateinit var dragFooter:View
        override fun onBindDragView(clickedView: View, dragView: View) {

            val clickedLayout = clickedView as LinearLayout
            val clickedHeader = clickedLayout.getChildAt(0)
            val clickedFooter = clickedLayout.getChildAt(2)
            val clickedRecyclerView = clickedLayout.getChildAt(1) as RecyclerView
            val dragList = dragView.findViewById<View>(R.id.drag_list)
            val dragHeader = dragView.findViewById<View>(R.id.drag_header)
            dragFooter = dragView.findViewById<View>(R.id.drag_footer)
            val dragScrollView = dragView.findViewById<ScrollView>(R.id.drag_scroll_view)
            val dragLayout = dragView.findViewById<LinearLayout>(R.id.drag_list)
            val clickedColumnBackground = clickedLayout.background
            if (clickedColumnBackground != null) {
                ViewCompat.setBackground(dragView, clickedColumnBackground)
            }
            val clickedRecyclerBackground = clickedRecyclerView.background
            if (clickedRecyclerBackground != null) {
                ViewCompat.setBackground(dragLayout, clickedRecyclerBackground)
            }
            dragLayout.removeAllViews()
            if((clickedHeader.findViewById<View>(R.id.tv_list_card_name) as TextView).text.trim().isEmpty()){
                (dragHeader.findViewById<View>(R.id.tv_tag_list_card_name) as TextView).visibility=View.VISIBLE
//                (dragFooter.findViewById<View>(R.id.item_layout) as CardView).visibility = View.GONE
            }else{
                (dragHeader.findViewById<View>(R.id.tv_tag_list_card_name) as TextView).visibility=View.GONE
//                (dragFooter.findViewById<View>(R.id.item_layout) as CardView).visibility = View.VISIBLE
            }
            (dragHeader.findViewById<View>(R.id.tv_list_card_name) as TextView).visibility=View.VISIBLE
            (dragHeader.findViewById<View>(R.id.tv_list_card_name) as TextView).text = (clickedHeader.findViewById<View>(R.id.tv_list_card_name) as TextView).text
            (dragHeader.findViewById<View>(R.id.item_count) as TextView).text = (clickedHeader.findViewById<View>(R.id.item_count) as TextView).text
            (dragFooter.findViewById<View>(R.id.tv_add_card) as TextView).text = (clickedFooter.findViewById<View>(R.id.tv_add_card) as TextView).text



            for (i in 0 until clickedRecyclerView.childCount) {
                val view = View.inflate(dragView.context, R.layout.column_item, null)
                (view.findViewById<View>(R.id.text) as TextView).text = (clickedRecyclerView.getChildAt(i).findViewById<View>(R.id.text) as TextView).text
                dragLayout.addView(view)
                if (i == 0) {
                    dragScrollView.scrollY = -clickedRecyclerView.getChildAt(i).top
                }
            }
            dragView.pivotY = 0f
            dragView.pivotX = (clickedView.getMeasuredWidth() / 2).toFloat()
        }

        override fun onStartDragAnimation(dragView: View) {
            super.onStartDragAnimation(dragView)
            dragView.animate().scaleX(0.9f).scaleY(0.9f).start()
        }

        override fun onEndDragAnimation(dragView: View) {
            super.onEndDragAnimation(dragView)
            dragView.animate().scaleX(1f).scaleY(1f).start()
        }

        init {
            setSnapToTouch(false)
        }
    }

    private inner class MyDragItem(context: Context?, layoutId: Int) : DragItem(context, layoutId) {
        override fun onBindDragView(clickedView: View, dragView: View) {
            val text = (clickedView.findViewById<View>(R.id.text) as TextView).text
            (dragView.findViewById<View>(R.id.text) as TextView).text = text
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val clickedCard: CardView = clickedView.findViewById(R.id.card)
            dragCard.maxCardElevation = 40f
            dragCard.cardElevation = clickedCard.cardElevation
            // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
            dragCard.foreground = clickedView.resources.getDrawable(R.drawable.card_view_drag_foreground)
        }

        override fun onMeasureDragView(clickedView: View, dragView: View) {

            val dragCard: CardView = dragView.findViewById(R.id.card)
            val clickedCard: CardView = clickedView.findViewById(R.id.card)
            val widthDiff = dragCard.paddingLeft - clickedCard.paddingLeft + dragCard.paddingRight -
                    clickedCard.paddingRight
            val heightDiff = dragCard.paddingTop - clickedCard.paddingTop + dragCard.paddingBottom -
                    clickedCard.paddingBottom
            val width = clickedView.measuredWidth + widthDiff
            val height = clickedView.measuredHeight + heightDiff
            dragView.layoutParams = FrameLayout.LayoutParams(width, height)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            dragView.measure(widthSpec, heightSpec)
        }

        override fun onStartDragAnimation(dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 40f)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = ANIMATION_DURATION.toLong()
            anim.start()
        }

        override fun onEndDragAnimation(dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 6f)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = ANIMATION_DURATION.toLong()
            anim.start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== EXTRA_REQUEST){
            if(resultCode==DetailCardActivity.EXTRA_RESULT_CODE){
                val boardColumnPosition = data?.getIntExtra(DetailCardActivity.EXTRA_POSITION_COLUMN, -1)
                val boardCardPosition = data?.getIntExtra(DetailCardActivity.EXTRA_POSITION_ROW, -1)
                val statusDelete = data?.getBooleanExtra(DetailCardActivity.EXTRA_STATUS_DELETE, false)
                val cardName = data?.getStringExtra(DetailCardActivity.EXTRA_CARD_NAME)
                Log.d("hasil_feedback", "name card ${cardName} column position ${boardColumnPosition} card position ${boardCardPosition} status delete ${statusDelete}")
                if(boardColumnPosition!=null && boardCardPosition!=null){
                    if (statusDelete == true) {
                        mBoardView.removeItem(boardColumnPosition, boardCardPosition)
                    }else{

                        val id: Long = sCreatedItems++.toLong()
                        val item: Pair<*, *> = Pair(id, cardName)
                        mBoardView.replaceItem(boardColumnPosition, boardCardPosition, item, false)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.boards_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.add_member_tab_menu -> {
                val memberIntent = Intent(this, MembersActivity::class.java)
                memberIntent.putExtra(MembersActivity.EXTRA_ID_BOARDS, idBoardsGlobal)
                memberIntent.putExtra(MembersActivity.EXTRA_ID_PROJECT, idProjectGlobal)
                startActivity(memberIntent)
                true
            }
            R.id.project_deadline_menu -> {
                val projectDeadlinView = ProjectDeadlineBinding.inflate(layoutInflater)
                if (detailProject.projectDueDate != null) {

                    val deadlineDay = detailProject.projectDueDate!!.toDate().time - Calendar.getInstance().timeInMillis
                    val seconds = deadlineDay / 1000
                    val minutes = seconds / 60
                    val hours = minutes / 60
                    val days = hours / 24
                    Log.d("deadlin", "ini dia $days")
                    if (days.toInt() > 1) {
                        projectDeadlinView.tvProjectDeadline.setTextColor(ContextCompat.getColor(this, R.color.primary))
                        setTextViewDrawableColor(projectDeadlinView.tvProjectDeadline, R.color.primary)
                    } else if (days.toInt() == 1) {
                        projectDeadlinView.tvProjectDeadline.setTextColor(ContextCompat.getColor(this, R.color.secondary))
                        setTextViewDrawableColor(projectDeadlinView.tvProjectDeadline, R.color.secondary)
                    } else if (days.toInt() == 0) {
                        projectDeadlinView.tvProjectDeadline.setTextColor(ContextCompat.getColor(this, R.color.error))
                        setTextViewDrawableColor(projectDeadlinView.tvProjectDeadline, R.color.error)
                    }

                    val formatter = SimpleDateFormat("EEEE, d MMM yyyy 'at' hh:mm a")
                    projectDeadlinView.tvProjectDeadline.textSize = 18F
                    projectDeadlinView.tvProjectDeadline.text = formatter.format(detailProject.projectDueDate!!.toDate().time)
                } else {
                    projectDeadlinView.tvProjectDeadline.textSize = 14F
                    projectDeadlinView.tvProjectDeadline.text = getString(R.string.coming_soon_in_days, detailProject.durationInDays)
                }
                MaterialAlertDialogBuilder(this)
                        .setTitle(getString(R.string.project_deadline))
                        .setView(projectDeadlinView.root)
                        .setPositiveButton(getString(R.string.ok)) { dialogInterface: DialogInterface, i: Int ->

                        }
                        .show()
                true
            }
            else-> super.onOptionsItemSelected(item)
        }
    }

    private fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawablesRelative) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(textView.context, color), PorterDuff.Mode.SRC_IN)
            }
        }
    }


//    fun hideSoftKeyboard(activity: Activity=this) {
//        val inputMethodManager: InputMethodManager = activity.getSystemService(
//                INPUT_METHOD_SERVICE) as InputMethodManager
//        inputMethodManager.hideSoftInputFromWindow(
//                activity.currentFocus!!.windowToken,
//                0
//        )
//    }
}