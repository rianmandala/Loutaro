package com.example.loutaro.ui.project.finishProject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository

class FinishProjectViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var statusGetFinishProject: MutableLiveData<List<Project?>> = MutableLiveData()

    fun getFinishProjectForFreelancer(){
        val idFreelancer = loutaroRepository.getCurrentUser()?.uid.toString()
        loutaroRepository.getFinishProjectForFreelancer(idFreelancer)
            .addSnapshotListener { querySnapshot, error ->
                val data = mutableListOf<Project?>()
                querySnapshot?.let {
                    for(document in it.documents){
                        data.add(document.toObject(Project::class.java))
                    }
                }
                statusGetFinishProject.postValue(data)
            }
    }

    fun getFinishProjectForBusinessMan(){
        val idBusinessMan = loutaroRepository.getCurrentUser()?.uid.toString()
        loutaroRepository.getFinishProjectForBusinessMan(idBusinessMan)
            .addSnapshotListener { querySnapshot, error ->
                val data = mutableListOf<Project?>()
                querySnapshot?.let {
                    for(document in it.documents){
                        data.add(document.toObject(Project::class.java))
                    }
                }
                statusGetFinishProject.postValue(data)
            }
    }

}