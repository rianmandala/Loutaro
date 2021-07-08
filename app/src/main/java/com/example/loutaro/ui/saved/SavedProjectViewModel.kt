package com.example.loutaro.ui.saved

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository

class SavedProjectViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {
    var statusGetSaveDataProjects: MutableLiveData<List<Project>?> = MutableLiveData()
    var statusGetSaveDataFreelancer: MutableLiveData<List<Freelancer>?> = MutableLiveData()

    fun getDataSaveProjects(){
        loutaroRepository.getDataSavedProject().addSnapshotListener { querySnapshot, error ->
            var dataProject = mutableListOf<Project>()
            querySnapshot?.let { query ->
                for (document in query.documents) {
                    val response = document.toObject(Project::class.java)
                    response?.idProject = document.id
                    if (response != null) {
                        dataProject.add(response)
                    }
                }
                statusGetSaveDataProjects.postValue(dataProject)
            }
            error?.let {
                statusGetSaveDataProjects.postValue(null)

            }

        }
    }

    fun getDataSaveFreelancer(){
        loutaroRepository.getDataSavedFreelancer().addSnapshotListener { querySnapshot, error ->
            var dataFreelancer = mutableListOf<Freelancer>()
            querySnapshot?.let { query ->
                for (document in query.documents) {
                    val response = document.toObject(Freelancer::class.java)
                    response?.idFreelancer = document.id
                    if (response != null) {
                        dataFreelancer.add(response)
                    }
                }
                statusGetSaveDataFreelancer.postValue(dataFreelancer)
            }
            error?.let {
                statusGetSaveDataFreelancer.postValue(null)

            }

        }
    }
}