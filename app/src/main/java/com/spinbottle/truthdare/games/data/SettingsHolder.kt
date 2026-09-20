package com.spinbottle.truthdare.games.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SettingsHolder {
    private const val PREFS_NAME = "game_settings"
    private const val KEY_SOUND = "sound_enabled"
    private const val KEY_HAPTIC = "haptic_enabled"
    private const val KEY_SPIN_SPEED = "spin_speed"
    private const val KEY_DEFAULT_DIFFICULTY = "default_difficulty"

    private var prefs: SharedPreferences? = null
    private var loading = false

    private val _soundEnabledFlow = MutableStateFlow(true)
    val soundEnabledFlow: StateFlow<Boolean> = _soundEnabledFlow.asStateFlow()

    private val _hapticEnabledFlow = MutableStateFlow(true)
    val hapticEnabledFlow: StateFlow<Boolean> = _hapticEnabledFlow.asStateFlow()

    private val _spinSpeedFlow = MutableStateFlow(0.5f)
    val spinSpeedFlow: StateFlow<Float> = _spinSpeedFlow.asStateFlow()

    var soundEnabled: Boolean = true
        set(value) {
            field = value
            _soundEnabledFlow.value = value
            persist(KEY_SOUND, value)
        }

    var hapticEnabled: Boolean = true
        set(value) {
            field = value
            _hapticEnabledFlow.value = value
            persist(KEY_HAPTIC, value)
        }

    var spinSpeed: Float = 0.5f
        set(value) {
            field = value.coerceIn(0f, 1f)
            _spinSpeedFlow.value = field
            persist(KEY_SPIN_SPEED, field)
        }

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
        _soundEnabledFlow.value = soundEnabled
        
        hapticEnabled = storage?.getBoolean(KEY_HAPTIC, true) ?: true
        _hapticEnabledFlow.value = hapticEnabled
        
        spinSpeed = storage?.getFloat(KEY_SPIN_SPEED, 0.5f) ?: 0.5f
        _spinSpeedFlow.value = spinSpeed
        
        defaultDifficulty = storage?.getString(
            KEY_DEFAULT_DIFFICULTY,
            Difficulty.MEDIUM.displayName
        ) ?: Difficulty.MEDIUM.displayName
        loading = false
    }

    fun spinDurationMillis(): Int {
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
