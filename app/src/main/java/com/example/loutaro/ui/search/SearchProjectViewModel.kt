package com.example.loutaro.ui.search

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository

class SearchProjectViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var responseGetSearchProject: MutableLiveData<List<Project?>> = MutableLiveData()
    var filterSearchProject: MutableLiveData<List<Project?>> = MutableLiveData()

    var responseGetSearchFreelancer: MutableLiveData<List<Freelancer?>> = MutableLiveData()
    var filterSearchFreelancer: MutableLiveData<List<Freelancer?>> = MutableLiveData()

    fun getSearchProject(titleProject: String){
        if(filterSearchProject.value==null){
            loutaroRepository.getDataProjects()
                .get().addOnSuccessListener { querySnapshot->
                    val data = mutableListOf<Project?>()
                    val data2 = mutableListOf<Project?>()
                    querySnapshot?.let{
                        for(document in it.documents){
                            val response = document.toObject(Project::class.java)
                            response?.idProject = document.id
                            data2.add(response)
                            response?.title?.let {
                                if(it.toUpperCase().contains(titleProject.toUpperCase())){
                                    data.add(response)
                                }
                            }
                        }
                    }
                    responseGetSearchProject.postValue(data)
                    filterSearchProject.postValue(data2)
                }
        }else{
            val dataFilter = filterSearchProject.value!!.filter { item-> item!!.title!!.toUpperCase().contains(titleProject.toUpperCase()) }
            responseGetSearchProject.postValue(dataFilter)
        }

    }

    fun getSearchFreelance(name: String){
        if(filterSearchProject.value==null){
            loutaroRepository.getAllFreelancer()
                .get().addOnSuccessListener { querySnapshot->
                    val data = mutableListOf<Freelancer?>()
                    val data2 = mutableListOf<Freelancer?>()
                    querySnapshot?.let{
                        for(document in it.documents){
                            val response = document.toObject(Freelancer::class.java)
                            response?.idFreelancer = document.id
                            data2.add(response)
                            response?.name?.let {
                                if(it.toUpperCase().contains(name.toUpperCase())){
                                    data.add(response)
                                }
                            }
                        }
                    }
                    responseGetSearchFreelancer.postValue(data)
                    filterSearchFreelancer.postValue(data2)
                }
        }else{
            val dataFilter = filterSearchFreelancer.value!!.filter { item-> item!!.name!!.toUpperCase().contains(name.toUpperCase()) }
            responseGetSearchFreelancer.postValue(dataFilter)
        }

    }

}