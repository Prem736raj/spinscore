package com.spinbottle.truthdare.games.data

/**
 * Settings holder for app preferences
 */
object SettingsHolder {
    var soundEnabled: Boolean = true
    var hapticEnabled: Boolean = true
    var spinSpeed: Float = 0.5f  // 0.0 = slow, 1.0 = fast
    var defaultDifficulty: String = "Medium"  // Easy, Medium, Hard, Spicy
    
    fun toggleSound(): Boolean {
        soundEnabled = !soundEnabled
        return soundEnabled
    }
    
    fun toggleHaptic(): Boolean {
        hapticEnabled = !hapticEnabled
        return hapticEnabled
    }
}
