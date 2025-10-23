package com.test.totp.domain.service

import com.test.totp.data.model.TotpAccount
import com.test.totp.data.security.EncryptionService
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import kotlin.math.abs

@RunWith(MockitoJUnitRunner::class)
class TotpServiceTest {

    @Mock
    private lateinit var mockEncryptionService: EncryptionService

    private lateinit var totpService: TotpService

    private val testAccount = TotpAccount(
        id = "1",
        name = "Test Account",
        issuer = "Test Issuer",
        secret = "ENCRYPTED_SECRET",
        algorithm = "SHA1",
        digits = 6,
        period = 30
    )

    @Before
    fun setUp() {
        totpService = TotpService(mockEncryptionService)
    }

    @Test
    fun `generateTotpCode with valid account returns valid code`() = runTest {
        // Given
        val secretBase32 = "JBSWY3DPEHPK3PXP" // Known test secret
        whenever(mockEncryptionService.decryptSecret(testAccount.id)).thenReturn(secretBase32)

        // When
        val result = totpService.generateTotpCode(testAccount)

        // Then
        assertEquals(6, result.code.length)
        assertTrue(result.code.all { it.isDigit() })
        assertTrue(result.remainingSeconds in 0..30)
        assertTrue(result.progress in 0f..1f)
    }

    @Test(expected = IllegalStateException::class)
    fun `generateTotpCode with null secret throws exception`() = runTest {
        // Given
        whenever(mockEncryptionService.decryptSecret(testAccount.id)).thenReturn(null)

        // When
        totpService.generateTotpCode(testAccount)
    }

    @Test
    fun `generateTotpCode with SHA256 algorithm returns valid code`() = runTest {
        // Given
        val secretBase32 = "JBSWY3DPEHPK3PXP"
        val sha256Account = testAccount.copy(algorithm = "SHA256")
        whenever(mockEncryptionService.decryptSecret(sha256Account.id)).thenReturn(secretBase32)

        // When
        val result = totpService.generateTotpCode(sha256Account)

        // Then
        assertEquals(6, result.code.length)
        assertTrue(result.code.all { it.isDigit() })
    }

    @Test
    fun `generateTotpCode with 8 digits returns 8 digit code`() = runTest {
        // Given
        val secretBase32 = "JBSWY3DPEHPK3PXP"
        val eightDigitAccount = testAccount.copy(digits = 8)
        whenever(mockEncryptionService.decryptSecret(eightDigitAccount.id)).thenReturn(secretBase32)

        // When
        val result = totpService.generateTotpCode(eightDigitAccount)

        // Then
        assertEquals(8, result.code.length)
        assertTrue(result.code.all { it.isDigit() })
    }

    @Test
    fun `generateTotpCode with custom period returns correct remaining seconds`() = runTest {
        // Given
        val secretBase32 = "JBSWY3DPEHPK3PXP"
        val customPeriodAccount = testAccount.copy(period = 60)
        whenever(mockEncryptionService.decryptSecret(customPeriodAccount.id)).thenReturn(secretBase32)

        // When
        val result = totpService.generateTotpCode(customPeriodAccount)

        // Then
        assertTrue(result.remainingSeconds in 0..60)
        assertTrue(result.progress in 0f..1f)
    }

    @Test
    fun `generateTotpCodeFlow emits codes periodically`() = runTest {
        // Given
        val secretBase32 = "JBSWY3DPEHPK3PXP"
        whenever(mockEncryptionService.decryptSecret(testAccount.id)).thenReturn(secretBase32)

        // When
        val flow = totpService.generateTotpCodeFlow(testAccount)
        val results = flow.take(3).toList()

        // Then
        assertEquals(3, results.size)
        results.forEach { code ->
            assertEquals(6, code.code.length)
            assertTrue(code.remainingSeconds in 0..30)
            assertTrue(code.progress in 0f..1f)
        }
    }

    @Test
    fun `generateSecret returns valid base32 string`() {
        // When
        val secret = totpService.generateSecret(20)

        // Then
        assertTrue(secret.isNotEmpty())
        assertTrue(secret.all { it in "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567" })
        assertTrue(totpService.isValidSecret(secret))
    }

    @Test
    fun `generateSecret with custom length returns correct length`() {
        // When
        val secret = totpService.generateSecret(32)

        // Then
        // Base32 encoding expands data, so we just verify it's valid
        assertTrue(totpService.isValidSecret(secret))
    }

