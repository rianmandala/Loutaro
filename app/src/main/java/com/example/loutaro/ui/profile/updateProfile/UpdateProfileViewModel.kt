package com.example.loutaro.ui.profile.updateProfile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.BusinessMan
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.MessageRegisterWithEmailAndPass
import com.example.loutaro.data.entity.MessageResponse
import com.example.loutaro.data.source.LoutaroRepository

class UpdateProfileViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var statusSetDataFreelancer= MutableLiveData<MessageRegisterWithEmailAndPass>()
    var statusSetDataBusinessMan= MutableLiveData<MessageRegisterWithEmailAndPass>()

    var statusPutDataAndFile = MutableLiveData<MessageResponse>()
    var statusProgressPutFile = MutableLiveData<Int>()

    var responseGetMyProfileFreelancer: MutableLiveData<Freelancer> = MutableLiveData()
    var responseGetMyProfileBusinessMan: MutableLiveData<BusinessMan> = MutableLiveData()


    fun updateFullDataFreelancer(id: String, data: Freelancer){
        loutaroRepository.updateFullProfileFreelancer(id, data).addOnSuccessListener {
            statusSetDataFreelancer.postValue(MessageRegisterWithEmailAndPass(true))
        }.addOnFailureListener {
            statusSetDataFreelancer.postValue(MessageRegisterWithEmailAndPass(false, message = it.message))
        }
    }

    fun updateFullDataBusinessMan(id: String, data: BusinessMan){
        loutaroRepository.updateFullProfileBusinessMan(id, data).addOnSuccessListener {
            statusSetDataBusinessMan.postValue(MessageRegisterWithEmailAndPass(true))
        }.addOnFailureListener {
            statusSetDataBusinessMan.postValue(MessageRegisterWithEmailAndPass(false, message = it.message))
        }
    }

    fun getCurrentUser()= loutaroRepository.getCurrentUser()

    fun getMyProfileFreelancer(){
        val id = loutaroRepository.getCurrentUser()?.uid.toString()
        loutaroRepository.getDataFreelancerRealtime(id)
            .addSnapshotListener { querySnapshot, error ->
                querySnapshot?.let { query->
                    responseGetMyProfileFreelancer.postValue(query.toObject(Freelancer::class.java))
                }
            }
    }

    fun getMyProfileBusinessMan(){
        val id = loutaroRepository.getCurrentUser()?.uid.toString()
        loutaroRepository.getDataBusinessManRealtime(id)
                .addSnapshotListener { querySnapshot, error ->
                    querySnapshot?.let { query->
                        responseGetMyProfileBusinessMan.postValue(query.toObject(BusinessMan::class.java))
                    }
                }
    }

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