package com.test.totp.domain.service

import com.test.totp.data.model.TotpAccount
import com.test.totp.data.model.TotpCode
import com.test.totp.data.security.EncryptionService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

/**
 * Service for generating TOTP codes
 * Implements RFC 6238 TOTP algorithm with Base32 secrets (otpauth spec)
 */
class TotpService(
    private val encryptionService: EncryptionService
) {
    private val secureRandom = SecureRandom()

    fun generateTotpCode(account: TotpAccount): TotpCode {
        val secretBase32 = encryptionService.decryptSecret(account.id)
            ?: throw IllegalStateException("Secret not found for account: ${account.id}")

        val currentTime = System.currentTimeMillis() / 1000
        val timeStep = account.period
        val counter = currentTime / timeStep

        val code = generateCode(secretBase32, counter, account.algorithm, account.digits)
        val remainingSeconds = (timeStep - (currentTime % timeStep)).toInt()
        val progress = (timeStep - remainingSeconds).toFloat() / timeStep

        return TotpCode(
            code = code,
            remainingSeconds = remainingSeconds,
            progress = progress
        )
    }

    fun generateTotpCodeFlow(account: TotpAccount): Flow<TotpCode> = flow {
        while (true) {
            emit(generateTotpCode(account))
            delay(1000)
        }
    }

    private fun generateCode(
        secretBase32: String,
        counter: Long,
        algorithm: String,
        digits: Int
    ): String {
        val secretBytes = base32Decode(secretBase32)
        val counterBytes = counter.toByteArray()

        val mac = Mac.getInstance(getMacAlgorithm(algorithm))
        val secretKey = SecretKeySpec(secretBytes, mac.algorithm)
        mac.init(secretKey)

        val hash = mac.doFinal(counterBytes)
        val offset = hash[hash.size - 1].toInt() and 0x0f
        val binary = ((hash[offset].toInt() and 0x7f) shl 24) or
                ((hash[offset + 1].toInt() and 0xff) shl 16) or
                ((hash[offset + 2].toInt() and 0xff) shl 8) or
                (hash[offset + 3].toInt() and 0xff)

        val otp = binary % 10.0.pow(digits.toDouble()).toInt()
        return String.format("%0${digits}d", otp)
    }

    private fun getMacAlgorithm(algorithm: String): String = when (algorithm.uppercase()) {
        "SHA1" -> "HmacSHA1"
        "SHA256" -> "HmacSHA256"
        "SHA512" -> "HmacSHA512"
        else -> "HmacSHA1"
    }

    private fun Long.toByteArray(): ByteArray {
        val result = ByteArray(8)
        var value = this
        for (i in 7 downTo 0) {
            result[i] = (value and 0xff).toByte()
            value = value shr 8
        }
        return result
    }

    fun generateSecret(length: Int = 20): String {
        val bytes = ByteArray(length)
        secureRandom.nextBytes(bytes)
        return base32Encode(bytes)
    }

    fun isValidSecret(secret: String): Boolean = try {
        base32Decode(secret)
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    // RFC 4648 Base32 (uppercase, no padding)
    private val BASE32_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

    private fun base32Decode(input: String): ByteArray {
        val clean = input.uppercase().replace("=", "").replace(" ", "")
        if (clean.isEmpty()) return ByteArray(0)

        var buffer = 0
        var bitsLeft = 0
        val out = ArrayList<Byte>(clean.length * 5 / 8)
        for (ch in clean) {
            val index = BASE32_ALPHABET.indexOf(ch)
            if (index == -1) throw IllegalArgumentException("Invalid base32 char: $ch")
            buffer = (buffer shl 5) or index
            bitsLeft += 5
            if (bitsLeft >= 8) {
                bitsLeft -= 8
                out.add(((buffer shr bitsLeft) and 0xFF).toByte())
            }
        }
        return out.toByteArray()
    }

    private fun base32Encode(bytes: ByteArray): String {
        if (bytes.isEmpty()) return ""
        val sb = StringBuilder((bytes.size + 7) * 8 / 5)
        var buffer = 0
        var bitsLeft = 0
        for (b in bytes) {
            buffer = (buffer shl 8) or (b.toInt() and 0xFF)
            bitsLeft += 8
            while (bitsLeft >= 5) {
                bitsLeft -= 5
                val index = (buffer shr bitsLeft) and 0x1F
                sb.append(BASE32_ALPHABET[index])
            }
        }
        if (bitsLeft > 0) {
            val index = (buffer shl (5 - bitsLeft)) and 0x1F
            sb.append(BASE32_ALPHABET[index])
        }
        return sb.toString()
    }
}
