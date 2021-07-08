package com.example.loutaro.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loutaro.data.entity.MessageResponse
import com.example.loutaro.data.source.LoutaroRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var statusLoginWithEmailAndPass= MutableLiveData<MessageResponse>()

    var responseIsBusinessManExist = MutableLiveData<Boolean>()

    var responseIsFreelancerExist = MutableLiveData<Boolean>()

    fun loginWithEmailAndPass(email: String, password: String){
        loutaroRepository.signWithEmailAndPass(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                statusLoginWithEmailAndPass.postValue(MessageResponse(true))
            }else{
                statusLoginWithEmailAndPass.postValue(MessageResponse(false,it.exception?.message))
            }
        }.addOnFailureListener {
            statusLoginWithEmailAndPass.postValue(MessageResponse(false,it.message))
        }
    }

    fun getCurrentUser() = loutaroRepository.getCurrentUser()

    fun isBusinessManExist(id: String){
        viewModelScope.launch {
            val dataBusinessMan = loutaroRepository.isBusinessManExist(id).await()
            if(dataBusinessMan.exists()) responseIsBusinessManExist.postValue(true)
            else responseIsBusinessManExist.postValue(false)
        }
    }

    fun isFreelancerExist(id: String){
        viewModelScope.launch {
            val dataFreelancer = loutaroRepository.isFreelancerExist(id).await()
            if(dataFreelancer.exists()) responseIsFreelancerExist.postValue(true)
            else responseIsFreelancerExist.postValue(false)
        }
    }
}