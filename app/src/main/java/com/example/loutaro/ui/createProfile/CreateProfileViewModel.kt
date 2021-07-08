package com.example.loutaro.ui.createProfile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.MessageRegisterWithEmailAndPass
import com.example.loutaro.data.entity.MessageResponse
import com.example.loutaro.data.source.LoutaroRepository

class CreateProfileViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {
    var statusSetDataFreelancer= MutableLiveData<MessageRegisterWithEmailAndPass>()
    var statusPutDataAndFile = MutableLiveData<MessageResponse>()
    var statusProgressPutFile = MutableLiveData<Int>()
    fun updateDataFreelancer(id: String, data: Freelancer){
        loutaroRepository.updateDataFreelancer(id, data).addOnSuccessListener {
            statusSetDataFreelancer.postValue(MessageRegisterWithEmailAndPass(true))
        }.addOnFailureListener {
            statusSetDataFreelancer.postValue(MessageRegisterWithEmailAndPass(false, message = it.message))
        }
    }

    fun sendEmailVerification()= loutaroRepository.sendEmailVerification()

    fun getCurrentUser()= loutaroRepository.getCurrentUser()

    fun putDataAndFile(filePath: Uri) {
        loutaroRepository.putFile(filePath).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {uri->
                statusPutDataAndFile.postValue(MessageResponse(true, uri.toString()))
            }.addOnFailureListener { exception->
                statusPutDataAndFile.postValue(MessageResponse(false, exception.message))
            }
        }.addOnFailureListener { error ->
            statusPutDataAndFile.postValue(MessageResponse(false, error.message))
        }.addOnProgressListener { taskSnapshot ->
            val progress = ((100.0
                    * taskSnapshot.bytesTransferred
                    / taskSnapshot.totalByteCount))
            statusProgressPutFile.postValue(progress.toInt())
        }
    }

}