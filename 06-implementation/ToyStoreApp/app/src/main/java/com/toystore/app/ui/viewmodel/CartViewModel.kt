package com.toystore.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toystore.app.data.model.CartItem
import com.toystore.app.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class CartUiState {
    object Loading : CartUiState()
    data class Success(val items: List<CartItem>) : CartUiState()
    data class Error(val message: String) : CartUiState()
}

class CartViewModel : ViewModel() {

    private val repository = CartRepository()
    private val _state = MutableStateFlow<CartUiState>(CartUiState.Loading)
    val state: StateFlow<CartUiState> = _state

    private var authToken: String = ""

    fun setToken(token: String) {
        authToken = if (token.startsWith("Bearer ", ignoreCase = true)) token else "Bearer $token"
    }

    fun loadCart() {
        viewModelScope.launch {
            _state.value = CartUiState.Loading
            repository.getCart(authToken)
                .onSuccess { items ->
                    _state.value = CartUiState.Success(items)
                }
                .onFailure { error ->
                    _state.value = CartUiState.Error(error.message ?: "Failed to load cart")
                }
        }
    }

    fun addToCart(toyId: Long, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(authToken, toyId, quantity)
                .onSuccess {
                    loadCart() // Обновляем список после добавления
                }
                .onFailure { /* Можно показать ошибку */ }
        }
    }

    fun removeFromCart(cartId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(authToken, cartId)
                .onSuccess {
                    loadCart() // Обновляем список после удаления
                }
                .onFailure { /* Можно показать ошибку */ }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart(authToken)
                .onSuccess {
                    loadCart()
                }
                .onFailure { /* Можно показать ошибку */ }
        }
    }
}