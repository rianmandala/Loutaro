package com.example.loutaro.ui.freelancerRecommendation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.FreelancerRecommendation
import com.example.loutaro.data.source.LoutaroRepository

class FreelancerRecommendationViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var responseGetAllFreelancer: MutableLiveData<MutableList<FreelancerRecommendation>> = MutableLiveData()

    fun getAllFreelancer(){
        loutaroRepository.getAllFreelancer().get().addOnSuccessListener {
            val data= mutableListOf<FreelancerRecommendation>()
            for(document in it.documents){
                val response = document.toObject(FreelancerRecommendation::class.java)
                if (response != null) {
                    response.idFreelancer= document.id
                    data.add(response)
                }
            }
            responseGetAllFreelancer.postValue(data)
        }
    }

}