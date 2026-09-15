package com.spinbottle.truthdare.games.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.spinbottle.truthdare.games.data.SettingsHolder

/**
 * Manages sound effects for the game using system sounds
 */
class SoundManager(private val context: Context) {
    
    private var volume = 1.0f
    
    // Use SoundPool for better performance
    private val soundPool: SoundPool by lazy {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()
    }
    
    private fun isEnabled(): Boolean = SettingsHolder.soundEnabled
    
    /**
     * Play a simple click/tap sound using system sounds
     */
    fun playTap() {
        if (!isEnabled()) return
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
            audioManager.playSoundEffect(android.media.AudioManager.FX_KEY_CLICK, volume)
        } catch (e: Exception) {
            // Ignore sound errors
        }
    }
    
    /**
     * Play spin start sound
     */
    fun playSpinStart() {
        if (!isEnabled()) return
        playSystemSound(android.media.AudioManager.FX_KEYPRESS_STANDARD)
    }
    
    /**
     * Play spin complete sound
     */
    fun playSpinComplete() {
        if (!isEnabled()) return
        playSystemSound(android.media.AudioManager.FX_FOCUS_NAVIGATION_UP)
    }
    
    /**
     * Play truth reveal sound
     */
    fun playTruthReveal() {
        if (!isEnabled()) return
        playSystemSound(android.media.AudioManager.FX_FOCUS_NAVIGATION_LEFT)
    }
    
    /**
     * Play dare reveal sound
     */
    fun playDareReveal() {
        if (!isEnabled()) return
        playSystemSound(android.media.AudioManager.FX_FOCUS_NAVIGATION_RIGHT)
    }
    
    /**
     * Play success/done sound
     */
    fun playSuccess() {
        if (!isEnabled()) return
        playSystemSound(android.media.AudioManager.FX_KEYPRESS_RETURN)
    }
    
    /**
     * Play skip sound
     */
    fun playSkip() {
        if (!isEnabled()) return
        playSystemSound(android.media.AudioManager.FX_KEYPRESS_DELETE)
    }
    
    /**
     * Play celebration sound for game completion
     */
    fun playCelebration() {
        if (!isEnabled()) return
        playSystemSound(android.media.AudioManager.FX_KEYPRESS_RETURN)
    }
    
    /**
     * Play player added sound
     */
    fun playPlayerAdded() {
        if (!isEnabled()) return
        playSystemSound(android.media.AudioManager.FX_KEYPRESS_SPACEBAR)
    }
    
    private fun playSystemSound(soundEffect: Int) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
            audioManager.playSoundEffect(soundEffect, volume)
        } catch (e: Exception) {
            // Ignore sound errors silently
        }
    }
    
    fun setVolume(volumeLevel: Float) {
        volume = volumeLevel.coerceIn(0f, 1f)
    }
    
    fun release() {
        try {
            soundPool.release()
        } catch (e: Exception) {
            // Ignore
        }
    }
}

/**
 * Remember sound manager in composable
 */
@Composable
fun rememberSoundManager(): SoundManager {
    val context = LocalContext.current
    return remember { SoundManager(context) }
}
