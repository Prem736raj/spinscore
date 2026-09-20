package com.spinbottle.truthdare.games.data

/**
 * Game state that persists across the game session
 */
data class GameState(
    val players: List<Player> = emptyList(),
    val currentSpinnerIndex: Int = 0,
    val selectedPlayerIndex: Int? = null,
    val round: Int = 1,
    val isSpinning: Boolean = false,
    val showPrompt: Boolean = false,
    val currentPromptType: PromptType? = null,
    val gameMode: GameMode = GameMode.CLASSIC,
    val difficulty: Difficulty = Difficulty.MEDIUM
)

enum class PromptType {
    TRUTH,
    DARE
}

/**
 * Sample prompts for the game
 */
object GamePrompts {
    val truths = mapOf(
        Difficulty.EASY to listOf(
            "What's your favorite movie?",
            "What's your dream vacation destination?",
            "What's your favorite food?",
            "What's the best gift you've ever received?",
            "What's one hobby you'd love to try?",
            "What song do you know all the words to?",
            "What's your favorite childhood memory?",
            "If you could have any superpower, what would it be?"
        ),
        Difficulty.MEDIUM to listOf(
            "What's your most embarrassing moment?",
            "Who in this room do you trust the most?",
            "What's a secret talent you have?",
            "What's the biggest lie you've told?",
            "What's your biggest fear?",
            "Have you ever had a crush on someone in this room?",
            "What's something you've never told anyone?",
            "What's your guilty pleasure?"
        ),
        Difficulty.HARD to listOf(
            "What's your biggest regret in life?",
            "Who was your first kiss?",
            "What's the worst thing you've done to a friend?",
            "What's your deepest secret?",
            "Have you ever cheated on anything?",
            "What's the meanest thing you've said about someone here?",
            "What's your most controversial opinion?",
            "What would you do if you were invisible for a day?"
        ),
        Difficulty.EXTREME to listOf(
            "What's the most scandalous thing you've done?",
            "Describe your most embarrassing romantic moment",
            "What secret would ruin your reputation?",
            "What's the worst thing someone could find on your phone?",
            "What's something you'd never want your parents to know?"
        ),
        Difficulty.MIXED to listOf(
            "What's something unexpected about you?",
            "What's a secret you're okay sharing now?",
            "What would you change about yourself?"
        )
    )
    
    val dares = mapOf(
        Difficulty.EASY to listOf(
            "Do your best dance move",
            "Sing the chorus of your favorite song",
            "Do 10 jumping jacks",
            "Make a funny face and hold it for 30 seconds",
            "Talk in an accent for the next round",
            "Give someone in the group a compliment",
            "Do your best celebrity impression",
            "Let someone draw on your hand"
        ),
        Difficulty.MEDIUM to listOf(
            "Choose a photo you are comfortable showing and tell the story behind it",
            "Make up a silly message to an imaginary crush and read it aloud",
            "Do an embarrassing TikTok dance",
            "Speak only in whispers for the next 3 rounds",
            "Pretend to record a dramatic social-media intro without posting it",
            "Sing happy birthday dramatically to the group",
            "Exchange an item of clothing with someone",
            "Let the group give you a new hairstyle"
        ),
        Difficulty.HARD to listOf(
            "Share a harmless message you choose, or make one up",
            "Take a silly selfie for yourself; keep or delete it as you prefer",
            "Do your best impression of someone here",
            "Guess your screen time; checking it is optional and private",
            "Name five people or characters you would invite to a dream party",
            "Let the group invent a fake status you do not have to post",
            "Act out an embarrassing moment from your life",
            "Tell someone in the room one thing you appreciate about them"
        ),
        Difficulty.EXTREME to listOf(
            "Read a harmless sentence from any screen you choose",
            "Describe a funny photo without showing it",
            "Let someone dictate a fictional message; do not send it",
            "Do a silly group-approved dare that avoids pain, privacy, spending, substances, and third parties",
            "Name a harmless topic you have been curious about lately"
        ),
        Difficulty.MIXED to listOf(
            "Do something silly for 30 seconds",
            "Describe or show one photo only if you are comfortable",
            "Do a harmless group-chosen dare that respects privacy and physical comfort"
        )
    )
    
