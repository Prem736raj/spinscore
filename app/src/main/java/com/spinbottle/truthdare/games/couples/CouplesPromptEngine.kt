package com.spinbottle.truthdare.games.couples

import kotlin.random.Random

class CouplesPromptEngine(
    private val random: Random = Random.Default
) {

    fun nextPrompt(
        catalog: List<CouplesPrompt>,
        preferences: CouplesPreferences,
        recentPromptIds: Set<String> = emptySet(),
        recentTags: List<CouplesPromptTag> = emptyList(),
        preferredPackTags: Set<CouplesPromptTag> = emptySet()
    ): CouplesPrompt? {

        val allowed = catalog.filter {
            CouplesContentPolicy.isAllowed(it, preferences)
        }

        if (allowed.isEmpty()) return null

        val unseen = allowed.filterNot { it.id in recentPromptIds }
        val basePool = if (unseen.isNotEmpty()) unseen else allowed

        val packPool = if (preferredPackTags.isEmpty()) {
            basePool
        } else {
            val matched = basePool.filter { prompt ->
                prompt.tags.any { it in preferredPackTags }
            }
            if (matched.isNotEmpty()) matched else basePool
        }

        val lastTag = recentTags.lastOrNull()
        val varied = if (lastTag == null) {
            packPool
        } else {
            val tagFiltered = packPool.filterNot {
                lastTag in it.tags && it.tags.size == 1
            }
            if (tagFiltered.isNotEmpty()) tagFiltered else packPool
        }

        return varied.random(random)
    }
}

