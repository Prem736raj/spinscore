package com.spinbottle.truthdare.games.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.premiumDataStore: DataStore<Preferences> by preferencesDataStore(name = "premium_settings")

class PremiumPreferences(private val context: Context) {
    companion object {
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
    }

    val isPremium: Flow<Boolean> = context.premiumDataStore.data.map { prefs ->
        prefs[IS_PREMIUM] ?: false
    }

    suspend fun setPremium(isPremium: Boolean) {
        context.premiumDataStore.edit { prefs ->
            prefs[IS_PREMIUM] = isPremium
        }
    }
}
