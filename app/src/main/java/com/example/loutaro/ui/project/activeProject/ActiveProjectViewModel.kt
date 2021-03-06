package com.example.loutaro.ui.project.activeProject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp

class ActiveProjectViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var statusGetActiveProject: MutableLiveData<List<Project?>> = MutableLiveData()

    fun getActiveProjectForFreelancer(){
        val idFreelancer = loutaroRepository.getCurrentUser()?.uid.toString()
        loutaroRepository.getActiveProjectForFreelancer(idFreelancer).addSnapshotListener { querySnapshot, error ->
            val dataProject = mutableListOf<Project?>()
            querySnapshot?.let { query->
                    for (document in query.documents){
                        val response = document.toObject(Project::class.java)
                        response?.idProject = document.id
                        if (response != null) {
                            dataProject.add(response)
                        }
                    }
            }
            statusGetActiveProject.postValue(dataProject)
        }
    }

    fun getActiveProjectForBusinessMan(){
        val idBusinessMan = loutaroRepository.getCurrentUser()?.uid.toString()
        loutaroRepository.getActiveProjectForBusinessMan(idBusinessMan).addSnapshotListener { querySnapshot, error ->
            val dataProject = mutableListOf<Project?>()
            querySnapshot?.let { query->
                for (document in query.documents){
                    val response = document.toObject(Project::class.java)
                    response?.idProject = document.id
                    if (response != null) {
                        dataProject.add(response)
                    }
                }
            }
            statusGetActiveProject.postValue(dataProject)
        }
    }

    fun deleteProject(idProject: String): Task<Void> {
        return loutaroRepository.deleteProject(idProject)
    }

    fun projectHasCompleted(idProject: String): Task<Void> {
        return loutaroRepository.projectHasCompleted(idProject)
    }

    fun updateBalanceFreelancer(idFreelancer: String, balance: Long): Task<Void> {
        return loutaroRepository.updateBalanceFreelancer(idFreelancer, balance)
    }

    fun startProject(idProject: String, projectStartDate: Timestamp): Task<Void> {
        return loutaroRepository.startProject(idProject, projectStartDate)
    }

}