package com.example.loutaro.ui.members.addMember

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Boards
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

class AddMemberViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var responseGetDetailBoards: MutableLiveData<Boards> = MutableLiveData()

    fun getAllFreelancer(): CollectionReference {
        return loutaroRepository.getAllFreelancer()
    }

    fun addMemberToBoards(idboards: String, idMember: String): Task<Void> {
        return loutaroRepository.addMember(idboards, idMember)
    }

    fun addMemberToProject(idProject: String, idMember: String): Task<Void> {
        return loutaroRepository.addMemberToProject(idProject, idMember)
    }

    fun getDetailBoards(idboards: String) {
        loutaroRepository.getDetailDataBoards(idboards).addSnapshotListener { querySnapshot, error ->
            querySnapshot?.let {
                responseGetDetailBoards.postValue(it.toObject(Boards::class.java))
            }
        }
    }

}