package com.example.loutaro.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository

class HomeViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var statusGetDataProjects: MutableLiveData<List<Project>?> = MutableLiveData()

    var statusGetAllFreelancer: MutableLiveData<List<Freelancer>?> = MutableLiveData()

    fun getDataProjects(){
        loutaroRepository.getDataProjects().addSnapshotListener { querySnapshot, error ->
            var dataProject = mutableListOf<Project>()
            querySnapshot?.let { query->
                for(document in query.documents){
                    val response = document.toObject(Project::class.java)
                    response?.idProject = document.id
                    if (response != null) {
                        dataProject.add(response)
                    }
                }
                statusGetDataProjects.postValue(dataProject)
            }
            error?.let {
                statusGetDataProjects.postValue(null)
            }
        }
    }

    fun getAllFreelancer(){
        loutaroRepository.getAllFreelancer().addSnapshotListener { querySnapshot, error ->
            var dataProject = mutableListOf<Freelancer>()
            querySnapshot?.let { query->
                for(document in query.documents){
                    val response = document.toObject(Freelancer::class.java)
                    response?.idFreelancer = document.id
                    if (response != null) {
                        dataProject.add(response)
                    }
                }
                statusGetAllFreelancer.postValue(dataProject)
            }
            error?.let {
                statusGetDataProjects.postValue(null)
            }
        }
    }

}