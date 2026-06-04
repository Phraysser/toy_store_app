package com.toystore.app.data.model

import com.google.gson.annotations.SerializedName

data class Toy(
    val id: Long = 0,
    val name: String,
    val description: String,
    val price: Double,
    @SerializedName("imageUrl")
    val imageUrl: String,
    val stock: Int,
    val category: String,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

data class User(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("username") val username: String,
    @SerializedName("role") val role: String
)
