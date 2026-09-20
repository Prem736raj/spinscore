package com.spinbottle.truthdare.games.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import com.spinbottle.truthdare.games.ui.theme.*

/**
 * Bottle design options
 */
enum class BottleDesign(
    val displayName: String,
    val emoji: String,
    val description: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val isLocked: Boolean = false,
    val unlockRequirement: String? = null
) {
    CLASSIC(
        displayName = "Classic Green",
        emoji = "🍾",
        description = "The original party bottle",
        primaryColor = Color(0xFF2E7D32),
        secondaryColor = Color(0xFF81C784)
    ),
    CHAMPAGNE(
        displayName = "Champagne",
        emoji = "🥂",
        description = "Celebration style",
        primaryColor = Color(0xFFFFD54F),
        secondaryColor = Color(0xFFFFF176)
    ),
    WINE(
        displayName = "Wine Bottle",
        emoji = "🍷",
        description = "Elegant and classy",
        primaryColor = Color(0xFF880E4F),
        secondaryColor = Color(0xFFAD1457)
    ),
    POTION(
        displayName = "Magic Potion",
        emoji = "🧪",
        description = "Mysterious and magical",
        primaryColor = Color(0xFF7B1FA2),
        secondaryColor = Color(0xFFBA68C8)
    ),
    CRYSTAL(
        displayName = "Crystal Diamond",
        emoji = "💎",
        description = "Sparkling and precious",
        primaryColor = Color(0xFF00ACC1),
        secondaryColor = Color(0xFF4DD0E1)
    ),
    NEON(
        displayName = "Neon Glow",
        emoji = "✨",
        description = "Bright and electric",
        primaryColor = Color(0xFFE91E63),
        secondaryColor = Color(0xFF00E676)
    ),
    GALAXY(
        displayName = "Galaxy",
        emoji = "🌌",
        description = "Cosmic and stellar",
        primaryColor = Color(0xFF1A237E),
        secondaryColor = Color(0xFF7C4DFF)
    ),
    FIRE(
        displayName = "Fire Bottle",
        emoji = "🔥",
        description = "Hot and intense",
        primaryColor = Color(0xFFFF5722),
        secondaryColor = Color(0xFFFF9800)
    ),
    ICE(
        displayName = "Ice Crystal",
        emoji = "❄️",
        description = "Cool and refreshing",
        primaryColor = Color(0xFF0288D1),
        secondaryColor = Color(0xFF81D4FA)
    ),
    GOLD(
        displayName = "Golden Trophy",
        emoji = "🏆",
        description = "Winner's bottle",
        primaryColor = Color(0xFFFFB300),
        secondaryColor = Color(0xFFFFE082),
        isLocked = true,
        unlockRequirement = "Play 10 games"
    ),
    RAINBOW(
        displayName = "Rainbow Pride",
        emoji = "🌈",
        description = "Colorful celebration",
        primaryColor = Color(0xFFE91E63),
        secondaryColor = Color(0xFF2196F3),
        isLocked = true,
        unlockRequirement = "Play 25 games"
    ),
    SKULL(
        displayName = "Skull Bottle",
        emoji = "💀",
        description = "Danger zone!",
        primaryColor = Color(0xFF424242),
        secondaryColor = Color(0xFF757575),
        isLocked = true,
        unlockRequirement = "Complete 50 dares"
    )
}

/**
 * Background theme options
 */
enum class BackgroundTheme(
    val displayName: String,
    val emoji: String,
    val description: String,
    val colors: List<Color>,
    val isLocked: Boolean = false,
    val unlockRequirement: String? = null
) {
    PARTY(
        displayName = "Party Gradient",
        emoji = "🎉",
        description = "Default party vibes",
        colors = listOf(DarkBackground, DarkBackgroundSecondary, DarkBackground)
    ),
    ROMANTIC(
        displayName = "Romantic Rose",
        emoji = "🌹",
        description = "For couples night",
        colors = listOf(Color(0xFF1A0A0D), Color(0xFF3D1A24), Color(0xFF1A0A0D))
    ),
    NEON_CLUB(
        displayName = "Neon Nightclub",
        emoji = "🕺",
        description = "Electric party vibes",
        colors = listOf(Color(0xFF0D0015), Color(0xFF1A0033), Color(0xFF0D0015))
    ),
    CAMPFIRE(
        displayName = "Cozy Campfire",
        emoji = "🏕️",
        description = "Warm outdoor nights",
        colors = listOf(Color(0xFF1A1005), Color(0xFF2D1A0A), Color(0xFF1A1005))
    ),
    SPACE(
        displayName = "Space Galaxy",
        emoji = "🚀",
        description = "Cosmic adventure",
        colors = listOf(Color(0xFF000510), Color(0xFF0A1929), Color(0xFF000510))
    ),
    DARK_MINIMAL(
        displayName = "Dark Minimal",
        emoji = "⬛",
        description = "Clean and simple",
        colors = listOf(Color(0xFF121212), Color(0xFF1E1E1E), Color(0xFF121212))
    ),
    OCEAN(
        displayName = "Deep Ocean",
        emoji = "🌊",
        description = "Calm blue depths",
        colors = listOf(Color(0xFF001F3F), Color(0xFF003366), Color(0xFF001F3F))
    ),
    FOREST(
        displayName = "Mystic Forest",
        emoji = "🌲",
        description = "Nature's embrace",
        colors = listOf(Color(0xFF0A1A0A), Color(0xFF1A2D1A), Color(0xFF0A1A0A))
    ),
    SUNSET(
        displayName = "Sunset Glow",
        emoji = "🌅",
        description = "Golden hour vibes",
        colors = listOf(Color(0xFF1A0A05), Color(0xFF331A0A), Color(0xFF1A0A05)),
        isLocked = true,
        unlockRequirement = "Play 15 games"
    ),
    AURORA(
        displayName = "Aurora Borealis",
        emoji = "🌌",
        description = "Northern lights magic",
        colors = listOf(Color(0xFF0A1A1A), Color(0xFF0A2D2D), Color(0xFF0A1A1A)),
        isLocked = true,
        unlockRequirement = "Answer 100 truths"
    )
}

