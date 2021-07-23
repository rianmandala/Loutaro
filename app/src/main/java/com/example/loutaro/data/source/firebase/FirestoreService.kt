package com.example.loutaro.data.source.firebase

import com.example.loutaro.data.entity.*
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*

object FirestoreService {
    private val dbFreelancer = FirebaseFirestore.getInstance().collection("Freelancer")
    private val dbBusinessMan = FirebaseFirestore.getInstance().collection("BusinessMan")
    private val dbProject = FirebaseFirestore.getInstance().collection("Projects")
    private val dbBoards = FirebaseFirestore.getInstance().collection("Boards")
    private val dbWithdrawal = FirebaseFirestore.getInstance().collection("Withdrawal")

    fun isFreelancerExist(id: String): Task<DocumentSnapshot> {
        return dbFreelancer.document(id).get()
    }

    fun isBusinessManExist(id: String): Task<DocumentSnapshot> {
        return dbBusinessMan.document(id).get()
    }

    fun withdrawalMoney(withdrawal: Withdrawal): Task<DocumentReference> {
        return dbWithdrawal.add(withdrawal)
    }

    fun setDataBusinessMan(id: String, data: BusinessMan): Task<Void> {
        return dbBusinessMan.document(id).set(data, SetOptions.merge())
    }

    fun updateFullProfileBusinessMan(id: String, data: BusinessMan): Task<Void> {
        val dataProfile= mapOf(
                "photo" to data.photo,
                "name" to data.name,
                "country" to data.country,
                "address" to data.address,
                "contact" to data.contact,
                "bio" to data.bio,
                "portofolio" to data.portofolio,
                )
        return dbBusinessMan.document(id).update(dataProfile)
    }

    fun getDataBusinessManRealtime(id: String): DocumentReference {
        return dbBusinessMan.document(id)
    }

    fun getAllFreelancer(): CollectionReference {
        return dbFreelancer
    }

    fun getListFreelancerWithListIdFreelancer(listIdFreelancer: List<String>): Query {
        return dbFreelancer
            .whereIn(FieldPath.documentId(), listIdFreelancer)

    }

    fun getListProjectWithListIdProject(listIdProject: List<String>): Query {
        return dbProject
                .whereIn(FieldPath.documentId(), listIdProject)

    }


    fun setDataFreelancer(id: String, data: Freelancer): Task<Void> {
        return dbFreelancer.document(id).set(data, SetOptions.merge())
    }

    fun updateDataFromCreateProfile(id: String, data: Freelancer): Task<Void> {
        val newDataProfile = mapOf(
                "photo" to data.photo,
                "bio" to data.bio,
                "service" to data.service,
                "skills" to data.skills,

        )
        return dbFreelancer.document(id)
                .update(newDataProfile)
    }


    fun updateFullProfileFreelancer(id: String, data: Freelancer): Task<Void> {
        val dataProfile= mapOf(
            "photo" to data.photo,
            "name" to data.name,
            "country" to data.country,
            "address" to data.address,
            "contact" to data.contact,
            "portofolio" to data.portofolio,
            "service" to data.service,
            "skills" to data.skills,
            "bio" to data.bio
        )
        return dbFreelancer.document(id).update(dataProfile)
    }

    fun getDataFreelancer(id: String): Task<DocumentSnapshot> {
        return dbFreelancer.document(id).get()
    }

    fun getDataFreelancerRealtime(id: String): DocumentReference {
        return dbFreelancer.document(id)
    }

    fun updateSavedProject(id: String, isSaved: Boolean): Task<Void> {
        return dbProject.document(id)
                .update("isSaved", isSaved)
    }

    fun updateIdBoardProject(idProject: String, idBoards: String): Task<Void> {
        return dbProject.document(idProject)
            .update("idBoards", idBoards)
    }

    fun addDataProject(data: Project): Task<DocumentReference> {
        return dbProject.add(data)
    }

    fun deleteProject(idProject: String): Task<Void> {
        return dbProject.document(idProject).delete()
    }

    fun addMemberToBoards(idBoards: String, idMember: String): Task<Void> {
        return dbBoards.document(idBoards)
            .update("members", FieldValue.arrayUnion(idMember))
    }

    fun deleteMemberFromBoards(idBoards: String, idMember: String): Task<Void> {
        return dbBoards.document(idBoards)
                .update("members", FieldValue.arrayRemove(idMember))
    }

    fun getListMember(idBoards: String="", listIdMember: List<String>): Query {
        return dbFreelancer
            .whereIn(FieldPath.documentId(), listIdMember)
    }

    fun addDataBoards(data: Boards): Task<DocumentReference> {
        return dbBoards.add(data)
    }

    fun addBoardsColumn(idBoards: String, boardsColumn: BoardsColumn): Task<Void> {
        return dbBoards.document(idBoards)
                .update("columns",FieldValue.arrayUnion(boardsColumn))
    }

