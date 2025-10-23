package com.test.totp.domain.service

import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertEquals
import kotlin.test.assertNull

@RunWith(JUnit4::class)
class QrCodeServiceTest {
    
    private val qrCodeService = QrCodeService()
    
    @Test
    fun `parseQrCode should parse valid otpauth URL`() {
        // Given
        val qrData = "otpauth://totp/Test%20Account?secret=JBSWY3DPEHPK3PXP&issuer=Test%20Service&algorithm=SHA1&digits=6&period=30"
        
        // When
        val result = qrCodeService.parseQrCode(qrData)
        
        // Then
        assertNotNull(result)
        assertEquals("Test Account", result!!.name)
        assertEquals("Test Service", result.issuer)
        assertEquals("JBSWY3DPEHPK3PXP", result.secret)
        assertEquals("SHA1", result.algorithm)
        assertEquals(6, result.digits)
        assertEquals(30, result.period)
    }
    
    @Test
    fun `parseQrCode should parse URL with issuer in label`() {
        // Given
        val qrData = "otpauth://totp/Test%20Service:Test%20Account?secret=JBSWY3DPEHPK3PXP"
        
        // When
        val result = qrCodeService.parseQrCode(qrData)
        
        // Then
        assertNotNull(result)
        assertEquals("Test Account", result!!.name)
        assertEquals("Test Service", result.issuer)
        assertEquals("JBSWY3DPEHPK3PXP", result.secret)
    }
    
    @Test
    fun `parseQrCode should return null for invalid URL`() {
        // Given
        val invalidQrData = "https://example.com"
        
        // When
        val result = qrCodeService.parseQrCode(invalidQrData)
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `parseQrCode should return null for non-totp URL`() {
        // Given
        val qrData = "otpauth://hotp/Test%20Account?secret=JBSWY3DPEHPK3PXP"
        
        // When
        val result = qrCodeService.parseQrCode(qrData)
        
        // Then
        assertNull(result)
    }
    
    @Test
    fun `generateQrUrl should create valid otpauth URL`() {
        // Given
        val name = "Test Account"
        val issuer = "Test Service"
        val secret = "JBSWY3DPEHPK3PXP"
        val algorithm = "SHA1"
        val digits = 6
        val period = 30
        
        // When
        val result = qrCodeService.generateQrUrl(name, issuer, secret, algorithm, digits, period)
        
        // Then
        assert(result.startsWith("otpauth://totp/"))
        assert(result.contains("secret=$secret"))
        assert(result.contains("issuer=$issuer"))
        assert(result.contains("algorithm=$algorithm"))
        assert(result.contains("digits=$digits"))
        assert(result.contains("period=$period"))
    }
}