/**
 * Manager for theme preferences
 */
object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_BOTTLE_DESIGN = "bottle_design"
    private const val KEY_BACKGROUND_THEME = "background_theme"
    private const val KEY_GAMES_PLAYED = "total_games_played"
    private const val KEY_DARES_COMPLETED = "total_dares_completed"
    private const val KEY_TRUTHS_ANSWERED = "total_truths_answered"
    
    private var prefs: SharedPreferences? = null
    
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    var selectedBottle: BottleDesign
        get() {
            val name = prefs?.getString(KEY_BOTTLE_DESIGN, BottleDesign.CLASSIC.name)
            val candidate = try {
                BottleDesign.valueOf(name ?: BottleDesign.CLASSIC.name)
            } catch (e: Exception) {
                BottleDesign.CLASSIC
            }
            return candidate.takeIf(::isBottleUnlocked) ?: BottleDesign.CLASSIC
        }
        set(value) {
            if (isBottleUnlocked(value)) {
                prefs?.edit()?.putString(KEY_BOTTLE_DESIGN, value.name)?.apply()
            }
        }
    
    var selectedBackground: BackgroundTheme
        get() {
            val name = prefs?.getString(KEY_BACKGROUND_THEME, BackgroundTheme.PARTY.name)
            val candidate = try {
                BackgroundTheme.valueOf(name ?: BackgroundTheme.PARTY.name)
            } catch (e: Exception) {
                BackgroundTheme.PARTY
            }
            return candidate.takeIf(::isBackgroundUnlocked) ?: BackgroundTheme.PARTY
        }
        set(value) {
            if (isBackgroundUnlocked(value)) {
                prefs?.edit()?.putString(KEY_BACKGROUND_THEME, value.name)?.apply()
            }
        }
    
    // Track stats for unlocking themes
    var totalGamesPlayed: Int
        get() = prefs?.getInt(KEY_GAMES_PLAYED, 0) ?: 0
        set(value) { prefs?.edit()?.putInt(KEY_GAMES_PLAYED, value)?.apply() }
    
    var totalDaresCompleted: Int
        get() = prefs?.getInt(KEY_DARES_COMPLETED, 0) ?: 0
        set(value) { prefs?.edit()?.putInt(KEY_DARES_COMPLETED, value)?.apply() }
    
    var totalTruthsAnswered: Int
        get() = prefs?.getInt(KEY_TRUTHS_ANSWERED, 0) ?: 0
        set(value) { prefs?.edit()?.putInt(KEY_TRUTHS_ANSWERED, value)?.apply() }
    
    fun incrementGamesPlayed() {
        totalGamesPlayed++
    }
    
    fun addDaresCompleted(count: Int) {
        totalDaresCompleted += count
    }
    
    fun addTruthsAnswered(count: Int) {
        totalTruthsAnswered += count
    }
    
    fun isBottleUnlocked(bottle: BottleDesign): Boolean {
        if (!bottle.isLocked) return true
        return when (bottle) {
            BottleDesign.GOLD -> totalGamesPlayed >= 10
            BottleDesign.RAINBOW -> totalGamesPlayed >= 25
            BottleDesign.SKULL -> totalDaresCompleted >= 50
            else -> true
        }
    }
    
    fun isBackgroundUnlocked(background: BackgroundTheme): Boolean {
        if (!background.isLocked) return true
        return when (background) {
            BackgroundTheme.SUNSET -> totalGamesPlayed >= 15
            BackgroundTheme.AURORA -> totalTruthsAnswered >= 100
            else -> true
        }
    }
}
