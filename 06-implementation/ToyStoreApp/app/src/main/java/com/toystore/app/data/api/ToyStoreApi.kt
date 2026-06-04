package com.toystore.app.data.api

import com.toystore.app.data.model.AuthResponse
import com.toystore.app.data.model.Toy
import com.toystore.app.data.model.User
import com.toystore.app.data.model.CartItem
import com.toystore.app.data.model.FileUploadResponse
import com.toystore.app.data.model.RegisterRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface ToyStoreApi {

    @Multipart
    @POST("api/upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<FileUploadResponse>

    @GET("api/toys/search")
    suspend fun searchToys(
        @Header("Authorization") token: String,
        @Query("query") query: String? = null
    ): Response<List<Toy>>

    @DELETE("api/toys/{id}")
    suspend fun deleteToy(@Header("Authorization") token: String, @Path("id") id: Long): Response<Unit>

    @POST("api/toys")
    suspend fun createToy(@Header("Authorization") token: String, @Body toy: Toy): Response<Toy>

    @POST("api/auth/login")
    suspend fun login(@Body user: User): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @GET("api/toys")
    suspend fun getAllToys(@Header("Authorization") token: String): Response<List<Toy>>

    @GET("api/toys/{id}")
    suspend fun getToyById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Toy>

    @PUT("api/toys/{id}")
    suspend fun updateToy(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body toy: Toy
    ): Response<Toy>
    // 👇 ИСПРАВЛЕНО: CartResponse → CartItem
    @POST("api/cart/add")
    suspend fun addToCart(
        @Header("Authorization") token: String,
        @Query("toyId") toyId: Long,
        @Query("quantity") quantity: Int
    ): Response<CartItem>

    @GET("api/cart")
    suspend fun getCart(@Header("Authorization") token: String): Response<List<CartItem>>

    @DELETE("api/cart/{id}")
    suspend fun removeFromCart(
        @Header("Authorization") token: String,
        @Path("id") cartId: Long
    ): Response<Unit>

    @DELETE("api/cart/clear")
    suspend fun clearCart(@Header("Authorization") token: String): Response<Unit>

    companion object {
        fun create(): ToyStoreApi {
            return Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ToyStoreApi::class.java)
        }
    }
}