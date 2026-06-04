package com.toystore.app.data.model

import com.google.gson.annotations.SerializedName

data class CartItem(
    val id: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("username") val username: String,
    @SerializedName("toyId") val toyId: Long,
    @SerializedName("toyName") val toyName: String,
    @SerializedName("imageUrl") val imageUrl: String?,
    val price: Double,
    val quantity: Int,
    val total: Double
)