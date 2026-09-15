package com.spinbottle.truthdare.games.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.spinbottle.truthdare.games.data.SettingsHolder

/**
 * Manages haptic feedback throughout the app
 */
class HapticManager(private val context: Context) {
    
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
    
    /**
     * Light haptic for button taps
     */
    fun lightTap() {
        if (!SettingsHolder.hapticEnabled) return
        vibrate(10, VibrationEffect.EFFECT_TICK)
    }
    
    /**
     * Medium haptic for selections made
     */
    fun mediumTap() {
        if (!SettingsHolder.hapticEnabled) return
        vibrate(20, VibrationEffect.EFFECT_CLICK)
    }
    
    /**
     * Heavy haptic for important events
     */
    fun heavyTap() {
        if (!SettingsHolder.hapticEnabled) return
        vibrate(30, VibrationEffect.EFFECT_HEAVY_CLICK)
    }
    
    /**
     * Success haptic pattern for victories
     */
    fun successPattern() {
        if (!SettingsHolder.hapticEnabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val pattern = longArrayOf(0, 50, 50, 50, 50, 100)
            val effect = VibrationEffect.createWaveform(pattern, -1)
            vibrator?.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(100)
        }
    }
    
    /**
     * Spin haptic - continuous subtle for bottle spin
     */
    fun spinStart() {
        if (!SettingsHolder.hapticEnabled) return
        vibrate(25, VibrationEffect.EFFECT_TICK)
    }
    
    /**
     * Spin complete haptic
     */
    fun spinComplete() {
        if (!SettingsHolder.hapticEnabled) return
        vibrate(40, VibrationEffect.EFFECT_HEAVY_CLICK)
    }
    
    private fun vibrate(durationMs: Long, effectId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator?.vibrate(VibrationEffect.createPredefined(effectId))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(durationMs)
        }
    }
}

/**
 * Remember haptic manager in composable
 */
@Composable
fun rememberHapticManager(): HapticManager {
    val context = LocalContext.current
    return remember { HapticManager(context) }
}
