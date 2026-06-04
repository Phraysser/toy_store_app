package com.toystore.app.data.repository

import android.content.Context
import com.toystore.app.data.api.ToyStoreApi
import com.toystore.app.data.local.AppDatabase
import com.toystore.app.data.local.ToyMapper
import com.toystore.app.data.model.Toy
import com.toystore.app.utils.NetworkUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToyRepository @Inject constructor(
    private val api: ToyStoreApi = ToyStoreApi.create(),
    @ApplicationContext private val context: Context
) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.toyDao()


    fun getAllToys(token: String): Flow<Result<List<Toy>>> = flow {
        android.util.Log.d("REPO_DEBUG", "🌐 getAllToys called")


        emit(Result.success(emptyList<Toy>()))


        val cachedToys = dao.getAllToys().firstOrNull() ?: emptyList()
        android.util.Log.d("REPO_DEBUG", "💾 Cache: ${cachedToys.size} toys")

        if (cachedToys.isNotEmpty()) {
            emit(Result.success(ToyMapper.toDomainList(cachedToys)))
        }


        val isOnline = NetworkUtils.isOnline(context)
        android.util.Log.d("REPO_DEBUG", "📡 Network online: $isOnline")

        if (isOnline) {
            try {
                android.util.Log.d("REPO_DEBUG", "🚀 Calling API with token: ${token.take(30)}...")
                val response = api.getAllToys(token)
                android.util.Log.d("REPO_DEBUG", "📥 API response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val freshToys = response.body()!!
                    android.util.Log.d("REPO_DEBUG", "✨ Received ${freshToys.size} toys from API")
                    dao.insertToys(ToyMapper.toCachedList(freshToys))
                    emit(Result.success(freshToys))
                } else {
                    android.util.Log.e("REPO_DEBUG", "❌ API error: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("REPO_DEBUG", "💥 Exception: ${e.message}", e)
            }
        }
    }


    fun searchToys(token: String, query: String?): Flow<Result<List<Toy>>> = flow {
        val searchQuery = query?.trim()?.takeIf { it.isNotEmpty() }

        if (searchQuery.isNullOrEmpty()) {
            getAllToys(token).firstOrNull()?.let { emit(it) }
            return@flow
        }


        val cachedToys = dao.searchToys(searchQuery).firstOrNull() ?: emptyList()
        if (cachedToys.isNotEmpty()) {
            emit(Result.success(ToyMapper.toDomainList(cachedToys)))
        }


        if (NetworkUtils.isOnline(context)) {
            try {
                val response = api.searchToys(token, searchQuery)
                if (response.isSuccessful && response.body() != null) {
                    val freshToys = response.body()!!
                    dao.insertToys(ToyMapper.toCachedList(freshToys))
                    emit(Result.success(freshToys))
                }
            } catch (e: Exception) {
                android.util.Log.e("REPO_DEBUG", "Search error: ${e.message}")
            }
        }
    }


    suspend fun createToy(token: String, toy: Toy): Result<Toy> {
        return try {
            val response = api.createToy(token, toy)
            if (response.isSuccessful && response.body() != null) {
                val createdToy = response.body()!!
                dao.insertToy(ToyMapper.toCached(createdToy))
                Result.success(createdToy)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to create toy"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateToy(token: String, toyId: Long, toy: Toy): Result<Toy> {
        return try {
            val response = api.updateToy(token, toyId, toy)
            if (response.isSuccessful && response.body() != null) {
                val updatedToy = response.body()!!
                dao.insertToy(ToyMapper.toCached(updatedToy))
                Result.success(updatedToy)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to update toy"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deleteToy(token: String, toyId: Long): Result<Unit> {
        return try {
            val response = api.deleteToy(token, toyId)
            if (response.isSuccessful) {
                dao.deleteToyById(toyId)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to delete toy"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun getToyById(token: String, toyId: Long): Result<Toy> {
        return try {
            val cached = dao.getToyById(toyId)
            if (cached != null) {
                return Result.success(ToyMapper.toDomain(cached))
            }

            if (NetworkUtils.isOnline(context)) {
                val response = api.getToyById(token, toyId)
                if (response.isSuccessful && response.body() != null) {
                    val toy = response.body()!!
                    dao.insertToy(ToyMapper.toCached(toy))
                    Result.success(toy)
                } else {
                    Result.failure(Exception(response.message() ?: "Failed to load toy"))
                }
            } else {
                Result.failure(Exception("No internet connection"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun uploadImage(imageFile: File): Result<String> {
        return try {
            val mediaType = "image/*".toMediaType()
            val requestFile = imageFile.asRequestBody(mediaType)
            val body = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            val response = api.uploadImage(body)
            if (response.isSuccessful && response.body() != null) {
                val imageUrl = response.body()!!.imageUrl
                    ?: response.body()!!.url
                    ?: ""
                Result.success(imageUrl)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to upload image"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun addToCart(token: String, toyId: Long, quantity: Int): Result<Unit> {
        return try {
            val response = api.addToCart(token, toyId, quantity)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to add to cart"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}