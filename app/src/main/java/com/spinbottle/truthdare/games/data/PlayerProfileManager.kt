package com.spinbottle.truthdare.games.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Player profile with persistent stats
 */
data class PlayerProfile(
    val name: String,
    val avatar: String,
    var gamesPlayed: Int = 0,
    var truthsAnswered: Int = 0,
    var daresCompleted: Int = 0,
    var skips: Int = 0,
    var lastPlayedTimestamp: Long = System.currentTimeMillis(),
    var colorTheme: String? = null
) {
    val totalChallenges: Int get() = truthsAnswered + daresCompleted
    
    fun getCompletionRate(): Float {
        val total = totalChallenges + skips
        return if (total > 0) totalChallenges.toFloat() / total else 0f
    }
}

/**
 * Manager for persistent player profiles
 */
object PlayerProfileManager {
    private const val PREFS_NAME = "player_profiles_prefs"
    private const val KEY_PROFILES = "player_profiles"
    
    private val gson = Gson()
    private var prefs: SharedPreferences? = null
    private val profiles = mutableMapOf<String, PlayerProfile>()
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadProfiles()
    }
    
    private fun loadProfiles() {
        val json = prefs?.getString(KEY_PROFILES, null)
        if (json != null) {
            try {
                val type = object : TypeToken<Map<String, PlayerProfile>>() {}.type
                val loaded: Map<String, PlayerProfile> = gson.fromJson(json, type)
                profiles.clear()
                profiles.putAll(loaded)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun saveProfiles() {
        val json = gson.toJson(profiles)
        prefs?.edit()?.putString(KEY_PROFILES, json)?.apply()
    }
    
    /**
     * Check if a player with this name has played before
     */
    fun isReturningPlayer(name: String): Boolean {
        return profiles.containsKey(PlayerKeyNormalizer.normalizePlayerKey(name))
    }
    
    /**
     * Get profile for a player, or null if new player
     */
    fun getProfile(name: String): PlayerProfile? {
        return profiles[PlayerKeyNormalizer.normalizePlayerKey(name)]
    }
    
    /**
     * Get or create profile for a player
     */
    fun getOrCreateProfile(name: String, defaultAvatar: String): PlayerProfile {
        val key = PlayerKeyNormalizer.normalizePlayerKey(name)
        return profiles.getOrPut(key) {
            PlayerProfile(name = name.trim(), avatar = defaultAvatar)
        }.also {
            // Update the display name in case casing changed
            profiles[key] = it.copy(name = name.trim())
            saveProfiles()
        }
    }
    
    /**
     * Update player's avatar
     */
    fun updateAvatar(name: String, avatar: String) {
        val key = PlayerKeyNormalizer.normalizePlayerKey(name)
        profiles[key]?.let {
            profiles[key] = it.copy(avatar = avatar)
            saveProfiles()
        }
    }
    
    /**
     * Record a completed game for a player
     */
    fun recordGamePlayed(name: String, truthsAnswered: Int, daresCompleted: Int, skips: Int) {
        val key = PlayerKeyNormalizer.normalizePlayerKey(name)
        profiles[key]?.let { profile ->
            profiles[key] = profile.copy(
                gamesPlayed = profile.gamesPlayed + 1,
                truthsAnswered = profile.truthsAnswered + truthsAnswered,
                daresCompleted = profile.daresCompleted + daresCompleted,
                skips = profile.skips + skips,
                lastPlayedTimestamp = System.currentTimeMillis()
            )
            saveProfiles()
        }
    }
    
    /**
     * Get all saved profiles
     */
    fun getAllProfiles(): List<PlayerProfile> {
        return profiles.values.toList().sortedByDescending { it.lastPlayedTimestamp }
    }
    
    /**
     * Get recent players (for quick add)
     */
    fun getRecentPlayers(limit: Int = 10): List<PlayerProfile> {
        return getAllProfiles().take(limit)
    }

    fun clearProfiles() {
        profiles.clear()
        prefs?.edit()?.remove(KEY_PROFILES)?.apply()
    }
}

/**
 * Extended avatar options grouped by category
 */
object AvatarCategories {
    val faces = listOf("😀", "😎", "🥳", "😇", "🤩", "😂", "🥰", "😜", "🤗", "😏", "🤔", "😴")
    val animals = listOf("🐶", "🐱", "🦊", "🐻", "🐼", "🦁", "🐯", "🐸", "🐵", "🐰", "🦄", "🐥")
    val food = listOf("🍕", "🍔", "🌮", "🍣", "🍩", "🍪", "🧁", "🍦", "🍫", "🍿", "🥤", "🍓")
    val sports = listOf("⚽", "🏀", "🏈", "⚾", "🎾", "🏐", "🎱", "🏓", "🥊", "🎯", "🎮", "🎲")
    val nature = listOf("🌸", "🌻", "🌺", "🌴", "🌵", "🍀", "🌙", "⭐", "🌈", "❄️", "🔥", "💧")
    val objects = listOf("💎", "👑", "🎸", "🎨", "📷", "🎧", "🚀", "🛸", "✈️", "🏆", "💰", "🎁")
    
    val allCategories = mapOf(
        "Faces" to faces,
        "Animals" to animals,
        "Food" to food,
        "Sports" to sports,
        "Nature" to nature,
        "Objects" to objects
    )
    
    val allAvatars: List<String> = allCategories.values.flatten()
    
    fun getRandomAvatar(): String = allAvatars.random()
    
    fun getRandomExcluding(usedAvatars: List<String>): String {
        val available = allAvatars.filter { it !in usedAvatars }
        return if (available.isNotEmpty()) available.random() else allAvatars.random()
    }
}
