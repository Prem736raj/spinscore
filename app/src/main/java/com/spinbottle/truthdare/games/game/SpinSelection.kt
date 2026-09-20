package com.spinbottle.truthdare.games.game

import kotlin.math.floor

/**
 * Maps the bottle's normalized clockwise rotation to the nearest player center.
 *
 * PlayerCircle places player 0 at the top and subsequent players at equal
 * clockwise sectors, so player 0 owns the wraparound half-sector around 0/360.
 */
object SpinSelection {
    fun playerIndexForRotation(rotation: Float, playerCount: Int): Int {
        require(playerCount > 0) { "playerCount must be greater than zero" }

        val normalized = ((rotation % 360f) + 360f) % 360f
        val sectorSize = 360f / playerCount
        return floor((normalized + sectorSize / 2f) / sectorSize)
            .toInt()
            .mod(playerCount)
    }
}
