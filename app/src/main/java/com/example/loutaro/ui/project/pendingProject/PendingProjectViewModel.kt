package com.example.loutaro.ui.project.pendingProject

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task

class PendingProjectViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    val responseGetPendingProject: MutableLiveData<MutableList<Project?>> = MutableLiveData()

    fun deleteProject(idProject: String): Task<Void> {
        return loutaroRepository.deleteProject(idProject)
    }

    fun getPendingProjectForBusinessMan(){
        val idBusinessMan = loutaroRepository.getCurrentUser()?.uid.toString()
        loutaroRepository.getPendingProjectForBusinessMan(idBusinessMan).addSnapshotListener { querySnapshot, error ->
            val dataProject = mutableListOf<Project?>()
            querySnapshot?.let { query->
                try{
                    for (document in query.documents){
                        val response = document.toObject(Project::class.java)
                        response?.idProject = document.id
                        if (response != null) {
                            dataProject.add(response)
                        }
                    }
                }catch (e: Exception){
                    Log.e("error", "error when try to get pending project ${e.message}")
                }

            }
            responseGetPendingProject.postValue(dataProject)
        }
    }

}