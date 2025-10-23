package com.test.totp.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Service for encrypting and decrypting sensitive data
 * Uses Android's EncryptedSharedPreferences for secure storage
 */
class EncryptionService(
    context: Context
) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "totp_encrypted_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Encrypt and store a secret key
     * @param key The key identifier
     * @param secret The secret to encrypt
     */
    fun encryptSecret(key: String, secret: String) {
        encryptedPrefs.edit {
            putString(key, secret)
        }
    }

    /**
     * Decrypt and retrieve a secret key
     * @param key The key identifier
     * @return The decrypted secret or null if not found
     */
    fun decryptSecret(key: String): String? {
        return encryptedPrefs.getString(key, null)
    }

    /**
     * Remove a secret key
     * @param key The key identifier
     */
    fun removeSecret(key: String) {
        encryptedPrefs.edit {
            remove(key)
        }
    }

    /**
     * Check if a secret key exists
     * @param key The key identifier
     * @return True if the key exists
     */
    fun hasSecret(key: String): Boolean {
        return encryptedPrefs.contains(key)
    }

    /**
     * Clear all encrypted data
     */
    fun clearAllSecrets() {
        encryptedPrefs.edit { clear() }
    }
}
