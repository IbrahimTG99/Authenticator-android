package com.test.totp.data.repository

import com.test.totp.data.dao.TotpAccountDao
import com.test.totp.data.dao.UserPreferencesDao
import com.test.totp.data.model.TotpAccount
import com.test.totp.data.model.UserPreferences
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class TotpRepositoryTest {

    @Mock
    private lateinit var mockTotpAccountDao: TotpAccountDao

    @Mock
    private lateinit var mockUserPreferencesDao: UserPreferencesDao

    private lateinit var totpRepository: TotpRepository

    private companion object {
        val testAccount = TotpAccount(
            id = "1",
            name = "Test Account",
            issuer = "Test Issuer",
            secret = "TEST_SECRET",
            algorithm = "SHA1",
            digits = 6,
            period = 30
        )

        val testPreferences = UserPreferences(
            id = "",
            isDarkModeEnabled = true,
            autoRefreshInterval = 30,
            showSeconds = true,
            hapticFeedback = false
        )
    }

    @Before
    fun setUp() {
        totpRepository = TotpRepository(mockTotpAccountDao, mockUserPreferencesDao)
    }

    // TOTP Account operations tests

    @Test
    fun `getAllAccounts returns flow from dao`() = runTest {
        // Given
        val expectedAccounts = listOf(testAccount)
        whenever(mockTotpAccountDao.getAllAccounts()).thenReturn(flowOf(expectedAccounts))

        // When
        val result = totpRepository.getAllAccounts().toList()

        // Then
        assertEquals(listOf(expectedAccounts), result)
    }

    @Test
    fun `getAccountById returns account from dao`() = runTest {
        // Given
        val accountId = "1"
        whenever(mockTotpAccountDao.getAccountById(accountId)).thenReturn(testAccount)

        // When
        val result = totpRepository.getAccountById(accountId)

        // Then
        assertEquals(testAccount, result)
        verify(mockTotpAccountDao).getAccountById(accountId)
    }

    @Test
    fun `getAccountById returns null when not found`() = runTest {
        // Given
        val accountId = "non-existent"
        whenever(mockTotpAccountDao.getAccountById(accountId)).thenReturn(null)

        // When
        val result = totpRepository.getAccountById(accountId)

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `insertAccount calls dao insert`() = runTest {
        // When
        totpRepository.insertAccount(testAccount)

        // Then
        verify(mockTotpAccountDao).insertAccount(testAccount)
    }

    @Test
    fun `updateAccount calls dao update`() = runTest {
        // When
        totpRepository.updateAccount(testAccount)

        // Then
        verify(mockTotpAccountDao).updateAccount(testAccount)
    }

    @Test
    fun `deleteAccount calls dao delete`() = runTest {
        // When
        totpRepository.deleteAccount(testAccount)

        // Then
        verify(mockTotpAccountDao).deleteAccount(testAccount)
    }

    @Test
    fun `deleteAccountById calls dao deleteAccountById`() = runTest {
        // Given
        val accountId = "1"

        // When
        totpRepository.deleteAccountById(accountId)

        // Then
        verify(mockTotpAccountDao).deleteAccountById(accountId)
    }

    @Test
    fun `getAccountCount returns count from dao`() = runTest {
        // Given
        val expectedCount = 5
        whenever(mockTotpAccountDao.getAccountCount()).thenReturn(expectedCount)

        // When
        val result = totpRepository.getAccountCount()

        // Then
        assertEquals(expectedCount, result)
    }

    // User Preferences operations tests

    @Test
    fun `getPreferences returns flow from dao`() = runTest {
        // Given
        whenever(mockUserPreferencesDao.getPreferences()).thenReturn(flowOf(testPreferences))

        // When
        val result = totpRepository.getPreferences().toList()

        // Then
        assertEquals(listOf(testPreferences), result)
    }

    @Test
    fun `getPreferences returns null flow from dao`() = runTest {
        // Given
        whenever(mockUserPreferencesDao.getPreferences()).thenReturn(flowOf(null))

        // When
        val result = totpRepository.getPreferences().toList()

        // Then
        assertEquals(listOf(null), result)
    }

    @Test
    fun `insertPreferences calls dao insert`() = runTest {
        // When
        totpRepository.insertPreferences(testPreferences)

        // Then
        verify(mockUserPreferencesDao).insertPreferences(testPreferences)
    }

    @Test
    fun `updatePreferences calls dao update`() = runTest {
        // When
        totpRepository.updatePreferences(testPreferences)

        // Then
        verify(mockUserPreferencesDao).updatePreferences(testPreferences)
    }

    @Test
    fun `updateDarkMode calls dao updateDarkMode`() = runTest {
        // Given
        val isDarkMode = true

        // When
        totpRepository.updateDarkMode(isDarkMode)

        // Then
        verify(mockUserPreferencesDao).updateDarkMode(isDarkMode)
    }

    @Test
    fun `updateRefreshInterval calls dao updateRefreshInterval`() = runTest {
        // Given
        val interval = 60

        // When
        totpRepository.updateRefreshInterval(interval)

        // Then
        verify(mockUserPreferencesDao).updateRefreshInterval(interval)
    }

    @Test
    fun `updateShowSeconds calls dao updateShowSeconds`() = runTest {
        // Given
        val showSeconds = true

        // When
        totpRepository.updateShowSeconds(showSeconds)

        // Then
        verify(mockUserPreferencesDao).updateShowSeconds(showSeconds)
    }

    @Test
    fun `updateHapticFeedback calls dao updateHapticFeedback`() = runTest {
        // Given
        val hapticFeedback = true

        // When
        totpRepository.updateHapticFeedback(hapticFeedback)

        // Then
        verify(mockUserPreferencesDao).updateHapticFeedback(hapticFeedback)
    }
}
