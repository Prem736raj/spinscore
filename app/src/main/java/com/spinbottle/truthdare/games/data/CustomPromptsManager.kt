package com.spinbottle.truthdare.games.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

/**
 * A custom prompt created by the user
 */
data class CustomPrompt(
    val id: String = UUID.randomUUID().toString(),
    val type: PromptItemType,
    val category: PromptCategory,
    val difficulty: Difficulty,
    val text: String,
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Manager for custom prompts with SharedPreferences persistence
 */
object CustomPromptsManager {
    private const val PREFS_NAME = "custom_prompts_prefs"
    private const val KEY_CUSTOM_PROMPTS = "custom_prompts"
    
    private val gson = Gson()
    private var prefs: SharedPreferences? = null
    private val customPrompts = mutableListOf<CustomPrompt>()
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadPrompts()
    }
    
    private fun loadPrompts() {
        val json = prefs?.getString(KEY_CUSTOM_PROMPTS, null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<CustomPrompt>>() {}.type
                val loaded: List<CustomPrompt> = gson.fromJson(json, type)
                customPrompts.clear()
                customPrompts.addAll(loaded)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun savePrompts() {
        val json = gson.toJson(customPrompts)
        prefs?.edit()?.putString(KEY_CUSTOM_PROMPTS, json)?.apply()
    }
    
    fun getAllPrompts(): List<CustomPrompt> = customPrompts.toList()
    
    fun getEnabledPrompts(): List<CustomPrompt> = customPrompts.filter { it.isEnabled }
    
    fun addPrompt(prompt: CustomPrompt) {
        customPrompts.add(prompt)
        savePrompts()
    }
    
    fun updatePrompt(prompt: CustomPrompt) {
        val index = customPrompts.indexOfFirst { it.id == prompt.id }
        if (index != -1) {
            customPrompts[index] = prompt
            savePrompts()
        }
    }
    
    fun deletePrompt(id: String) {
        customPrompts.removeAll { it.id == id }
        savePrompts()
    }
    
    fun togglePrompt(id: String) {
        val index = customPrompts.indexOfFirst { it.id == id }
        if (index != -1) {
            val prompt = customPrompts[index]
            customPrompts[index] = prompt.copy(isEnabled = !prompt.isEnabled)
            savePrompts()
        }
    }
    
    fun getPromptById(id: String): CustomPrompt? = customPrompts.find { it.id == id }
    
    /**
     * Get a random custom truth matching category and difficulty
     */
    fun getRandomCustomTruth(category: PromptCategory?, difficulty: Difficulty?): CustomPrompt? {
        val eligible = customPrompts.filter { prompt ->
            prompt.isEnabled &&
            prompt.type == PromptItemType.TRUTH &&
            (category == null || prompt.category == category) &&
            (difficulty == null || prompt.difficulty == difficulty || difficulty == Difficulty.MIXED)
        }
        return eligible.randomOrNull()
    }
    
    /**
     * Get a random custom dare matching category and difficulty
     */
    fun getRandomCustomDare(category: PromptCategory?, difficulty: Difficulty?): CustomPrompt? {
        val eligible = customPrompts.filter { prompt ->
            prompt.isEnabled &&
            prompt.type == PromptItemType.DARE &&
            (category == null || prompt.category == category) &&
            (difficulty == null || prompt.difficulty == difficulty || difficulty == Difficulty.MIXED)
        }
        return eligible.randomOrNull()
    }
}