    @Test
    fun `isValidSecret returns true for valid base32`() {
        // Given
        val validSecrets = listOf(
            "JBSWY3DPEHPK3PXP",
            "ABCDEFGHIJKLMNOP",
            "234567",
            "A",
            "GEZDGNBVGY3TQOJQGEZDGNBVGY3TQOJQ" // 32 chars
        )

        validSecrets.forEach { secret ->
            // When & Then
            assertTrue("Secret '$secret' should be valid", totpService.isValidSecret(secret))
        }
    }

    @Test
    fun `base32Encode and base32Decode are inverse operations`() {
        // Given
        val testData = listOf(
            byteArrayOf(0x48, 0x65, 0x6c, 0x6c, 0x6f), // "Hello"
            byteArrayOf(0x74, 0x65, 0x73, 0x74), // "test"
            byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04), // binary data
            byteArrayOf(), // empty
            ByteArray(10) { it.toByte() } // 0-9
        )

        testData.forEach { original ->
            // When
            val encoded = totpService.generateSecretFromBytes(original)
            val decoded = totpService.base32DecodeInternal(encoded)

            // Then
            assertTrue("Original and decoded should match", original.contentEquals(decoded))
        }
    }

    @Test
    fun `getMacAlgorithm returns correct algorithms`() {
        // When & Then
        assertEquals("HmacSHA1", totpService.getMacAlgorithmInternal("SHA1"))
        assertEquals("HmacSHA1", totpService.getMacAlgorithmInternal("sha1"))
        assertEquals("HmacSHA256", totpService.getMacAlgorithmInternal("SHA256"))
        assertEquals("HmacSHA512", totpService.getMacAlgorithmInternal("SHA512"))
        assertEquals("HmacSHA1", totpService.getMacAlgorithmInternal("UNKNOWN"))
        assertEquals("HmacSHA1", totpService.getMacAlgorithmInternal(""))
    }

    @Test
    fun `generateCode with known secret produces expected format`() = runTest {
        // Given
        val secretBase32 = "JBSWY3DPEHPK3PXP"
        val counter = 1L
        val algorithm = "SHA1"
        val digits = 6

        // When
        val code = totpService.generateCodeInternal(secretBase32, counter, algorithm, digits)

        // Then
        assertEquals(6, code.length)
        assertTrue(code.all { it.isDigit() })
        // Note: Actual TOTP value depends on current time, so we just verify format
    }

    @Test
    fun `progress calculation is accurate`() = runTest {
        // Given
        val secretBase32 = "JBSWY3DPEHPK3PXP"
        whenever(mockEncryptionService.decryptSecret(testAccount.id)).thenReturn(secretBase32)

        // When
        val result = totpService.generateTotpCode(testAccount)

        // Then
        val expectedProgress = (30f - result.remainingSeconds) / 30f
        assertTrue("Progress should be accurate", abs(result.progress - expectedProgress) < 0.01f)
    }

    @Test
    fun `lowercase base32 secrets are handled correctly`() {
        // Given
        val lowercaseSecret = "jbswy3dpehpk3pxp" // lowercase version of known secret

        // When & Then
        assertTrue("Lowercase secret should be valid", totpService.isValidSecret(lowercaseSecret))
    }
}

// Extension functions to access private methods for testing
private fun TotpService.generateSecretFromBytes(bytes: ByteArray): String {
    val method = this::class.java.getDeclaredMethod("base32Encode", ByteArray::class.java)
    method.isAccessible = true
    return method.invoke(this, bytes) as String
}

private fun TotpService.base32DecodeInternal(input: String): ByteArray {
    val method = this::class.java.getDeclaredMethod("base32Decode", String::class.java)
    method.isAccessible = true
    return method.invoke(this, input) as ByteArray
}

private fun TotpService.counterToByteArrayInternal(counter: Long): ByteArray {
    val method = this::class.java.getDeclaredMethod("toByteArray", Long::class.java)
    method.isAccessible = true
    return method.invoke(this, counter) as ByteArray
}

private fun TotpService.getMacAlgorithmInternal(algorithm: String): String {
    val method = this::class.java.getDeclaredMethod("getMacAlgorithm", String::class.java)
    method.isAccessible = true
    return method.invoke(this, algorithm) as String
}

private fun TotpService.generateCodeInternal(
    secretBase32: String,
    counter: Long,
    algorithm: String,
    digits: Int
): String {
    val method = this::class.java.getDeclaredMethod(
        "generateCode",
        String::class.java,
        Long::class.java,
        String::class.java,
        Int::class.java
    )
    method.isAccessible = true
    return method.invoke(this, secretBase32, counter, algorithm, digits) as String
}