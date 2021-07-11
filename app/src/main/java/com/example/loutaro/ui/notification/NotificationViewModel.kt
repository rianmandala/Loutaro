package com.example.loutaro.ui.notification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository

class NotificationViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var responseGetProjectNotCompleteYet: MutableLiveData<MutableList<Project>> = MutableLiveData()

    var responseGetApplyerFromFreelancer: MutableLiveData<MutableList<Freelancer>> = MutableLiveData()

    fun getProjectNotCompletedYet(idBusinessMan: String){
            loutaroRepository.getDataProjectsOngoingForBusinessMan(idBusinessMan).addSnapshotListener { querySnapshot, error ->
                querySnapshot?.let {
                    val data= mutableListOf<Project>()
                    for (document in it.documents){
                        val response = document.toObject(Project::class.java)
                        response?.idProject= document.id
                        if (response != null) {
                            data.add(response)
                        }
                    }
                    responseGetProjectNotCompleteYet.postValue(data)
                }
            }
    }

    fun getApplyerFromFreelancer(listIdFreelancer: List<String>){
        loutaroRepository.getApplyerFromFreelancer(listIdFreelancer).get().addOnSuccessListener { listFreelancer->
            var data = mutableListOf<Freelancer>()
            for(document in listFreelancer){
                val response = document.toObject(Freelancer::class.java)
                response.idFreelancer = document.id
                data.add(response)
            }
            responseGetApplyerFromFreelancer.postValue(data)
        }
    }

}