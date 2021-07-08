package com.example.loutaro.data.source

import android.net.Uri
import com.example.loutaro.data.entity.Boards
import com.example.loutaro.data.entity.BusinessMan
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.Project
import com.example.loutaro.data.source.firebase.FireAuthService
import com.example.loutaro.data.source.firebase.FireStorageService
import com.example.loutaro.data.source.firebase.FirestoreService

class LoutaroRepository {
    companion object{
        @Volatile
        private var INSTANCE: LoutaroRepository?=null

        fun getInstance():LoutaroRepository{
            return INSTANCE?: synchronized(this){
                INSTANCE= LoutaroRepository()
                INSTANCE as LoutaroRepository
            }
        }
    }

    fun isFreelancerExist(id: String) = FirestoreService.isFreelancerExist(id)

    fun isBusinessManExist(id: String) = FirestoreService.isBusinessManExist(id)

    fun registerWithEmailAndPass(email: String, password: String) = FireAuthService.registerWithEmailAndPass(email, password)

    fun signWithEmailAndPass(email: String, password: String) = FireAuthService.signWithEmailAndPass(email, password)

    fun sendEmailVerification() = FireAuthService.sendEmailVerification()

    fun setDataFreelancer(id: String, data: Freelancer) = FirestoreService.setDataFreelancer(id, data)

    fun updateDataFreelancer(id: String, data: Freelancer) = FirestoreService.updateDataFromCreateProfile(id, data)

    fun updateFullProfileFreelancer(id: String, data: Freelancer) = FirestoreService.updateFullProfileFreelancer(id, data)

    fun getDataFreelancer(id: String) = FirestoreService.getDataFreelancer(id)

    fun getDataFreelancerRealtime(id: String) = FirestoreService.getDataFreelancerRealtime(id)

    fun getCurrentUser() = FireAuthService.getCurrentUser()

    fun putFile(filePath: Uri) = FireStorageService.putFile(filePath)

    fun addDataProject(data: Project) = FirestoreService.addDataProject(data)

    fun addDataBoards(data: Boards) = FirestoreService.addDataBoards(data)

    fun getDetailDataBoards(idBoards: String) = FirestoreService.getDetailDataBoards(idBoards)

    fun updateDataProject(idProject: String, data: Project) = FirestoreService.updateDataProject(idProject, data)

    fun updateSavedProject(id: String, isSaved: Boolean) = FirestoreService.updateSavedProject(id, isSaved)

    fun updateIdBoardProject(idProject: String, idBoards: String)= FirestoreService.updateIdBoardProject(idProject, idBoards)

    fun getDataProjects() = FirestoreService.getDataProjects()

    fun getDataSavedProject() = FirestoreService.getDataSavedProject()

    fun getDataSavedFreelancer() = FirestoreService.getDataSavedFreelancer()

    fun getDetailProject(idProject:String) = FirestoreService.getDetailProject(idProject)

    fun getActiveProjectForFreelancer(idFreelancer: String) = FirestoreService.getActiveProjectForFreelancer(idFreelancer)

    fun getActiveProjectForBusinessMan(idBusinessMan: String) = FirestoreService.getActiveProjectForBusinessMan(idBusinessMan)

    fun getFinishProjectForFreelancer(idFreelancer: String) = FirestoreService.getFinishProjectForFreelancer(idFreelancer)

    fun getFinishProjectForBusinessMan(idBusinessMan: String) = FirestoreService.getFinishProjectForFreelancer(idBusinessMan)

    fun getSearchProject(titleProject: String) = FirestoreService.getSearchProject(titleProject)

    fun setDataBusinessMan(id: String, data: BusinessMan) = FirestoreService.setDataBusinessMan(id, data)

    fun updateFullProfileBusinessMan(id: String, data: BusinessMan) = FirestoreService.updateFullProfileBusinessMan(id, data)

    fun getAllFreelancer() = FirestoreService.getAllFreelancer()

    fun getDataBusinessManRealtime(id: String) = FirestoreService.getDataBusinessManRealtime(id)
}