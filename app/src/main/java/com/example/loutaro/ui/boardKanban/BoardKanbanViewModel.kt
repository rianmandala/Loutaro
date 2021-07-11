package com.example.loutaro.ui.boardKanban

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.*
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query

class BoardKanbanViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var responseGetDetailDataBoards: MutableLiveData<Boards?> = MutableLiveData()

    fun getDetailDataBoards(idBoards: String): DocumentReference {
        loutaroRepository.getDetailDataBoards(idBoards).addSnapshotListener { querySnapshot, error ->
            querySnapshot?.let {
                responseGetDetailDataBoards.postValue(querySnapshot.toObject(Boards::class.java))
            }

            error?.let {
                responseGetDetailDataBoards.postValue(null)
            }
        }

        return loutaroRepository.getDetailDataBoards(idBoards)
    }

    fun addBoardsColumn(idBoards: String, boardsColumn: BoardsColumn): Task<Void> {
        return loutaroRepository.addBoardsColumn(idBoards, boardsColumn)
    }

    fun updateBoardsColumn(idBoards: String, listBoardsColumn: List<BoardsColumn?>?): Task<Void> {
        return loutaroRepository.updateBoardsColumn(idBoards, listBoardsColumn)
    }

}