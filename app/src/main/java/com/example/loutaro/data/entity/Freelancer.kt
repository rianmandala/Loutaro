package com.example.loutaro.data.entity

data class Freelancer(
    var idFreelancer: String?=null,
    @field:JvmField
    var isSaved: Boolean?=null,
    var name: String?=null,
    var photo: String?=null,
    var email: String?=null,
    var country: String?=null,
    var address: String?=null,
    var bio: String?=null,
    var contact:Contact?= null,
    var service: String?=null,
    var skills: List<String>? = null,
    var portofolio: String?=null
)