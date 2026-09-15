package com.spinbottle.truthdare.games.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import com.spinbottle.truthdare.games.ui.theme.*

/**
 * Themed prompt packs for game customization
 */
enum class PromptPack(
    val displayName: String,
    val emoji: String,
    val description: String,
    val color: Color,
    val difficultyRange: String,
    val promptCount: Int,
    val requiresPin: Boolean = false,
    val isKidsSafe: Boolean = false,
    val categories: List<PromptCategory>
) {
    PARTY_STARTER(
        displayName = "Party Starter",
        emoji = "🎉",
        description = "General party fun for everyone!",
        color = AccentPurple,
        difficultyRange = "Easy - Hard",
        promptCount = 200,
        categories = listOf(PromptCategory.FRIENDS, PromptCategory.PARTY)
    ),
    DATE_NIGHT(
        displayName = "Date Night",
        emoji = "💕",
        description = "Romantic prompts for couples",
        color = AccentPink,
        difficultyRange = "Easy - Medium",
        promptCount = 150,
        categories = listOf(PromptCategory.COUPLES)
    ),
    SPICY_NIGHT(
        displayName = "Spicy Night",
        emoji = "🌶️",
        description = "Adults only - intense and daring!",
        color = DareOrange,
        difficultyRange = "Hard - Extreme",
        promptCount = 180,
        requiresPin = true,
        categories = listOf(PromptCategory.COUPLES)
    ),
    FAMILY_GAME_NIGHT(
        displayName = "Family Game Night",
        emoji = "👨‍👩‍👧‍👦",
        description = "Safe fun for all ages",
        color = AccentGreen,
        difficultyRange = "Easy",
        promptCount = 120,
        isKidsSafe = true,
        categories = listOf(PromptCategory.KIDS, PromptCategory.FAMILY)
    ),
    BACK_TO_SCHOOL(
        displayName = "Back to School",
        emoji = "🎓",
        description = "Perfect for students and campus parties",
        color = AccentTeal,
        difficultyRange = "Easy - Hard",
        promptCount = 140,
        isKidsSafe = true,
        categories = listOf(PromptCategory.STUDENTS)
    ),
    EXTREME_CHALLENGES(
        displayName = "Extreme Challenges",
        emoji = "🏆",
        description = "Only for the fearless! Difficult dares only",
        color = DifficultyExtreme,
        difficultyRange = "Extreme",
        promptCount = 80,
        categories = listOf(PromptCategory.FRIENDS, PromptCategory.PARTY)
    )
}

/**
 * Manager for prompt pack selection and persistence
 */
object PromptPackManager {
    private const val PREFS_NAME = "prompt_pack_prefs"
    private const val KEY_ENABLED_PACKS = "enabled_packs"
    
    private var prefs: SharedPreferences? = null
    private val enabledPacks = mutableSetOf<PromptPack>()
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        loadEnabledPacks()
    }
    
    private fun loadEnabledPacks() {
        val savedNames = prefs?.getStringSet(KEY_ENABLED_PACKS, null)
        enabledPacks.clear()
        
        if (savedNames != null) {
            savedNames.forEach { name ->
                try {
                    enabledPacks.add(PromptPack.valueOf(name))
                } catch (e: Exception) {}
            }
        } else {
            // Default: enable Party Starter and Family Game Night
            enabledPacks.add(PromptPack.PARTY_STARTER)
            enabledPacks.add(PromptPack.FAMILY_GAME_NIGHT)
        }
    }
    
    private fun saveEnabledPacks() {
        val names = enabledPacks.map { it.name }.toSet()
        prefs?.edit()?.putStringSet(KEY_ENABLED_PACKS, names)?.apply()
    }
    
    fun isPackEnabled(pack: PromptPack): Boolean {
        return enabledPacks.contains(pack)
    }
    
    fun enablePack(pack: PromptPack) {
        enabledPacks.add(pack)
        saveEnabledPacks()
    }
    
    fun disablePack(pack: PromptPack) {
        enabledPacks.remove(pack)
        saveEnabledPacks()
    }
    
    fun togglePack(pack: PromptPack): Boolean {
        return if (isPackEnabled(pack)) {
            disablePack(pack)
            false
        } else {
            enablePack(pack)
            true
        }
    }
    
    fun getEnabledPacks(): Set<PromptPack> = enabledPacks.toSet()
    
    fun getEnabledCategories(): Set<PromptCategory> {
        return enabledPacks.flatMap { it.categories }.toSet()
    }
    
    fun getTotalPromptCount(): Int {
        return enabledPacks.sumOf { it.promptCount }
    }
    
    fun getEnabledPackCount(): Int = enabledPacks.size
    
    fun hasAdultPackEnabled(): Boolean {
        return enabledPacks.any { it.requiresPin }
    }
}
