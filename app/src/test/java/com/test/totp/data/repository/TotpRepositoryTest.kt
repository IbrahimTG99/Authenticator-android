package com.test.totp.data.repository

import com.test.totp.data.dao.TotpAccountDao
import com.test.totp.data.dao.UserPreferencesDao
import com.test.totp.data.model.TotpAccount
import com.test.totp.data.model.UserPreferences
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class TotpRepositoryTest {
    
    @Mock
    private lateinit var totpAccountDao: TotpAccountDao
    
    @Mock
    private lateinit var userPreferencesDao: UserPreferencesDao
    
    private lateinit var repository: TotpRepository
    
    @Before
    fun setUp() {
        repository = TotpRepository(totpAccountDao, userPreferencesDao)
    }
    
    @Test
    fun `getAllAccounts should return flow from dao`() = runTest {
        // Given
        val accounts = listOf(
            TotpAccount(name = "Test Account 1", secret = "secret1"),
            TotpAccount(name = "Test Account 2", secret = "secret2")
        )
        `when`(totpAccountDao.getAllAccounts()).thenReturn(flowOf(accounts))
        
        // When
        val result = repository.getAllAccounts()
        
        // Then
        verify(totpAccountDao).getAllAccounts()
    }
    
    @Test
    fun `insertAccount should call dao insert`() = runTest {
        // Given
        val account = TotpAccount(name = "Test Account", secret = "secret")
        
        // When
        repository.insertAccount(account)
        
        // Then
        verify(totpAccountDao).insertAccount(account)
    }
    
    @Test
    fun `deleteAccount should call dao delete`() = runTest {
        // Given
        val account = TotpAccount(name = "Test Account", secret = "secret")
        
        // When
        repository.deleteAccount(account)
        
        // Then
        verify(totpAccountDao).deleteAccount(account)
    }
    
    @Test
    fun `getPreferences should return flow from dao`() = runTest {
        // Given
        val preferences = UserPreferences()
        `when`(userPreferencesDao.getPreferences()).thenReturn(flowOf(preferences))
        
        // When
        val result = repository.getPreferences()
        
        // Then
        verify(userPreferencesDao).getPreferences()
    }
    
    @Test
    fun `updateDarkMode should call dao update`() = runTest {
        // Given
        val isDarkMode = true
        
        // When
        repository.updateDarkMode(isDarkMode)
        
        // Then
        verify(userPreferencesDao).updateDarkMode(isDarkMode)
    }
}
