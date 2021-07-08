package com.example.loutaro.data.source.firebase

import com.example.loutaro.data.entity.Boards
import com.example.loutaro.data.entity.BusinessMan
import com.example.loutaro.data.entity.Freelancer
import com.example.loutaro.data.entity.Project
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*

object FirestoreService {
    private val dbFreelancer = FirebaseFirestore.getInstance().collection("Freelancer")
    private val dbBusinessMan = FirebaseFirestore.getInstance().collection("BusinessMan")
    private val dbProject = FirebaseFirestore.getInstance().collection("Projects")
    private val dbBoards = FirebaseFirestore.getInstance().collection("Boards")

    fun isFreelancerExist(id: String): Task<DocumentSnapshot> {
        return dbFreelancer.document(id).get()
    }

    fun isBusinessManExist(id: String): Task<DocumentSnapshot> {
        return dbBusinessMan.document(id).get()
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
        return dbFreelancer.document(id).update(dataProfile)
    }

    fun getDataBusinessManRealtime(id: String): DocumentReference {
        return dbBusinessMan.document(id)
    }

    fun getAllFreelancer(): CollectionReference {
        return dbFreelancer
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

    fun addDataBoards(data: Boards): Task<DocumentReference> {
        return dbBoards.add(data)
    }

    fun getDetailDataBoards(idBoards: String): DocumentReference {
        return dbBoards.document(idBoards)
    }

    fun updateDataProject(idProject: String, data: Project): Task<Void> {
        val newDataProject= mapOf(
                "name" to data.title,
                "description" to data.description,
                "category" to data.category,
                "skills" to data.skills,
                "budget" to data.budget,
                "num_freelancer" to data.num_freelancer,
                "skills" to data.skills,
                "tasks" to data.tasks
        )
        return dbProject.document(idProject).update(newDataProject)
    }

    fun getDataProjects(): CollectionReference {
        return dbProject
    }

    fun getDataSavedProject(): Query {
        return dbProject
                .whereEqualTo("isSaved", true)
    }

    fun getDataSavedFreelancer(): Query {
        return dbFreelancer
                .whereEqualTo("isSaved", true)
    }

    fun getDetailProject(idProject: String): Task<DocumentSnapshot> {
        return dbProject.document(idProject).get()
    }

    fun getActiveProjectForFreelancer(idFreelancer: String): Query {
        return dbProject
            .whereArrayContains("idFreelancer", idFreelancer)
    }

    fun getActiveProjectForBusinessMan(idBusinessMan: String): Query {
        return dbProject
            .whereEqualTo("idBusinessMan", idBusinessMan)
    }

    fun getFinishProjectForFreelancer(idFreelancer: String): Query {
        return dbProject
            .whereArrayContains("idFreelancer", idFreelancer)
            .whereEqualTo("statusCompleted",true)
    }

    fun getFinishProjectForBusinessMan(idBusinessMan: String): Query {
        return dbProject
            .whereArrayContains("idBusinessMan", idBusinessMan)
            .whereEqualTo("statusCompleted",true)
    }

    fun getSearchProject(titleProject: String): Query {
        return dbProject
            .whereGreaterThanOrEqualTo("title",titleProject)
    }

}