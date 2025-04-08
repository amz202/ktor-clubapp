package com.example.data.model


data class MyAuthenticatedUser(
    val id: String,
    val email: String,
    val name: String?,
    val role:String,
    val schoolName:String? = null,
)
