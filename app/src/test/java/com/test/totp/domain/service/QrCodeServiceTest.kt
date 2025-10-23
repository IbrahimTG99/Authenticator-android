package com.test.totp.domain.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class QrCodeServiceTest {

    private lateinit var qrCodeService: QrCodeService

    @Before
    fun setUp() {
        qrCodeService = QrCodeService()
    }

    // parseQrCode Success Tests

    @Test
    fun `parseQrCode with valid otpauth URL returns correct result`() {
        // Given
        val qrData = "otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example&algorithm=SHA1&digits=6&period=30"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertEquals(QrCodeResult(
            name = "alice@google.com",
            issuer = "Example",
            secret = "JBSWY3DPEHPK3PXP",
            algorithm = "SHA1",
            digits = 6,
            period = 30
        ), result)
    }

    @Test
    fun `parseQrCode with minimal valid URL uses defaults`() {
        // Given
        val qrData = "otpauth://totp/Example:user@site.com?secret=JBSWY3DPEHPK3PXP"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertEquals(QrCodeResult(
            name = "user@site.com",
            issuer = "Example", // From label
            secret = "JBSWY3DPEHPK3PXP",
            algorithm = "SHA1", // Default
            digits = 6, // Default
            period = 30 // Default
        ), result)
    }

    @Test
    fun `parseQrCode with issuer parameter overrides label issuer`() {
        // Given
        val qrData = "otpauth://totp/Example:user@site.com?secret=JBSWY3DPEHPK3PXP&issuer=ActualIssuer"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertEquals("ActualIssuer", result?.issuer)
        assertEquals("user@site.com", result?.name)
    }

    @Test
    fun `parseQrCode with label without issuer uses empty issuer in label`() {
        // Given
        val qrData = "otpauth://totp/user@site.com?secret=JBSWY3DPEHPK3PXP&issuer=Google"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertEquals("Google", result?.issuer) // From issuer parameter
        assertEquals("user@site.com", result?.name)
    }

    @Test
    fun `parseQrCode with custom algorithm digits and period`() {
        // Given
        val qrData = "otpauth://totp/Service:user@example.com?secret=JBSWY3DPEHPK3PXP&issuer=Service&algorithm=SHA256&digits=8&period=60"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertEquals(QrCodeResult(
            name = "user@example.com",
            issuer = "Service",
            secret = "JBSWY3DPEHPK3PXP",
            algorithm = "SHA256",
            digits = 8,
            period = 60
        ), result)
    }

    @Test
    fun `parseQrCode with URL encoded characters`() {
        // Given
        val qrData = "otpauth://totp/My%20Company:user%40company.com?secret=JBSWY3DPEHPK3PXP&issuer=My%20Company"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertEquals("user@company.com", result?.name)
        assertEquals("My Company", result?.issuer)
    }

    // parseQrCode Failure Tests

    @Test
    fun `parseQrCode with wrong scheme returns null`() {
        // Given
        val qrData = "https://totp/Example:user@google.com?secret=JBSWY3DPEHPK3PXP"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertNull(result)
    }

    @Test
    fun `parseQrCode with wrong host returns null`() {
        // Given
        val qrData = "otpauth://hotp/Example:user@google.com?secret=JBSWY3DPEHPK3PXP"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertNull(result)
    }

    @Test
    fun `parseQrCode with missing secret returns null`() {
        // Given
        val qrData = "otpauth://totp/Example:user@google.com?issuer=Example"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertNull(result)
    }

    @Test
    fun `parseQrCode with malformed URL returns null`() {
        // Given
        val qrData = "not a valid url at all"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertNull(result)
    }

    @Test
    fun `parseQrCode with invalid digits uses default`() {
        // Given
        val qrData = "otpauth://totp/Example:user@google.com?secret=JBSWY3DPEHPK3PXP&digits=invalid"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertEquals(6, result?.digits) // Default value
    }

    @Test
    fun `parseQrCode with invalid period uses default`() {
        // Given
        val qrData = "otpauth://totp/Example:user@google.com?secret=JBSWY3DPEHPK3PXP&period=invalid"

        // When
        val result = qrCodeService.parseQrCode(qrData)

        // Then
        assertEquals(30, result?.period) // Default value
    }

    // generateQrUrl Tests

    @Test
    fun `generateQrUrl with all parameters returns correct URL`() {
        // Given
        val name = "alice@google.com"
        val issuer = "Example"
        val secret = "JBSWY3DPEHPK3PXP"
        val algorithm = "SHA1"
        val digits = 6
        val period = 30

        // When
        val result = qrCodeService.generateQrUrl(name, issuer, secret, algorithm, digits, period)

        // Then
        val expected = "otpauth://totp/Example:alice@google.com?secret=JBSWY3DPEHPK3PXP&issuer=Example&algorithm=SHA1&digits=6&period=30"
        assertEquals(expected, result)
    }

    @Test
    fun `generateQrUrl with default parameters returns correct URL`() {
        // Given
        val name = "user@site.com"
        val issuer = "Service"
        val secret = "JBSWY3DPEHPK3PXP"

        // When
        val result = qrCodeService.generateQrUrl(name, issuer, secret)

        // Then
        val expected = "otpauth://totp/Service:user@site.com?secret=JBSWY3DPEHPK3PXP&issuer=Service&algorithm=SHA1&digits=6&period=30"
        assertEquals(expected, result)
    }

    @Test
    fun `generateQrUrl with empty issuer uses name only in label`() {
        // Given
        val name = "user@site.com"
        val issuer = ""
        val secret = "JBSWY3DPEHPK3PXP"

        // When
        val result = qrCodeService.generateQrUrl(name, issuer, secret)

        // Then
        val expected = "otpauth://totp/user@site.com?secret=JBSWY3DPEHPK3PXP&issuer=&algorithm=SHA1&digits=6&period=30"
        assertEquals(expected, result)
    }

    @Test
    fun `generateQrUrl with custom algorithm digits and period`() {
        // Given
        val name = "user@example.com"
        val issuer = "Service"
        val secret = "JBSWY3DPEHPK3PXP"
        val algorithm = "SHA256"
        val digits = 8
        val period = 60

        // When
        val result = qrCodeService.generateQrUrl(name, issuer, secret, algorithm, digits, period)

        // Then
        val expected = "otpauth://totp/Service:user@example.com?secret=JBSWY3DPEHPK3PXP&issuer=Service&algorithm=SHA256&digits=8&period=60"
        assertEquals(expected, result)
    }

    @Test
    fun `generateQrUrl with special characters in name and issuer`() {
        // Given
        val name = "user@company.com"
        val issuer = "My Company"
        val secret = "JBSWY3DPEHPK3PXP"

        // When
        val result = qrCodeService.generateQrUrl(name, issuer, secret)

        // Then
        val expected = "otpauth://totp/My Company:user@company.com?secret=JBSWY3DPEHPK3PXP&issuer=My Company&algorithm=SHA1&digits=6&period=30"
        assertEquals(expected, result)
    }

    // Integration Tests - Round Trip

    @Test
    fun `parseQrCode with generated URL returns equivalent result`() {
        // Given
        val name = "test@example.com"
        val issuer = "TestService"
        val secret = "JBSWY3DPEHPK3PXP"
        val algorithm = "SHA256"
        val digits = 8
        val period = 45

        // When
        val generatedUrl = qrCodeService.generateQrUrl(name, issuer, secret, algorithm, digits, period)
        val parsedResult = qrCodeService.parseQrCode(generatedUrl)

        // Then
        assertEquals(QrCodeResult(
            name = name,
            issuer = issuer,
            secret = secret,
            algorithm = algorithm,
            digits = digits,
            period = period
        ), parsedResult)
    }

    @Test
    fun `parseQrCode with minimal generated URL returns equivalent result`() {
        // Given
        val name = "user@site.com"
        val issuer = "Service"
        val secret = "JBSWY3DPEHPK3PXP"

        // When
        val generatedUrl = qrCodeService.generateQrUrl(name, issuer, secret)
        val parsedResult = qrCodeService.parseQrCode(generatedUrl)

        // Then
        assertEquals(QrCodeResult(
            name = name,
            issuer = issuer,
            secret = secret,
            algorithm = "SHA1",
            digits = 6,
            period = 30
        ), parsedResult)
    }

    @Test
    fun `parseQrCode with empty issuer generated URL returns equivalent result`() {
        // Given
        val name = "user@site.com"
        val issuer = ""
        val secret = "JBSWY3DPEHPK3PXP"

        // When
        val generatedUrl = qrCodeService.generateQrUrl(name, issuer, secret)
        val parsedResult = qrCodeService.parseQrCode(generatedUrl)

        // Then
        assertEquals("", parsedResult?.issuer)
        assertEquals(name, parsedResult?.name)
        assertEquals(secret, parsedResult?.secret)
    }
}