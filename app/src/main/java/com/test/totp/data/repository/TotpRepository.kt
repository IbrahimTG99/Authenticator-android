package com.test.totp.data.repository

import com.test.totp.data.dao.TotpAccountDao
import com.test.totp.data.dao.UserPreferencesDao
import com.test.totp.data.model.TotpAccount
import com.test.totp.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing TOTP accounts and user preferences
 * Follows the Repository pattern for clean separation of concerns
 */
class TotpRepository(
    private val totpAccountDao: TotpAccountDao,
    private val userPreferencesDao: UserPreferencesDao
) {

    // TOTP Account operations
    fun getAllAccounts(): Flow<List<TotpAccount>> = totpAccountDao.getAllAccounts()

    suspend fun getAccountById(id: String): TotpAccount? = totpAccountDao.getAccountById(id)

    suspend fun insertAccount(account: TotpAccount) = totpAccountDao.insertAccount(account)

    suspend fun updateAccount(account: TotpAccount) = totpAccountDao.updateAccount(account)

    suspend fun deleteAccount(account: TotpAccount) = totpAccountDao.deleteAccount(account)

    suspend fun deleteAccountById(id: String) = totpAccountDao.deleteAccountById(id)

    suspend fun getAccountCount(): Int = totpAccountDao.getAccountCount()

    // User Preferences operations
    fun getPreferences(): Flow<UserPreferences?> = userPreferencesDao.getPreferences()

    suspend fun insertPreferences(preferences: UserPreferences) =
        userPreferencesDao.insertPreferences(preferences)

    suspend fun updatePreferences(preferences: UserPreferences) =
        userPreferencesDao.updatePreferences(preferences)

    suspend fun updateDarkMode(isDarkMode: Boolean) =
        userPreferencesDao.updateDarkMode(isDarkMode)

    suspend fun updateRefreshInterval(interval: Int) =
        userPreferencesDao.updateRefreshInterval(interval)

    suspend fun updateShowSeconds(showSeconds: Boolean) =
        userPreferencesDao.updateShowSeconds(showSeconds)

    suspend fun updateHapticFeedback(hapticFeedback: Boolean) =
        userPreferencesDao.updateHapticFeedback(hapticFeedback)
}
