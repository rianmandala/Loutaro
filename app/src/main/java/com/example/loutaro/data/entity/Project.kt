package com.example.loutaro.data.entity

import com.google.firebase.Timestamp
import com.google.firebase.firestore.ServerTimestamp

data class Project(
        var idProject: String?=null,
        var idFreelancer: List<String>?=null,
        var idBusinessMan: String?=null,
        var idBoards: String?=null,
        @field:JvmField
        var isSaved: Boolean?=null,
        var title: String?=null,
        var category: String?=null,
        var num_freelancer:Int?=null,
        var description: String?=null,
        var skills:List<String>?=null,
        var tasks: List<Task>?=null,
        var budget: Int?=null,
        @ServerTimestamp
        var timeStamp: Timestamp?=null,
        @field:JvmField
        var statusCompleted: Boolean?=null,
)

data class Task(
        var applyers:MutableList<String>?=null,
        var price: Int?=null,
        var todo: MutableList<String>?=null
)