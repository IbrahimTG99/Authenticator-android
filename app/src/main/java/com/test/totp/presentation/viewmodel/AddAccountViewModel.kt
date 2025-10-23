package com.test.totp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.totp.data.model.TotpAccount
import com.test.totp.data.repository.TotpRepository
import com.test.totp.data.security.EncryptionService
import com.test.totp.domain.service.QrCodeService
import com.test.totp.domain.service.TotpService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for adding new TOTP accounts
 */
class AddAccountViewModel constructor(
    private val repository: TotpRepository,
    private val encryptionService: EncryptionService,
    private val totpService: TotpService,
    private val qrCodeService: QrCodeService
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success.asStateFlow()

    fun addAccountFromQr(qrData: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val qrResult = qrCodeService.parseQrCode(qrData)
                    ?: throw IllegalArgumentException("Invalid QR code format")

                val sanitizedSecret = qrResult.secret.replace(" ", "").uppercase()
                if (!totpService.isValidSecret(sanitizedSecret)) {
                    throw IllegalArgumentException("Invalid secret key format")
                }

                val account = TotpAccount(
                    name = qrResult.name,
                    issuer = qrResult.issuer,
                    secret = "", // Encrypted separately
                    algorithm = qrResult.algorithm,
                    digits = qrResult.digits,
                    period = qrResult.period
                )

                encryptionService.encryptSecret(account.id, sanitizedSecret)
                repository.insertAccount(account)
                _success.value = true
            } catch (e: Exception) {
                _error.value = "Failed to add account: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addAccountManually(
        name: String,
        issuer: String,
        secret: String,
        algorithm: String = "SHA1",
        digits: Int = 6,
        period: Int = 30
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                if (name.isBlank()) throw IllegalArgumentException("Account name is required")
                if (secret.isBlank()) throw IllegalArgumentException("Secret key is required")

                val sanitizedSecret = secret.replace(" ", "").uppercase()
                if (!totpService.isValidSecret(sanitizedSecret)) {
                    throw IllegalArgumentException("Invalid secret key format")
                }

                val account = TotpAccount(
                    name = name.trim(),
                    issuer = issuer.trim().takeIf { it.isNotBlank() },
                    secret = "", // Encrypted separately
                    algorithm = algorithm,
                    digits = digits,
                    period = period
                )

                encryptionService.encryptSecret(account.id, sanitizedSecret)
                repository.insertAccount(account)
                _success.value = true
            } catch (e: Exception) {
                _error.value = "Failed to add account: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateSecret(): String = totpService.generateSecret()

    fun clearError() { _error.value = null }

    fun clearSuccess() { _success.value = false }
}
