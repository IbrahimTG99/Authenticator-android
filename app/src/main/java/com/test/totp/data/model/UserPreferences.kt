package com.test.totp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Data model for user preferences
 * @param id Primary key (always "preferences")
 * @param isDarkModeEnabled Whether dark mode is enabled
 * @param autoRefreshInterval Refresh interval in seconds (default: 30)
 * @param showSeconds Whether to show remaining seconds
 * @param hapticFeedback Whether haptic feedback is enabled
 */
@Entity(tableName = "user_preferences")
data class UserPreferences(
    @PrimaryKey
    val id: String = "preferences",
    val isDarkModeEnabled: Boolean = false,
    val autoRefreshInterval: Int = 30,
    val showSeconds: Boolean = true,
    val hapticFeedback: Boolean = true
)
