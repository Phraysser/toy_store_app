package com.toystore.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "toys_cache")
data class CachedToy(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String = "",
    val price: Double,
    val imageUrl: String = "",
    val stock: Int,
    val category: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)