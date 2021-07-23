package com.example.loutaro.ui.saved

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository

class SavedProjectViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {
    var statusGetSaveDataProjects: MutableLiveData<List<Project>?> = MutableLiveData()
    var statusGetSaveDataFreelancer: MutableLiveData<List<Freelancer>?> = MutableLiveData()

    fun getDataSaveProjects(listIdProject: List<String>){
        loutaroRepository.getListProjectWithListIdProject(listIdProject).get().addOnSuccessListener { query->
            var dataProject = mutableListOf<Project>()
            for (document in query.documents) {
                val response = document.toObject(Project::class.java)
                response?.idProject = document.id
                if (response != null) {
                    dataProject.add(response)
                }
            }
            statusGetSaveDataProjects.postValue(dataProject)
        }.addOnFailureListener {
            statusGetSaveDataProjects.postValue(null)
        }
    }

    fun getDataSaveFreelancer(listIdFreelancer: List<String>){
        loutaroRepository.getListFreelancerWithListIdFreelancer(listIdFreelancer).get().addOnSuccessListener {query->
            var dataFreelancer = mutableListOf<Freelancer>()
            for (document in query.documents) {
                val response = document.toObject(Freelancer::class.java)
                response?.idFreelancer = document.id
                if (response != null) {
                    dataFreelancer.add(response)
                }
            }
            statusGetSaveDataFreelancer.postValue(dataFreelancer)
        }.addOnFailureListener {
            statusGetSaveDataFreelancer.postValue(null)
        }
    }
}