    private fun <T> avoidSeen(items: List<T>, textOf: (T) -> String): List<T> {
        if (!PromptHistoryManager.avoidRecentlyPlayed) return items
        val unseen = items.filterNot { PromptHistoryManager.hasBeenSeen(textOf(it)) }
        // Once this exact pool is exhausted, allow reuse instead of looping forever.
        return unseen.ifEmpty { items }
    }

    private fun enabledCategories(): Set<PromptCategory> =
        PromptPackManager.getEnabledCategories().ifEmpty { setOf(PromptCategory.FRIENDS) }

    private fun selectDatabasePrompt(
        type: PromptItemType,
        difficulty: Difficulty
    ): PromptItem? {
        val categories = enabledCategories()
        val all = when (type) {
            PromptItemType.TRUTH -> PromptsDatabase.getTruths(difficulty = difficulty)
            PromptItemType.DARE -> PromptsDatabase.getDares(difficulty = difficulty)
        }.filter { it.category in categories }
            // Party prompts can intentionally exist in more than one category.
            // Collapse identical text before selection so enabling overlapping packs
            // cannot make the same prompt more likely or repeat as a separate item.
            .distinctBy { it.text }

        val eligible = avoidSeen(all) { it.text }
            .sortedBy { it.playCount }

        if (eligible.isEmpty()) return null

        // Prefer prompts with lower play counts while retaining randomness.
        val poolSize = (eligible.size / 2).coerceAtLeast(1)
        return eligible.take(poolSize).random().also { it.playCount++ }
    }

    fun getRandomTruth(difficulty: Difficulty): String {
        if (difficulty == Difficulty.FAVORITES) {
            val favorites = avoidSeen(FavoritesManager.getFavoriteTruths()) { it.text }
            val selected = favorites.randomOrNull()
            if (selected != null) {
                PromptHistoryManager.markAsSeen(selected.text)
                return "❤️ ${selected.text}"
            }
            return "No favorite truths yet! Tap ❤️ during gameplay to save some."
        }

        if (difficulty == Difficulty.CUSTOM) {
            val custom = avoidSeen(
                CustomPromptsManager.getEnabledPrompts().filter { it.type == PromptItemType.TRUTH }
            ) { it.text }
            val selected = custom.randomOrNull()
            if (selected != null) {
                PromptHistoryManager.markAsSeen(selected.text)
                return "✨ ${selected.text}"
            }
            return "No custom truths yet! Create some in My Prompts."
        }

        val dbPrompt = selectDatabasePrompt(PromptItemType.TRUTH, difficulty)
        if (dbPrompt != null) {
            PromptHistoryManager.markAsSeen(dbPrompt.text)
            return dbPrompt.text
        }

        val prompts = avoidSeen(truths[difficulty] ?: truths[Difficulty.MEDIUM]!!) { it }
        val selected = prompts.random()
        PromptHistoryManager.markAsSeen(selected)
        return selected
    }

    fun getRandomDare(difficulty: Difficulty): String {
        if (difficulty == Difficulty.FAVORITES) {
            val favorites = avoidSeen(FavoritesManager.getFavoriteDares()) { it.text }
            val selected = favorites.randomOrNull()
            if (selected != null) {
                PromptHistoryManager.markAsSeen(selected.text)
                return "❤️ ${selected.text}"
            }
            return "No favorite dares yet! Tap ❤️ during gameplay to save some."
        }

        if (difficulty == Difficulty.CUSTOM) {
            val custom = avoidSeen(
                CustomPromptsManager.getEnabledPrompts().filter { it.type == PromptItemType.DARE }
            ) { it.text }
            val selected = custom.randomOrNull()
            if (selected != null) {
                PromptHistoryManager.markAsSeen(selected.text)
                return "✨ ${selected.text}"
            }
            return "No custom dares yet! Create some in My Prompts."
        }

        val dbPrompt = selectDatabasePrompt(PromptItemType.DARE, difficulty)
        if (dbPrompt != null) {
            PromptHistoryManager.markAsSeen(dbPrompt.text)
            return dbPrompt.text
        }

        val prompts = avoidSeen(dares[difficulty] ?: dares[Difficulty.MEDIUM]!!) { it }
        val selected = prompts.random()
        PromptHistoryManager.markAsSeen(selected)
        return selected
    }

}

