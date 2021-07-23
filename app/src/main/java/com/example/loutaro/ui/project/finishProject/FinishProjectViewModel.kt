package com.example.loutaro.ui.project.finishProject

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task

class FinishProjectViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var statusGetFinishProject: MutableLiveData<List<Project?>> = MutableLiveData()

    fun getFinishProjectForFreelancer(){
        val idFreelancer = loutaroRepository.getCurrentUser()?.uid.toString()
        loutaroRepository.getFinishProjectForFreelancer(idFreelancer)
            .addSnapshotListener { querySnapshot, error ->
                querySnapshot?.let {
                    val data = mutableListOf<Project?>()
                    for(document in it.documents){
                        val response = document.toObject(Project::class.java)
                        response?.idProject = document.id
                        data.add(response)
                    }
                    statusGetFinishProject.postValue(data)
                }
            }
    }

    fun deleteProject(idProject: String): Task<Void> {
        return loutaroRepository.deleteProject(idProject)
    }

    fun getFinishProjectForBusinessMan(){
        val idBusinessMan = loutaroRepository.getCurrentUser()?.uid.toString()
        loutaroRepository.getFinishProjectForBusinessMan(idBusinessMan)
            .addSnapshotListener { querySnapshot, error ->
                querySnapshot?.let {
                    val data = mutableListOf<Project?>()
                    for(document in it.documents){
                        val response = document.toObject(Project::class.java)
                        response?.idProject = document.id
                        data.add(response)
                    }
                    Log.d("Hasil_finish","finish project ${data}")
                    statusGetFinishProject.postValue(data)
                }
            }
    }

}