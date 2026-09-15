package com.spinbottle.truthdare.games.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.pinDataStore: DataStore<Preferences> by preferencesDataStore(name = "pin_settings")

/**
 * Manages PIN storage and verification for age-restricted content
 */
class PinManager(private val context: Context) {
    
    companion object {
        private val PIN_KEY = stringPreferencesKey("adult_pin")
        private val LOCKOUT_TIME_KEY = longPreferencesKey("lockout_until")
        private val FAILED_ATTEMPTS_KEY = intPreferencesKey("failed_attempts")
        private val AGE_VERIFIED_KEY = booleanPreferencesKey("age_verified")
        
        const val MAX_ATTEMPTS = 3
        const val LOCKOUT_DURATION_MS = 5 * 60 * 1000L // 5 minutes
    }
    
    /**
     * Check if PIN has been set
     */
    val isPinSet: Flow<Boolean> = context.pinDataStore.data.map { prefs ->
        prefs[PIN_KEY]?.isNotEmpty() == true
    }
    
    /**
     * Check if user has verified their age
     */
    val isAgeVerified: Flow<Boolean> = context.pinDataStore.data.map { prefs ->
        prefs[AGE_VERIFIED_KEY] == true
    }
    
    /**
     * Get current lockout end time (0 if not locked)
     */
    val lockoutEndTime: Flow<Long> = context.pinDataStore.data.map { prefs ->
        prefs[LOCKOUT_TIME_KEY] ?: 0L
    }
    
    /**
     * Get failed attempts count
     */
    val failedAttempts: Flow<Int> = context.pinDataStore.data.map { prefs ->
        prefs[FAILED_ATTEMPTS_KEY] ?: 0
    }
    
    /**
     * Set age verified status
     */
    suspend fun setAgeVerified(verified: Boolean) {
        context.pinDataStore.edit { prefs ->
            prefs[AGE_VERIFIED_KEY] = verified
        }
    }
    
    /**
     * Set the PIN
     */
    suspend fun setPin(pin: String) {
        context.pinDataStore.edit { prefs ->
            prefs[PIN_KEY] = pin
            prefs[FAILED_ATTEMPTS_KEY] = 0
            prefs[LOCKOUT_TIME_KEY] = 0L
        }
    }
    
    /**
     * Verify PIN and return result
     */
    suspend fun verifyPin(pin: String): PinResult {
        val prefs = context.pinDataStore.data.first()
        val storedPin = prefs[PIN_KEY] ?: return PinResult.NO_PIN_SET
        val lockoutTime = prefs[LOCKOUT_TIME_KEY] ?: 0L
        val currentTime = System.currentTimeMillis()
        
        // Check if locked out
        if (lockoutTime > currentTime) {
            val remainingMs = lockoutTime - currentTime
            return PinResult.LOCKED_OUT(remainingMs)
        }
        
        // Verify PIN
        if (pin == storedPin) {
            // Reset failed attempts on success
            context.pinDataStore.edit { editPrefs ->
                editPrefs[FAILED_ATTEMPTS_KEY] = 0
                editPrefs[LOCKOUT_TIME_KEY] = 0L
            }
            return PinResult.SUCCESS
        } else {
            // Increment failed attempts
            var newFailedAttempts = (prefs[FAILED_ATTEMPTS_KEY] ?: 0) + 1
            
            context.pinDataStore.edit { editPrefs ->
                editPrefs[FAILED_ATTEMPTS_KEY] = newFailedAttempts
                
                // Lock out after max attempts
                if (newFailedAttempts >= MAX_ATTEMPTS) {
                    editPrefs[LOCKOUT_TIME_KEY] = currentTime + LOCKOUT_DURATION_MS
                    editPrefs[FAILED_ATTEMPTS_KEY] = 0
                }
            }
            
            return if (newFailedAttempts >= MAX_ATTEMPTS) {
                PinResult.LOCKED_OUT(LOCKOUT_DURATION_MS)
            } else {
                PinResult.WRONG_PIN(MAX_ATTEMPTS - newFailedAttempts)
            }
        }
    }
    
    /**
     * Clear PIN (for reset)
     */
    suspend fun clearPin() {
        context.pinDataStore.edit { prefs ->
            prefs.remove(PIN_KEY)
            prefs.remove(FAILED_ATTEMPTS_KEY)
            prefs.remove(LOCKOUT_TIME_KEY)
            prefs.remove(AGE_VERIFIED_KEY)
        }
    }
    
    /**
     * Check if currently locked out
     */
    suspend fun isLockedOut(): Boolean {
        val prefs = context.pinDataStore.data.first()
        val lockoutTime = prefs[LOCKOUT_TIME_KEY] ?: 0L
        return lockoutTime > System.currentTimeMillis()
    }
}

sealed class PinResult {
    object SUCCESS : PinResult()
    object NO_PIN_SET : PinResult()
    data class WRONG_PIN(val attemptsRemaining: Int) : PinResult()
    data class LOCKED_OUT(val remainingMs: Long) : PinResult()
}
