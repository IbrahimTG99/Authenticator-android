package com.test.totp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.totp.data.model.TotpAccount
import com.test.totp.data.model.TotpCode
import com.test.totp.data.model.UserPreferences
import com.test.totp.data.repository.TotpRepository
import com.test.totp.data.security.EncryptionService
import com.test.totp.domain.service.TotpService
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the main screen displaying TOTP accounts
 */
class MainViewModel constructor(
    private val repository: TotpRepository,
    private val totpService: TotpService,
    private val encryptionService: EncryptionService
) : ViewModel() {

    private val _accounts = MutableStateFlow<List<TotpAccount>>(emptyList())
    val accounts: StateFlow<List<TotpAccount>> = _accounts.asStateFlow()

    private val _preferences = MutableStateFlow<UserPreferences?>(null)
    val preferences: StateFlow<UserPreferences?> = _preferences.asStateFlow()

    private val _totpCodes = MutableStateFlow<Map<String, TotpCode>>(emptyMap())
    val totpCodes: StateFlow<Map<String, TotpCode>> = _totpCodes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val generatorJobs = mutableMapOf<String, Job>()

    init {
        loadAccounts()
        loadPreferences()
    }

    private fun loadAccounts() {
        viewModelScope.launch {
            repository.getAllAccounts()
                .catch { e -> _error.value = "Failed to load accounts: ${e.message}" }
                .collect { list ->
                    _accounts.value = list
                    restartGenerators(list)
                }
        }
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            repository.getPreferences()
                .catch { e -> _error.value = "Failed to load preferences: ${e.message}" }
                .collect { preferences ->
                    _preferences.value = preferences ?: UserPreferences()
                }
        }
    }

    private fun restartGenerators(accounts: List<TotpAccount>) {
        // Cancel generators for removed accounts
        val currentIds = accounts.map { it.id }.toSet()
        val toCancel = generatorJobs.keys - currentIds
        toCancel.forEach { id -> generatorJobs.remove(id)?.cancel() }

        // Start generators for new accounts
        accounts.forEach { account ->
            if (!generatorJobs.containsKey(account.id)) {
                val job = viewModelScope.launch {
                    totpService.generateTotpCodeFlow(account)
                        .catch { e ->
                            _error.value = "Failed to generate TOTP for ${account.name}: ${e.message}"
                        }
                        .collect { code ->
                            _totpCodes.value = _totpCodes.value + (account.id to code)
                        }
                }
                generatorJobs[account.id] = job
            }
        }
    }

    fun deleteAccount(account: TotpAccount) {
        viewModelScope.launch {
            try {
                repository.deleteAccount(account)
                encryptionService.removeSecret(account.id)
                _totpCodes.value = _totpCodes.value - account.id
                generatorJobs.remove(account.id)?.cancel()
            } catch (e: Exception) {
                _error.value = "Failed to delete account: ${e.message}"
            }
        }
    }

    fun clearError() { _error.value = null }
}
