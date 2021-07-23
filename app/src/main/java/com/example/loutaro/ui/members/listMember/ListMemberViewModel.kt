package com.example.loutaro.ui.members.listMember

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Boards
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class ListMemberViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var responseGetListMember: MutableLiveData<MutableList<Freelancer>> = MutableLiveData()
    var responseGetDetailBoards: MutableLiveData<Boards> = MutableLiveData()

    fun getListMember(idBoards: String, listIdMember: List<String>){
        loutaroRepository.getListMember(idBoards, listIdMember).addSnapshotListener { querySnapshot, error ->
            querySnapshot?.let {
                val data = mutableListOf<Freelancer>()
                for (document in it.documents){
                    val response = document.toObject(Freelancer::class.java)
                    response?.idFreelancer= document.id
                    if (response != null) {
                        data.add(response)
                    }
                }
                responseGetListMember.postValue(data)
            }
        }
    }

    fun deleteMemberFromBoard(idBoards: String, idMember: String): Task<Void> {
        return loutaroRepository.deleteMemberFromBoards(idBoards, idMember)
    }

    fun deleteMemberFromProject(idProject: String, project: Project): Task<Void> {
        return loutaroRepository.removeMemberFromProject(idProject, project)
    }

    fun getDetailBoards(idBoards: String) {
        loutaroRepository.getDetailDataBoards(idBoards).addSnapshotListener { querySnapshot, error ->
            querySnapshot?.let {
                responseGetDetailBoards.postValue(it.toObject(Boards::class.java))
            }
        }
    }

    fun updateBoards(idBoards: String, boards: Boards): Task<Void> {
        return loutaroRepository.updateBoards(idBoards, boards)
    }

    fun getDetailProject(idProject: String): Task<DocumentSnapshot> {
        return loutaroRepository.getDetailProject(idProject)
    }

}