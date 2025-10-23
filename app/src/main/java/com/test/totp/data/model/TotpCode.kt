package com.test.totp.data.model

/**
 * Data class representing a generated TOTP code
 * @param code The generated 6-digit code
 * @param remainingSeconds Seconds until the code expires
 * @param progress Progress percentage (0.0 to 1.0)
 */
data class TotpCode(
    val code: String,
    val remainingSeconds: Int,
    val progress: Float
)
