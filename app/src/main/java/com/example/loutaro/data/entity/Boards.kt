package com.example.loutaro.data.entity

import java.util.*

data class Boards(
        var idBoard: String?=null,
        var name: String?=null,
        var createdBy: String?=null,
        var members: List<String>?=null,
        var columns: MutableList<BoardsColumn?>?=null
)

data class BoardsColumn(
        var idBoard: String?=null,
        var idBoardsColumn: String?=null,
        var name: String?=null,
        var cards: MutableList<BoardsCard?>?=null,
)

data class BoardsCard(
        var idBoardsCard: String?=null,
        var idBoardsColumn: String?=null,
        var member: String?=null,
        var name: String?=null,
        var deskripsi: String?=null,
        var attachLink: String?=null,
        var dueDate: Date?=null,
        var checkList: MutableList<CheckList?>?=null
)

data class CheckList(
        var name: String?=null,
        var isComplete: String?=null
)
