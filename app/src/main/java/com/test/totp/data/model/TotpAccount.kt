package com.test.totp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Data model representing a TOTP account
 * @param id Unique identifier for the account
 * @param name Display name for the account
 * @param issuer Service provider (e.g., "Google", "GitHub")
 * @param secret Encrypted secret key for TOTP generation
 * @param algorithm Hash algorithm (default: SHA1)
 * @param digits Number of digits in the OTP (default: 6)
 * @param period Time period in seconds (default: 30)
 * @param createdAt Timestamp when account was created
 */
@Entity(tableName = "totp_accounts")
data class TotpAccount(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val issuer: String? = null,
    val secret: String, // This will be encrypted
    val algorithm: String = "SHA1", // SHA1, SHA256, SHA512
    val digits: Int = 6,
    val period: Int = 30,
    val createdAt: Long = System.currentTimeMillis()
)
