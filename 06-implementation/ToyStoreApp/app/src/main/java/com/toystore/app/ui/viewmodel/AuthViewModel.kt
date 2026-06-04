package com.toystore.app.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toystore.app.data.repository.AuthRepository
import com.toystore.app.utils.AuthPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    object Registered : AuthState()
    data class Success(val token: String, val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val authPrefs = AuthPreferences(context)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    val isAdmin: Boolean
        get() = when (val state = _authState.value) {
            is AuthState.Success -> state.role == "ADMIN" || state.role == "ROLE_ADMIN"
            else -> false
        }

    val token: String?
        get() = when (val state = _authState.value) {
            is AuthState.Success -> state.token
            else -> authPrefs.getToken()
        }

    val username: String?
        get() = authPrefs.getUsername()

    init {
        checkSavedSession()
    }

    private fun checkSavedSession() {
        val savedToken = authPrefs.getToken()
        val savedRole = authPrefs.getRole()

        if (savedToken != null && savedRole != null) {
            _authState.value = AuthState.Success(savedToken, savedRole)
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.login(username, password)
                .onSuccess { response ->
                    authPrefs.saveAuthData(response.token, response.role, username)
                    _authState.value = AuthState.Success(response.token, response.role)
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Login failed")
                }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            authRepository.register(username, password)
                .onSuccess {
                    _authState.value = AuthState.Registered
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Registration failed")
                }
        }
    }

    fun logout() {
        authPrefs.clearAuthData()
        _authState.value = AuthState.Initial
    }
}