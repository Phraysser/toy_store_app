package com.toystore.app.utils

import android.content.Context
import android.content.SharedPreferences

class AuthPreferences(context: Context) {

    companion object {
        private const val PREF_NAME = "toy_store_auth"
        private const val KEY_TOKEN = "token"
        private const val KEY_ROLE = "role"
        private const val KEY_USERNAME = "username"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // Сохранение данных
    fun saveAuthData(token: String, role: String, username: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_ROLE, role)
            .putString(KEY_USERNAME, username)
            .apply()
    }

    // Получение токена
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    // Получение роли
    fun getRole(): String? = prefs.getString(KEY_ROLE, null)

    // Получение имени пользователя
    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)

    // Очистка (выход из аккаунта)
    fun clearAuthData() {
        prefs.edit().clear().apply()
    }

    // Проверка: авторизован ли пользователь
    fun isLoggedIn(): Boolean = getToken() != null
}