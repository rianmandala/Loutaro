package com.example.loutaro.data.entity

import com.google.firebase.Timestamp
import java.util.*

data class Boards(
        var idBoard: String?=null,
        var name: String?=null,
        var createdBy: String?=null,
        var members: MutableList<String>?=null,
        var columns: MutableList<BoardsColumn?>?=null
)

data class BoardsColumn(
        var idBoard: String?=null,
        var idBoardsColumn: String?=null,
        var name: String?=null,
        var cards: MutableList<BoardsCard?> = mutableListOf(),
)

data class BoardsCard(
        var idBoardsCard: String?=null,
        var idBoardsColumn: String?=null,
        var member: String?=null,
        var name: String?=null,
        var description: String?=null,
        var attachLink: MutableList<AttachLink>?=null,
        var dueDate: Timestamp?=null,
        var checkList: MutableList<CheckList?>?=null,
        var activity: MutableList<Activity>?=null
)

data class AttachLink(
        var name: String?=null,
        var link: String?=null,
        var attachedBy: String?=null
)

data class Activity(
        var idUser: String?=null,
        var photo: String?=null,
        var name:String?=null,
        var message: String?=null,
)

data class CheckList(
        var name: String?=null,
        var isComplete: String?=null
)
