package com.example.loutaro.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.entity.ProjectRecommendation
import com.example.loutaro.data.source.LoutaroRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class HomeViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var statusGetDataProjects: MutableLiveData<List<ProjectRecommendation>?> = MutableLiveData()

    var statusGetAllFreelancer: MutableLiveData<List<Freelancer>?> = MutableLiveData()

    var responseGetProfile: MutableLiveData<Freelancer?> = MutableLiveData()

    fun getDataProjects(){
        loutaroRepository.getDataProjects().addSnapshotListener { querySnapshot, error ->
            var dataProject = mutableListOf<ProjectRecommendation>()
            querySnapshot?.let { query->
                for(document in query.documents){
                    val response = document.toObject(ProjectRecommendation::class.java)
                    if (response != null) {
                        response.idProject = document.id
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

    fun getProfile(idFreelancer: String) {
        loutaroRepository.getDataFreelancerRealtime(idFreelancer).addSnapshotListener { querySnapshot, error ->
            querySnapshot?.let {
                responseGetProfile.postValue(it.toObject(Freelancer::class.java))
            }
            error?.let {
                responseGetProfile.postValue(null)
            }
        }
    }

    fun getAllFreelancer(){
        loutaroRepository.getAllFreelancer().limit(10).addSnapshotListener { querySnapshot, error ->
            var dataProject = mutableListOf<Freelancer>()
            querySnapshot?.let { query->
                for(document in query.documents){
                    val response = document.toObject(Freelancer::class.java)
                    response?.idFreelancer = document.id
                    if (response != null ) {
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