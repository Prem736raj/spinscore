package com.spinbottle.truthdare.games.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * A favorited prompt
 */
data class FavoritePrompt(
    val text: String,
    val type: PromptItemType,
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Manager for favorite prompts with persistence
 */
object FavoritesManager {
    private const val PREFS_NAME = "favorites_prefs"
    private const val KEY_FAVORITES = "favorite_prompts"
    
    private val gson = Gson()
    private var prefs: SharedPreferences? = null
    private val favorites = mutableListOf<FavoritePrompt>()
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadFavorites()
    }
    
    private fun loadFavorites() {
        val json = prefs?.getString(KEY_FAVORITES, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<FavoritePrompt>>() {}.type
                val loaded: List<FavoritePrompt> = gson.fromJson(json, type)
                favorites.clear()
                favorites.addAll(loaded)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun saveFavorites() {
        val json = gson.toJson(favorites)
        prefs?.edit()?.putString(KEY_FAVORITES, json)?.apply()
    }
    
    fun isFavorite(text: String): Boolean {
        return favorites.any { it.text == text }
    }
    
    fun addFavorite(text: String, type: PromptItemType) {
        if (!isFavorite(text)) {
            favorites.add(FavoritePrompt(text = text, type = type))
            saveFavorites()
        }
    }
    
    fun removeFavorite(text: String) {
        favorites.removeAll { it.text == text }
        saveFavorites()
    }
    
    fun toggleFavorite(text: String, type: PromptItemType): Boolean {
        return if (isFavorite(text)) {
            removeFavorite(text)
            false
        } else {
            addFavorite(text, type)
            true
        }
    }
    
    fun getAllFavorites(): List<FavoritePrompt> = favorites.toList()
        .sortedByDescending { it.addedAt }
    
    fun getFavoriteTruths(): List<FavoritePrompt> = 
        favorites.filter { it.type == PromptItemType.TRUTH }
    
    fun getFavoriteDares(): List<FavoritePrompt> = 
        favorites.filter { it.type == PromptItemType.DARE }
    
    fun getRandomFavoriteTruth(): FavoritePrompt? = getFavoriteTruths().randomOrNull()
    
    fun getRandomFavoriteDare(): FavoritePrompt? = getFavoriteDares().randomOrNull()
    
    fun getFavoriteCount(): Int = favorites.size
}

/**
 * Manager for tracking which prompts have been seen
 */
object PromptHistoryManager {
    private const val PREFS_NAME = "prompt_history_prefs"
    private const val KEY_SEEN_PROMPTS = "seen_prompts"
    private const val KEY_AVOID_RECENT = "avoid_recently_played"
    
    private var prefs: SharedPreferences? = null
    private val seenPrompts = mutableSetOf<String>()
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadHistory()
    }
    
    private fun loadHistory() {
        val json = prefs?.getString(KEY_SEEN_PROMPTS, null)
        if (json != null) {
            try {
                val gson = Gson()
                val type = object : TypeToken<Set<String>>() {}.type
                val loaded: Set<String> = gson.fromJson(json, type)
                seenPrompts.clear()
                seenPrompts.addAll(loaded)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun saveHistory() {
        val gson = Gson()
        val json = gson.toJson(seenPrompts)
        prefs?.edit()?.putString(KEY_SEEN_PROMPTS, json)?.apply()
    }
    
    fun markAsSeen(promptText: String) {
        seenPrompts.add(promptText)
        saveHistory()
    }
    
    fun hasBeenSeen(promptText: String): Boolean = seenPrompts.contains(promptText)
    
    fun getSeenCount(): Int = seenPrompts.size
    
    fun resetHistory() {
        seenPrompts.clear()
        saveHistory()
    }
    
    var avoidRecentlyPlayed: Boolean
        get() = prefs?.getBoolean(KEY_AVOID_RECENT, false) ?: false
        set(value) {
            prefs?.edit()?.putBoolean(KEY_AVOID_RECENT, value)?.apply()
        }
}
