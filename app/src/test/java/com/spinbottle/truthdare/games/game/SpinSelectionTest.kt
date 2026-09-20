package com.spinbottle.truthdare.games.game

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpinSelectionTest {
    @Test
    fun fourPlayers_wraparoundSelectsTopPlayer() {
        assertEquals(0, SpinSelection.playerIndexForRotation(350f, 4))
        assertEquals(0, SpinSelection.playerIndexForRotation(10f, 4))
    }

    @Test
    fun fourPlayers_cardinalCentersMapExactly() {
        assertEquals(0, SpinSelection.playerIndexForRotation(0f, 4))
        assertEquals(1, SpinSelection.playerIndexForRotation(90f, 4))
        assertEquals(2, SpinSelection.playerIndexForRotation(180f, 4))
        assertEquals(3, SpinSelection.playerIndexForRotation(270f, 4))
        assertEquals(0, SpinSelection.playerIndexForRotation(360f, 4))
    }

    @Test
    fun negativeAndLargeRotationsNormalize() {
        assertEquals(3, SpinSelection.playerIndexForRotation(-90f, 4))
        assertEquals(1, SpinSelection.playerIndexForRotation(810f, 4))
    }

    @Test(expected = IllegalArgumentException::class)
    fun zeroPlayersRejected() {
        SpinSelection.playerIndexForRotation(0f, 0)
    }

    @Test
    fun uniformAnglesHaveNoMaterialPositionBias() {
        val random = Random(736)
        val sampleSize = 100_000

        for (playerCount in listOf(2, 3, 4, 5, 6, 8, 10, 12, 16)) {
            val counts = IntArray(playerCount)
            repeat(sampleSize) {
                val angle = random.nextFloat() * 360f
                counts[SpinSelection.playerIndexForRotation(angle, playerCount)]++
            }

            val expected = sampleSize.toDouble() / playerCount
            counts.forEachIndexed { index, count ->
                val relativeDeviation = kotlin.math.abs(count - expected) / expected
                assertTrue(
                    "playerCount=$playerCount index=$index count=$count expected=$expected",
                    relativeDeviation < 0.06
                )
            }
        }
    }
}
