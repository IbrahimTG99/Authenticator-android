package com.test.totp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.totp.data.model.UserPreferences
import com.test.totp.data.repository.TotpRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for settings screen
 */
class SettingsViewModel constructor(
    private val repository: TotpRepository
) : ViewModel() {

    private val _preferences = MutableStateFlow<UserPreferences?>(null)
    val preferences: StateFlow<UserPreferences?> = _preferences.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        ensurePreferences()
    }

    private fun ensurePreferences() {
        viewModelScope.launch {
            repository.getPreferences()
                .catch { e -> _error.value = "Failed to load preferences: ${e.message}" }
                .collect { preferences ->
                    if (preferences == null) {
                        val defaults = UserPreferences()
                        try {
                            repository.insertPreferences(defaults)
                            _preferences.value = defaults
                        } catch (e: Exception) {
                            _error.value = "Failed to initialize preferences: ${e.message}"
                        }
                    } else {
                        _preferences.value = preferences
                    }
                }
        }
    }

    fun updateDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateDarkMode(isDarkMode)
                _preferences.value = _preferences.value?.copy(isDarkModeEnabled = isDarkMode)
            } catch (e: Exception) {
                _error.value = "Failed to update dark mode: ${e.message}"
            }
        }
    }

    fun updateRefreshInterval(interval: Int) {
        viewModelScope.launch {
            try {
                repository.updateRefreshInterval(interval)
                _preferences.value = _preferences.value?.copy(autoRefreshInterval = interval)
            } catch (e: Exception) {
                _error.value = "Failed to update refresh interval: ${e.message}"
            }
        }
    }

    fun updateShowSeconds(showSeconds: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateShowSeconds(showSeconds)
                _preferences.value = _preferences.value?.copy(showSeconds = showSeconds)
            } catch (e: Exception) {
                _error.value = "Failed to update show seconds: ${e.message}"
            }
        }
    }

    fun updateHapticFeedback(hapticFeedback: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateHapticFeedback(hapticFeedback)
                _preferences.value = _preferences.value?.copy(hapticFeedback = hapticFeedback)
            } catch (e: Exception) {
                _error.value = "Failed to update haptic feedback: ${e.message}"
            }
        }
    }

    fun clearError() { _error.value = null }
}
