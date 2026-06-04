package com.toystore.app.data.repository

import com.toystore.app.data.api.ToyStoreApi
import com.toystore.app.data.model.AuthResponse
import com.toystore.app.data.model.RegisterRequest
import com.toystore.app.data.model.User
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: ToyStoreApi
) {
    suspend fun login(username: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(User(username, password))
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception(response.message() ?: "Login failed"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun register(username: String, password: String): Result<Unit> {
        return try {
            val response = api.register(RegisterRequest(username, password))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception(response.message() ?: "Registration failed"))
        } catch (e: Exception) { Result.failure(e) }
    }
}