package com.spinbottle.truthdare.games.data

import android.content.Context
import android.content.SharedPreferences

/**
 * Persistent local gameplay preferences.
 */
object SettingsHolder {
    private const val PREFS_NAME = "game_settings"
    private const val KEY_SOUND = "sound_enabled"
    private const val KEY_HAPTIC = "haptic_enabled"
    private const val KEY_SPIN_SPEED = "spin_speed"
    private const val KEY_DEFAULT_DIFFICULTY = "default_difficulty"

    private var prefs: SharedPreferences? = null
    private var loading = false

    var soundEnabled: Boolean = true
        set(value) {
            field = value
            persist(KEY_SOUND, value)
        }

    var hapticEnabled: Boolean = true
        set(value) {
            field = value
            persist(KEY_HAPTIC, value)
        }

    /**
     * 0 = slow, 0.5 = normal, 1 = fast.
     */
    var spinSpeed: Float = 0.5f
        set(value) {
            field = value.coerceIn(0f, 1f)
            persist(KEY_SPIN_SPEED, field)
        }

    /**
     * Only non-restricted defaults are persisted. Extreme remains an explicit,
     * PIN-gated choice in the difficulty screen.
     */
    var defaultDifficulty: String = Difficulty.MEDIUM.displayName
        set(value) {
            val normalized = when (value) {
                Difficulty.EASY.displayName,
                Difficulty.MEDIUM.displayName,
                Difficulty.HARD.displayName -> value
                else -> Difficulty.MEDIUM.displayName
            }
            field = normalized
            persist(KEY_DEFAULT_DIFFICULTY, normalized)
        }

    fun init(context: Context) {
        prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        loading = true
        val storage = prefs
        soundEnabled = storage?.getBoolean(KEY_SOUND, true) ?: true
        hapticEnabled = storage?.getBoolean(KEY_HAPTIC, true) ?: true
        spinSpeed = storage?.getFloat(KEY_SPIN_SPEED, 0.5f) ?: 0.5f
        defaultDifficulty = storage?.getString(
            KEY_DEFAULT_DIFFICULTY,
            Difficulty.MEDIUM.displayName
        ) ?: Difficulty.MEDIUM.displayName
        loading = false
    }

    fun spinDurationMillis(): Int {
        // Keep the animation deliberate even at the fastest setting.
        val slowMs = 5_000
        val fastMs = 2_500
        return (slowMs - (slowMs - fastMs) * spinSpeed).toInt()
    }

    private fun persist(key: String, value: Boolean) {
        if (!loading) prefs?.edit()?.putBoolean(key, value)?.apply()
    }

    private fun persist(key: String, value: Float) {
        if (!loading) prefs?.edit()?.putFloat(key, value)?.apply()
    }

    private fun persist(key: String, value: String) {
        if (!loading) prefs?.edit()?.putString(key, value)?.apply()
    }
}