    fun updateBoards(idBoards: String, boards: Boards): Task<Void> {
        val updateBords= mapOf(
                "name" to boards.name,
                "createdBy" to boards.createdBy,
                "members" to boards.members,
                "columns" to boards.columns
        )
        return dbBoards.document(idBoards).update(updateBords)
    }

    fun updateBoardsColumn(idBoards: String, boardsColumn: List<BoardsColumn?>?): Task<Void> {
        return dbBoards.document(idBoards)
                .update("columns",boardsColumn)
    }

    fun getDetailDataBoards(idBoards: String): DocumentReference {
        return dbBoards.document(idBoards)
    }

    fun confirmPayment(idProject: String): Task<Void> {
        return dbProject.document(idProject)
            .update("paymentStatus",true)
    }

    fun confirmApplyers(idProject: String, project: Project): Task<Void> {
        val updateProject= mapOf(
            "idFreelancer" to project.idFreelancer,
            "tasks" to project.tasks
        )
        return dbProject.document(idProject)
            .update(updateProject)
    }

    fun projectHasCompleted(idProject: String): Task<Void> {
        return dbProject.document(idProject)
                .update("statusCompleted",true)
    }

    fun startProject(idProject: String, projectStartDate: Timestamp): Task<Void> {
        return dbProject.document(idProject)
                .update("projectDueDate",projectStartDate)
    }

    fun updateBalanceFreelancer(idFreelancer: String, balance: Long): Task<Void> {
        return dbFreelancer.document(idFreelancer)
            .update("balance", FieldValue.increment(balance))
    }

    fun decreaseBalanceFreelancer(idFreelancer: String, balance: Long): Task<Void> {
        return dbFreelancer.document(idFreelancer)
            .update("balance", balance)
    }

    fun updateDataProject(idProject: String, data: Project): Task<Void> {
        val newDataProject= mapOf(
                "title" to data.title,
                "description" to data.description,
                "category" to data.category,
                "budget" to data.budget,
                "num_freelancer" to data.num_freelancer,
                "skills" to data.skills,
                "tasks" to data.tasks
        )
        return dbProject.document(idProject).update(newDataProject)
    }

    fun applyAsFreelancerToProject(idProject: String, dataProject: Project): Task<Void> {
        return dbProject.document(idProject)
            .update("tasks", dataProject.tasks)
    }

    fun getDataProjects(): Query {
        return dbProject
            .whereEqualTo("statusPending",false)
    }

    fun getDataProjectsOngoingForBusinessMan(idBusinessMan: String): Query {
        return dbProject
            .whereEqualTo("statusCompleted", false)
            .whereEqualTo("statusPending",false)
            .whereEqualTo("idBusinessMan", idBusinessMan)
    }

    fun getDetailProject(idProject: String): Task<DocumentSnapshot> {
        return dbProject.document(idProject).get()
    }

    fun getRealtimeDetailProject(idProject: String): DocumentReference {
        return dbProject.document(idProject)
    }

    fun addMemberToProject(idProject: String, idMember: String): Task<Void> {
        return dbProject.document(idProject)
            .update("idFreelancer", FieldValue.arrayUnion(idMember))
    }

    fun removeMemberFromProject(idProject: String, project: Project): Task<Void> {
        val updateProject = mapOf(
            "idFreelancer" to project.idFreelancer,
            "tasks" to project.tasks
        )
        return dbProject.document(idProject)
            .update(updateProject)
    }

    fun getActiveProjectForFreelancer(idFreelancer: String): Query {
        return dbProject
            .whereArrayContains("idFreelancer", idFreelancer)
            .whereEqualTo("statusPending",false)
            .whereEqualTo("statusCompleted",false)
    }

    fun getActiveProjectForBusinessMan(idBusinessMan: String): Query {
        return dbProject
            .whereEqualTo("idBusinessMan", idBusinessMan)
            .whereEqualTo("statusPending",false)
            .whereEqualTo("statusCompleted",false)
    }

    fun getPendingProjectForBusinessMan(idBusinessMan: String): Query {
        return dbProject
            .whereEqualTo("idBusinessMan",idBusinessMan)
            .whereEqualTo("statusPending", true)
            .whereEqualTo("statusCompleted",false)
    }

    fun getFinishProjectForFreelancer(idFreelancer: String): Query {
        return dbProject
            .whereArrayContains("idFreelancer", idFreelancer)
            .whereEqualTo("statusPending", false)
            .whereEqualTo("statusCompleted",true)
    }

    fun getFinishProjectForBusinessMan(idBusinessMan: String): Query {
        return dbProject
            .whereEqualTo("idBusinessMan", idBusinessMan)
            .whereEqualTo("statusPending", false)
            .whereEqualTo("statusCompleted",true)
    }

    fun getSearchProject(titleProject: String): Query {
        return dbProject
            .whereGreaterThanOrEqualTo("title",titleProject)
            .whereEqualTo("statusPending",false)
    }

}