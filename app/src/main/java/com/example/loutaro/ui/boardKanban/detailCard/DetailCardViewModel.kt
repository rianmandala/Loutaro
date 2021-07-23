package com.example.loutaro.ui.boardKanban.detailCard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Boards
import com.example.loutaro.data.entity.BoardsColumn
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class DetailCardViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var registration: ListenerRegistration?=null

    var responseGetDetailBoards: MutableLiveData<Boards> = MutableLiveData()

    fun getDetailBoards(idBoards: String) {
        registration=loutaroRepository.getDetailDataBoards(idBoards).addSnapshotListener { querySnapshot, error ->
            querySnapshot?.let {
                responseGetDetailBoards.postValue(it.toObject(Boards::class.java))
            }
        }
    }

    fun getDetailProject(idProject: String): Task<DocumentSnapshot> {
        return loutaroRepository.getDetailProject(idProject)
    }

    fun updateDataBoardsColumns(idBoards: String, dataBoardsColumn: List<BoardsColumn>): Task<Void> {
        return loutaroRepository.updateBoardsColumn(idBoards,dataBoardsColumn)
    }

    fun getDetailFreelancer(idFreelancer: String): Task<DocumentSnapshot> {
        return loutaroRepository.getDataFreelancer(idFreelancer)
    }

    fun getdetailBusinessMan(idBusinessMan: String): Task<DocumentSnapshot> {
        return loutaroRepository.getDataBusinessManRealtime(idBusinessMan).get()
    }

    fun getListFreelancerWithListIdFreelancer(listIdFreelancer:List<String> ): Query {
        return loutaroRepository.getListFreelancerWithListIdFreelancer(listIdFreelancer)
    }

}