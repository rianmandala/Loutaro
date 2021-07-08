package com.example.loutaro.ui.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loutaro.data.entity.BusinessMan
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.MessageRegisterWithEmailAndPass
import com.example.loutaro.data.source.LoutaroRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var statusRegisterWithEmailAndPass= MutableLiveData<MessageRegisterWithEmailAndPass>()

    fun registerWithEmailAndPass(email: String, password: String){
        loutaroRepository.registerWithEmailAndPass(email, password)
            .addOnSuccessListener {
                Log.d("hasil_get_uid","${it.user?.uid}")
                statusRegisterWithEmailAndPass.postValue(MessageRegisterWithEmailAndPass(true,uid = it.user?.uid))
            }
            .addOnFailureListener {
                statusRegisterWithEmailAndPass.postValue(MessageRegisterWithEmailAndPass(false,message = it.message))
            }
    }

    fun sendEmailVerification()= loutaroRepository.sendEmailVerification()

    fun setDataFreelancer(id: String, data: Freelancer){
        loutaroRepository.setDataFreelancer(id,data)
    }

    fun setDataBusinessMan(id: String, data: BusinessMan){
        loutaroRepository.setDataBusinessMan(id, data)
    }

}