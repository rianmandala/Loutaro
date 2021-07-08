package com.example.loutaro.data.entity

data class MessageRegisterWithEmailAndPass(
    var status: Boolean,
    var uid: String?="",
    var message: String?=""
)

data class MessageResponse(
    var status: Boolean,
    var response: String?=""
)