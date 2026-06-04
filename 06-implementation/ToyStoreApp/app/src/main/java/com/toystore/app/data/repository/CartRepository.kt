package com.toystore.app.data.repository

import com.toystore.app.data.api.ToyStoreApi
import com.toystore.app.data.model.CartItem

class CartRepository(
    private val api: ToyStoreApi = ToyStoreApi.create()
) {
    suspend fun getCart(token: String): Result<List<CartItem>> {
        return try {
            val response = api.getCart(token)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to load cart"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addToCart(token: String, toyId: Long, quantity: Int): Result<CartItem> {
        return try {
            val response = api.addToCart(token, toyId, quantity)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to add to cart"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeFromCart(token: String, cartId: Long): Result<Unit> {
        return try {
            val response = api.removeFromCart(token, cartId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to remove from cart"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(token: String): Result<Unit> {
        return try {
            val response = api.clearCart(token)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message() ?: "Failed to clear cart"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}