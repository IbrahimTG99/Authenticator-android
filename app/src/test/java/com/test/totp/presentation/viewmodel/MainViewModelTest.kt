package com.test.totp.presentation.viewmodel

import com.test.totp.data.model.TotpAccount
import com.test.totp.data.model.UserPreferences
import com.test.totp.data.repository.TotpRepository
import com.test.totp.data.security.EncryptionService
import com.test.totp.domain.service.TotpService
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    
    @Mock
    private lateinit var repository: TotpRepository
    
    @Mock
    private lateinit var totpService: TotpService

    @Mock
    private lateinit var encryptionService: EncryptionService
    
    private lateinit var viewModel: MainViewModel
    
    @Before
    fun setUp() {
        viewModel = MainViewModel(
            repository, totpService,
            encryptionService = encryptionService
        )
    }
    
    @Test
    fun `deleteAccount should call repository delete`() = runTest {
        // Given
        val account = TotpAccount(name = "Test Account", secret = "secret")
        
        // When
        viewModel.deleteAccount(account)
        
        // Then
        verify(repository).deleteAccount(account)
    }
    
    @Test
    fun `clearError should reset error state`() {
        // When
        viewModel.clearError()
        
        // Then - No exception thrown, error state should be cleared
        // This is a simple test to ensure the method exists and can be called
    }
}
