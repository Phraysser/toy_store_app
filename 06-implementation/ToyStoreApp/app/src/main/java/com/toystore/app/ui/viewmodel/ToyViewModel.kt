package com.toystore.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toystore.app.data.model.Toy
import com.toystore.app.data.repository.ToyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed class ToyUiState {
    object Loading : ToyUiState()
    data class Success(val toys: List<Toy>) : ToyUiState()
    data class Error(val message: String) : ToyUiState()
}

@HiltViewModel
class ToyViewModel @Inject constructor(
    private val repository: ToyRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ToyUiState>(ToyUiState.Loading)
    val state: StateFlow<ToyUiState> = _state

    private val _detailState = MutableStateFlow<ToyUiState>(ToyUiState.Loading)
    val detailState: StateFlow<ToyUiState> = _detailState

    private var authToken: String = ""

    fun setToken(token: String) {
        authToken = if (token.startsWith("Bearer ", ignoreCase = true)) {
            token
        } else {
            "Bearer $token"
        }
        android.util.Log.d("AUTH_DEBUG", "Token set: ${authToken.take(30)}...")
    }

    fun loadToys() {
        viewModelScope.launch {
            android.util.Log.d("TOY_DEBUG", "🔄 loadToys() called, token length: ${authToken.length}")
            _state.value = ToyUiState.Loading

            repository.getAllToys(authToken).collect { result ->
                android.util.Log.d("TOY_DEBUG", "📦 collect received result: ${result.isSuccess}")
                result
                    .onSuccess { toys ->
                        android.util.Log.d("TOY_DEBUG", "✅ Loaded ${toys.size} toys")
                        _state.value = ToyUiState.Success(toys)
                    }
                    .onFailure { error ->
                        android.util.Log.e("TOY_DEBUG", "❌ Failed: ${error.message}", error)
                        _state.value = ToyUiState.Error(error.message ?: "Failed to load toys")
                    }
            }
        }
    }

    fun loadToyById(toyId: Long) {
        viewModelScope.launch {
            _detailState.value = ToyUiState.Loading
            val result = repository.getToyById(authToken, toyId)
            result
                .onSuccess { toy ->
                    _detailState.value = ToyUiState.Success(listOf(toy))
                }
                .onFailure { error ->
                    _detailState.value = ToyUiState.Error(error.message ?: "Failed to load toy")
                }
        }
    }

    fun updateToyWithImage(toyId: Long, updatedToy: Toy, imageFile: File?) {
        viewModelScope.launch {
            _detailState.value = ToyUiState.Loading

            if (imageFile != null && imageFile.exists()) {
                repository.uploadImage(imageFile)
                    .onSuccess { imageUrl ->
                        repository.updateToy(authToken, toyId, updatedToy.copy(imageUrl = imageUrl))
                            .onSuccess { loadToyById(toyId) }
                            .onFailure { error ->
                                _detailState.value = ToyUiState.Error(error.message ?: "Update failed")
                            }
                    }
                    .onFailure { error ->
                        _detailState.value = ToyUiState.Error(error.message ?: "Image upload failed")
                    }
            } else {
                repository.updateToy(authToken, toyId, updatedToy)
                    .onSuccess { loadToyById(toyId) }
                    .onFailure { error ->
                        _detailState.value = ToyUiState.Error(error.message ?: "Update failed")
                    }
            }
        }
    }

    fun createToyWithImage(toy: Toy, imageFile: File?) {
        viewModelScope.launch {
            _state.value = ToyUiState.Loading
            if (imageFile != null && imageFile.exists()) {
                repository.uploadImage(imageFile)
                    .onSuccess { imageUrl ->
                        createToyInternal(toy.copy(imageUrl = imageUrl))
                    }
                    .onFailure { error ->
                        _state.value = ToyUiState.Error("Failed to upload image: ${error.message}")
                    }
            } else {
                createToyInternal(toy)
            }
        }
    }

    private suspend fun createToyInternal(toy: Toy) {
        repository.createToy(authToken, toy)
            .onSuccess {
                kotlinx.coroutines.delay(2000)
                loadToys()
            }
            .onFailure { error ->
                _state.value = ToyUiState.Error(error.message ?: "Failed to create toy")
            }
    }

    fun deleteToy(toyId: Long) {
        viewModelScope.launch {
            repository.deleteToy(authToken, toyId)
                .onSuccess { loadToys() }
                .onFailure { error ->
                    _state.value = ToyUiState.Error(error.message ?: "Failed to delete toy")
                }
        }
    }


    fun searchToys(query: String) {
        viewModelScope.launch {
            _state.value = ToyUiState.Loading
            repository.searchToys(authToken, query).collect { result ->
                result
                    .onSuccess { toys ->
                        _state.value = ToyUiState.Success(toys)
                    }
                    .onFailure { error ->
                        _state.value = ToyUiState.Error(error.message ?: "Search failed")
                    }
            }
        }
    }
}