package com.example.loutaro.data.entity

data class ItemId(
        var id: String="",
        var applyers:MutableList<String>? = mutableListOf(),
        var price: Int?=0,
        var todo: MutableList<String>? = mutableListOf()
)

data class TodoId(
        var id: String="",
        var todo: String=""
)