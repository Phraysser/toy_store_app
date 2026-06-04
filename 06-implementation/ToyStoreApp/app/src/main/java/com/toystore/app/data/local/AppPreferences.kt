package com.toystore.app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("toy_store_prefs", Context.MODE_PRIVATE)

    private companion object {
        private const val KEY_DARK_THEME = "is_dark_theme"
        private const val DEFAULT_DARK_THEME = false
    }


    fun isDarkThemeEnabled(): Boolean {
        return prefs.getBoolean(KEY_DARK_THEME, DEFAULT_DARK_THEME)
    }


    fun setDarkThemeEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DARK_THEME, enabled).apply()
    }
}