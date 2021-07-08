package com.example.loutaro.ui.createProject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loutaro.data.entity.Boards
import com.example.loutaro.data.entity.MessageResponse
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.LoutaroRepository

class CreateProjectViewModel(private val loutaroRepository: LoutaroRepository): ViewModel() {

    var responseAddDataProject: MutableLiveData<MessageResponse> = MutableLiveData()
    var responseGetDataProject: MutableLiveData<Project?> = MutableLiveData()
    var responseAddDataBoards: MutableLiveData<MessageResponse> = MutableLiveData()
    var responseUpdateIdBoardProject: MutableLiveData<MessageResponse> = MutableLiveData()

    fun addDataProject(data: Project){
        loutaroRepository.addDataProject(data).addOnSuccessListener {
            responseAddDataProject.postValue(MessageResponse(true, it.id))
        }.addOnFailureListener {
            responseAddDataProject.postValue(MessageResponse(status = false, response = it.message.toString()))
        }
    }

    fun getDetailProject(idProject: String){
        loutaroRepository.getDetailProject(idProject).addOnSuccessListener { project->
            val projectConvert = project.toObject(Project::class.java)
            responseGetDataProject.postValue(projectConvert)
        }.addOnFailureListener {
            responseGetDataProject.postValue(null)
        }
    }

    fun addDataBoards(data: Boards){
        loutaroRepository.addDataBoards(data).addOnSuccessListener {
            responseAddDataBoards.postValue(MessageResponse(true, it.id))
        }.addOnFailureListener {
            responseAddDataBoards.postValue(MessageResponse(false, it.message))
        }
    }

    fun updateIdBoardProject(idProject: String, idBoards: String){
        loutaroRepository.updateIdBoardProject(idProject, idBoards).addOnSuccessListener {
            responseUpdateIdBoardProject.postValue(MessageResponse(true))
        }.addOnFailureListener {
            responseUpdateIdBoardProject.postValue(MessageResponse(false, it.message))
        }
    }

}