package com.test.totp.domain.service

import com.test.totp.data.model.TotpAccount
import com.test.totp.data.security.EncryptionService
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class TotpServiceTest {
    
    @Mock
    private lateinit var encryptionService: EncryptionService
    
    private lateinit var totpService: TotpService
    
    @Before
    fun setUp() {
        totpService = TotpService(encryptionService)
    }
    
    @Test
    fun `generateSecret should return valid base64 string`() {
        val secret = totpService.generateSecret()
        
        // Should be valid base64
        assert(Base64.getDecoder().decode(secret) != null)
        assert(secret.isNotEmpty())
    }
    
    @Test
    fun `isValidSecret should return true for valid base64`() {
        val validSecret = Base64.getEncoder().encodeToString("test".toByteArray())
        
        assert(totpService.isValidSecret(validSecret))
    }
    
    @Test
    fun `isValidSecret should return false for invalid base64`() {
        val invalidSecret = "not-valid-base64!"
        
        assert(!totpService.isValidSecret(invalidSecret))
    }
    
    @Test
    fun `generateTotpCode should generate valid code`() {
        // Given
        val secret = Base64.getEncoder().encodeToString("test-secret".toByteArray())
        val account = TotpAccount(
            id = "test-id",
            name = "Test Account",
            secret = secret,
            algorithm = "SHA1",
            digits = 6,
            period = 30
        )
        
        `when`(encryptionService.decryptSecret(account.id)).thenReturn(secret)
        
        // When
        val result = totpService.generateTotpCode(account)
        
        // Then
        assert(result.code.length == 6)
        assert(result.code.all { it.isDigit() })
        assert(result.remainingSeconds in 0..30)
        assert(result.progress in 0.0..1.0)
    }
}
