package com.example.loutaro.data.source

import android.net.Uri
import com.example.loutaro.data.entity.*
import com.example.loutaro.data.source.firebase.FireAuthService
import com.example.loutaro.data.source.firebase.FireStorageService
import com.example.loutaro.data.source.firebase.FirestoreService
import com.google.firebase.Timestamp

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

    fun deleteProject(idProject: String)= FirestoreService.deleteProject(idProject)

    fun applyAsFreelancerToProject(idProject: String, dataProject: Project)= FirestoreService.applyAsFreelancerToProject(idProject, dataProject)

    fun addMemberToBoards(idBoards: String, idMember: String)= FirestoreService.addMemberToBoards(idBoards, idMember)

    fun addMemberToProject(idProject: String, idMember: String)= FirestoreService.addMemberToProject(idProject, idMember)

    fun removeMemberFromProject(idProject: String, project: Project)= FirestoreService.removeMemberFromProject(idProject, project)

    fun deleteMemberFromBoards(idBoards: String, idMember: String)= FirestoreService.deleteMemberFromBoards(idBoards, idMember)

    fun getListMember(idBoards: String, listIdMember: List<String>)= FirestoreService.getListMember(idBoards, listIdMember)

    fun addDataBoards(data: Boards) = FirestoreService.addDataBoards(data)

    fun addBoardsColumn(idBoards: String, boardsColumn: BoardsColumn) = FirestoreService.addBoardsColumn(idBoards, boardsColumn)

    fun updateBoards(idBoards: String, boards: Boards) = FirestoreService.updateBoards(idBoards, boards)

    fun updateBoardsColumn(idBoards: String, boardsColumn: List<BoardsColumn?>?) = FirestoreService.updateBoardsColumn(idBoards, boardsColumn)

    fun getDetailDataBoards(idBoards: String) = FirestoreService.getDetailDataBoards(idBoards)

    fun getDataProjectsOngoingForBusinessMan(idBusinessMan: String)= FirestoreService.getDataProjectsOngoingForBusinessMan(idBusinessMan)

    fun updateDataProject(idProject: String, data: Project) = FirestoreService.updateDataProject(idProject, data)

    fun confirmApplyers(idProject: String, project: Project) = FirestoreService.confirmApplyers(idProject, project)

    fun updateSavedProject(id: String, isSaved: Boolean) = FirestoreService.updateSavedProject(id, isSaved)

    fun updateIdBoardProject(idProject: String, idBoards: String)= FirestoreService.updateIdBoardProject(idProject, idBoards)

    fun getDataProjects() = FirestoreService.getDataProjects()

    fun getDetailProject(idProject:String) = FirestoreService.getDetailProject(idProject)

    fun getRealtimeDetailProject(idProject: String) = FirestoreService.getRealtimeDetailProject(idProject)

    fun getActiveProjectForFreelancer(idFreelancer: String) = FirestoreService.getActiveProjectForFreelancer(idFreelancer)

    fun getActiveProjectForBusinessMan(idBusinessMan: String) = FirestoreService.getActiveProjectForBusinessMan(idBusinessMan)

    fun getPendingProjectForBusinessMan(idBusinessMan: String) = FirestoreService.getPendingProjectForBusinessMan(idBusinessMan)

    fun getFinishProjectForFreelancer(idFreelancer: String) = FirestoreService.getFinishProjectForFreelancer(idFreelancer)

    fun getFinishProjectForBusinessMan(idBusinessMan: String) = FirestoreService.getFinishProjectForBusinessMan(idBusinessMan)

    fun getSearchProject(titleProject: String) = FirestoreService.getSearchProject(titleProject)

    fun setDataBusinessMan(id: String, data: BusinessMan) = FirestoreService.setDataBusinessMan(id, data)

    fun updateFullProfileBusinessMan(id: String, data: BusinessMan) = FirestoreService.updateFullProfileBusinessMan(id, data)

    fun getAllFreelancer() = FirestoreService.getAllFreelancer()

    fun getDataBusinessManRealtime(id: String) = FirestoreService.getDataBusinessManRealtime(id)

    fun getListFreelancerWithListIdFreelancer(listIdFreelancer: List<String>) = FirestoreService.getListFreelancerWithListIdFreelancer(listIdFreelancer)

    fun getListProjectWithListIdProject(listIdProject: List<String>) = FirestoreService.getListProjectWithListIdProject(listIdProject)

    fun confirmPayment(idProject: String) = FirestoreService.confirmPayment(idProject)

    fun projectHasCompleted(idProject: String) = FirestoreService.projectHasCompleted(idProject)

    fun startProject(idProject: String, projectStartDate: Timestamp) = FirestoreService.startProject(idProject, projectStartDate)

    fun updateBalanceFreelancer(idFreelancer: String, balance: Long) = FirestoreService.updateBalanceFreelancer(idFreelancer, balance)

    fun withdrawalMoney(withdrawal: Withdrawal) = FirestoreService.withdrawalMoney(withdrawal)

    fun decreaseBalanceFreelancer(idFreelancer: String, balance: Long) = FirestoreService.decreaseBalanceFreelancer(idFreelancer, balance)
}