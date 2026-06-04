package com.toystore.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.toystore.app.data.local.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: AppPreferences  // 👈 Инжектим Preferences
) : ViewModel() {

    private val _isDarkTheme = MutableStateFlow(preferences.isDarkThemeEnabled())  // 👇 Загружаем при старте
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme

    fun setDarkTheme(enabled: Boolean) {
        _isDarkTheme.value = enabled
        preferences.setDarkThemeEnabled(enabled)  // 👇 Сохраняем в SharedPreferences
    }
}