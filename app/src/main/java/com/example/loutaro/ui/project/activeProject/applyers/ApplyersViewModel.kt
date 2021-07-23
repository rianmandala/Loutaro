package com.example.loutaro.ui.project.activeProject.applyers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.*
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class ApplyersViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {
    var responseGetDetailProject: MutableLiveData<Project> = MutableLiveData()

    fun getDetailProject(idProject: String) {
        loutaroRepository.getRealtimeDetailProject(idProject).addSnapshotListener { querySnapshot, error ->
            querySnapshot?.let {
                responseGetDetailProject.postValue(querySnapshot.toObject(Project::class.java))
            }
            error?.let {
                responseGetDetailProject.postValue(null)
            }
        }
    }

    fun getDetailBoard(idBoard: String): DocumentReference {
        return loutaroRepository.getDetailDataBoards(idBoard)
    }

    fun getApplyerFromFreelancer(listIdFreelancer: List<String>): com.google.android.gms.tasks.Task<QuerySnapshot> {
        return loutaroRepository.getListFreelancerWithListIdFreelancer(listIdFreelancer).get()
    }

    fun getDetailFreelancer(idFreelancer: String): Task<DocumentSnapshot> {
        return loutaroRepository.getDataFreelancer(idFreelancer)
    }

    fun confirmApplyers(idProject: String, project: Project): com.google.android.gms.tasks.Task<Void> {
        return loutaroRepository.confirmApplyers(idProject, project)
    }

    fun addMemberToBoards(idBoard: String, idMember: String): com.google.android.gms.tasks.Task<Void> {
        return loutaroRepository.addMemberToBoards(idBoard, idMember)
    }

    fun updateBoards(idBoard: String, boards: Boards): Task<Void> {
        return loutaroRepository.updateBoards(idBoard, boards)
    }
}