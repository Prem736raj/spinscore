package com.spinbottle.truthdare.games.data

/**
 * Simple in-memory holder for game session data
 * Used to pass data between screens without complex navigation arguments
 */
object GameSessionHolder {
    var players: List<Player> = emptyList()
    var gameMode: GameMode = GameMode.CLASSIC
    var difficulty: Difficulty = Difficulty.MEDIUM
    var totalRounds: Int = 0
    var gameStartTime: Long = 0L
    var isKidsModeFlow: Boolean = false  // Track if user started from Kids Mode button
    
    // Tournament settings
    var isTournament: Boolean = false
    var targetScore: Int = 50  // 25, 50, or 100
    var targetRounds: Int? = null  // null = use targetScore, otherwise rounds-based
    var eliminationMode: Boolean = false
    var eliminatedPlayers: MutableList<String> = mutableListOf()
    
    // Challenge mode settings
    var currentChallengeLevel: Int = 1  // 1=Easy, 2=Medium, 3=Hard, 4=Extreme
    var roundsInCurrentLevel: Int = 0
    val roundsPerLevel: Int = 5  // Increase difficulty every 5 rounds
    
    fun getChallengeDifficulty(): Difficulty {
        return when (currentChallengeLevel) {
            1 -> Difficulty.EASY
            2 -> Difficulty.MEDIUM
            3 -> Difficulty.HARD
            else -> Difficulty.EXTREME
        }
    }
    
    fun getLevelName(): String {
        return when (currentChallengeLevel) {
            1 -> "Level 1: Easy"
            2 -> "Level 2: Medium"
            3 -> "Level 3: Hard"
            else -> "Level 4: Extreme"
        }
    }
    
    fun getRoundsUntilLevelUp(): Int {
        return roundsPerLevel - roundsInCurrentLevel
    }
    
    fun advanceChallengeRound(): Boolean {
        roundsInCurrentLevel++
        if (roundsInCurrentLevel >= roundsPerLevel && currentChallengeLevel < 4) {
            currentChallengeLevel++
            roundsInCurrentLevel = 0
            return true // Level up!
        }
        return false
    }
    
    fun startGame() {
        gameStartTime = System.currentTimeMillis()
        totalRounds = 0
        eliminatedPlayers.clear()
        currentChallengeLevel = 1
        roundsInCurrentLevel = 0
    }
    
    fun getGameDurationMinutes(): Int {
        if (gameStartTime == 0L) return 0
        return ((System.currentTimeMillis() - gameStartTime) / 60000).toInt()
    }
    
    fun getGameDurationFormatted(): String {
        if (gameStartTime == 0L) return "0:00"
        val durationMs = System.currentTimeMillis() - gameStartTime
        val minutes = (durationMs / 60000).toInt()
        val seconds = ((durationMs % 60000) / 1000).toInt()
        return "$minutes:${seconds.toString().padStart(2, '0')}"
    }
    
    fun updatePlayerStats(playerId: String, truthCompleted: Boolean, dareCompleted: Boolean, skipped: Boolean) {
        players = players.map { player ->
            if (player.id == playerId) {
                val pointsEarned = if (truthCompleted || dareCompleted) 1 else 0
                player.copy(
                    truthsCompleted = player.truthsCompleted + if (truthCompleted) 1 else 0,
                    daresCompleted = player.daresCompleted + if (dareCompleted) 1 else 0,
                    skips = player.skips + if (skipped) 1 else 0,
                    score = player.score + pointsEarned
                )
            } else {
                player
            }
        }
    }
    
    fun incrementRound() {
        totalRounds++
    }
    
    // Tournament functions
    fun getPlayerScore(playerId: String): Int {
        return players.find { it.id == playerId }?.score ?: 0
    }
    
    fun getLeader(): Player? {
        return getActivePlayers().maxByOrNull { it.score }
    }
    
    fun isMatchPoint(playerId: String): Boolean {
        val player = players.find { it.id == playerId } ?: return false
        return player.score == targetScore - 1
    }
    
    fun hasWinner(): Boolean {
        return players.any { it.score >= targetScore }
    }
    
    fun getWinner(): Player? {
        return players.find { it.score >= targetScore }
    }
    
    fun getActivePlayers(): List<Player> {
        return if (eliminationMode) {
            players.filter { !eliminatedPlayers.contains(it.id) }
        } else {
            players
        }
    }
    
    fun eliminatePlayer(playerId: String) {
        eliminatedPlayers.add(playerId)
    }
    
    fun isPlayerEliminated(playerId: String): Boolean {
        return eliminatedPlayers.contains(playerId)
    }
    
    /**
     * Get player with highest dare completion percentage
     */
    fun getMostDaring(): Player? {
        return players.filter { it.daresCompleted > 0 }
            .maxByOrNull { it.daresCompleted.toFloat() / (it.daresCompleted + it.truthsCompleted + it.skips) }
    }
    
    /**
     * Get player with highest truth completion percentage
     */
    fun getMostHonest(): Player? {
        return players.filter { it.truthsCompleted > 0 }
            .maxByOrNull { it.truthsCompleted.toFloat() / (it.daresCompleted + it.truthsCompleted + it.skips) }
    }
    
    fun clear() {
        players = emptyList()
        gameMode = GameMode.CLASSIC
        difficulty = Difficulty.MEDIUM
        totalRounds = 0
        gameStartTime = 0L
        isKidsModeFlow = false
        isTournament = false
        targetScore = 50
        targetRounds = null
        eliminationMode = false
        eliminatedPlayers.clear()
    }
}

