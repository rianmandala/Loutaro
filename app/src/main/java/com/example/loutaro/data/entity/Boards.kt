package com.example.loutaro.data.entity

import java.util.*

data class Boards(
        var idBoard: String?=null,
        var name: String?=null,
        var createdBy: String?=null,
        var members: List<String>?=null,
        var listCard: List<String>?=null
)

data class ListCard(
        var idListCard: String?=null,
        var name: String?=null,
        var card: List<String>?=null
)

data class Card(
        var idCard: String?=null,
        var member: String?=null,
        var name: String?=null,
        var deskripsi: String?=null,
        var attachLink: String?=null,
        var dueDate: Date?=null,
        var checkList: List<CheckList>?=null
)

data class CheckList(
        var name: String?=null,
        var isComplete: String?=null
)
