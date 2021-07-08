package com.example.loutaro.ui.boardKanban

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Boards
import com.example.loutaro.data.source.LoutaroRepository

class BoardKanbanViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var responseGetDetailDataBoards: MutableLiveData<Boards?> = MutableLiveData()

    fun getDetailDataBoards(idBoards: String){
        loutaroRepository.getDetailDataBoards(idBoards).addSnapshotListener { querySnapshot, error ->
            querySnapshot?.let {
                responseGetDetailDataBoards.postValue(querySnapshot.toObject(Boards::class.java))
            }

            error?.let {
                responseGetDetailDataBoards.postValue(null)
            }
        }
    }

}