package com.example.loutaro.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.BusinessMan
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.source.LoutaroRepository

class ProfileViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var responseGetMyProfileFreelancer: MutableLiveData<Freelancer> = MutableLiveData()
    var responseGetMyProfileBusinessMan: MutableLiveData<BusinessMan> = MutableLiveData()
    val idFreelancer = loutaroRepository.getCurrentUser()?.uid.toString()

    fun getMyProfileFreelancer(){
        loutaroRepository.getDataFreelancerRealtime(idFreelancer)
                .addSnapshotListener { querySnapshot, error ->
                    querySnapshot?.let { query->
                        responseGetMyProfileFreelancer.postValue(query.toObject(Freelancer::class.java))
                    }
                    error?.let{
                        responseGetMyProfileFreelancer.postValue(null)
                    }
                }
    }

    fun getMyProfileBusineMan(){
        loutaroRepository.getDataBusinessManRealtime(idFreelancer)
            .addSnapshotListener { querySnapshot, error ->
                querySnapshot?.let { query->
                    responseGetMyProfileBusinessMan.postValue(query.toObject(BusinessMan::class.java))
                }
                error?.let{
                    responseGetMyProfileBusinessMan.postValue(null)
                }
            }
    }

}