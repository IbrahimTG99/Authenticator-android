package com.test.totp.domain.service

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission

/**
 * Service for handling clipboard operations and haptic feedback
 */
class ClipboardService(
    context: Context
) {
    
    private val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    
    /**
     * Copy text to clipboard with haptic feedback
     * @param text The text to copy
     * @param hapticFeedback Whether to provide haptic feedback
     */
    @RequiresPermission(Manifest.permission.VIBRATE)
    fun copyToClipboard(text: String, hapticFeedback: Boolean = true) {
        val clipData = ClipData.newPlainText("TOTP Code", text)
        clipboardManager.setPrimaryClip(clipData)
        
        if (hapticFeedback) {
            provideHapticFeedback()
        }
    }
    
    /**
     * Provide haptic feedback when copying
     */
    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun provideHapticFeedback() {
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                50, // Duration in milliseconds
                VibrationEffect.DEFAULT_AMPLITUDE
            )
        )
    }
    
    /**
     * Check if clipboard has text
     * @return True if clipboard contains text
     */
    fun hasClipboardText(): Boolean {
        return clipboardManager.hasPrimaryClip() && 
               clipboardManager.primaryClip?.getItemAt(0)?.text != null
    }
    
    /**
     * Get text from clipboard
     * @return The clipboard text or null if not available
     */
    fun getClipboardText(): String? {
        return if (hasClipboardText()) {
            clipboardManager.primaryClip?.getItemAt(0)?.text?.toString()
        } else {
            null
        }
    }
}
