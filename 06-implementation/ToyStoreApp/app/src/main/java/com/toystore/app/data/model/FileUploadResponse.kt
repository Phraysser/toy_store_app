package com.toystore.app.data.model

import com.google.gson.annotations.SerializedName

data class FileUploadResponse(
    @SerializedName("imageUrl") val imageUrl: String? = null,
    @SerializedName("url") val url: String? = null
)