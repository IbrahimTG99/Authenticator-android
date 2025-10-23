package com.test.totp.domain.service

import androidx.core.net.toUri

/**
 * Service for parsing QR code data and extracting TOTP account information
 * Supports otpauth:// URL format
 */
class QrCodeService {

    /**
     * Parse QR code data and extract TOTP account information
     * @param qrData The QR code data (should be otpauth:// URL)
     * @return Parsed TOTP account data or null if invalid
     */
    fun parseQrCode(qrData: String): QrCodeResult? {
        return try {
            val uri = qrData.toUri()

            if (uri.scheme != "otpauth" || uri.host != "totp") {
                return null
            }

            val path = uri.path?.removePrefix("/") ?: return null
            val label = path.split(":").let { parts ->
                if (parts.size == 2) {
                    Pair(parts[0], parts[1])
                } else {
                    Pair("", parts[0])
                }
            }

            val secret = uri.getQueryParameter("secret") ?: return null
            val issuer = uri.getQueryParameter("issuer") ?: label.first
            val algorithm = uri.getQueryParameter("algorithm") ?: "SHA1"
            val digits = uri.getQueryParameter("digits")?.toIntOrNull() ?: 6
            val period = uri.getQueryParameter("period")?.toIntOrNull() ?: 30

            QrCodeResult(
                name = label.second,
                issuer = issuer,
                secret = secret,
                algorithm = algorithm,
                digits = digits,
                period = period
            )
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Generate otpauth:// URL for manual entry
     * @param name Account name
     * @param issuer Service provider
     * @param secret Secret key
     * @param algorithm Hash algorithm
     * @param digits Number of digits
     * @param period Time period
     * @return Generated otpauth:// URL
     */
    fun generateQrUrl(
        name: String,
        issuer: String,
        secret: String,
        algorithm: String = "SHA1",
        digits: Int = 6,
        period: Int = 30
    ): String {
        val label = if (issuer.isNotEmpty()) "$issuer:$name" else name
        return "otpauth://totp/$label?secret=$secret&issuer=$issuer&algorithm=$algorithm&digits=$digits&period=$period"
    }
}

/**
 * Data class representing parsed QR code data
 */
data class QrCodeResult(
    val name: String,
    val issuer: String,
    val secret: String,
    val algorithm: String,
    val digits: Int,
    val period: Int
)
