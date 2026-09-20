package com.spinbottle.truthdare.games.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * Session owner for active local gameplay.
 *
 * The snapshot survives Activity recreation and process death but is explicitly
 * excluded from cloud/device backup. UI-only animation/prompt reveal state is
 * intentionally not persisted; a restored game resumes at the start of the
 * current turn.
 */
object GameSessionHolder {
    private const val PREFS_NAME = "session_state_prefs"
    private const val KEY_SNAPSHOT = "active_session"

    private val gson = Gson()
    private var prefs: SharedPreferences? = null
    private var restoring = false

    var players: List<Player> = emptyList()
        set(value) {
            field = value
            persist()
        }

    var gameMode: GameMode = GameMode.CLASSIC
        set(value) {
            field = value
            persist()
        }

    var difficulty: Difficulty = Difficulty.MEDIUM
        set(value) {
            field = value
            persist()
        }

    var totalRounds: Int = 0
        set(value) {
            field = value.coerceAtLeast(0)
            persist()
        }

    var gameStartTime: Long = 0L
        set(value) {
            field = value.coerceAtLeast(0L)
            persist()
        }

    var isKidsModeFlow: Boolean = false
        set(value) {
            field = value
            persist()
        }

    var isTournament: Boolean = false
        set(value) {
            field = value
            persist()
        }

    var completionRecorded: Boolean = false
        private set(value) {
            field = value
            persist()
        }

    var targetScore: Int = 50
        set(value) {
            field = value.coerceAtLeast(1)
            persist()
        }

    var targetRounds: Int? = null
        set(value) {
            field = value?.coerceAtLeast(1)
            persist()
        }

    var eliminationMode: Boolean = false
        set(value) {
            field = value
            persist()
        }

    var eliminatedPlayers: MutableList<String> = mutableListOf()
        set(value) {
            field = value
            persist()
        }

    var currentChallengeLevel: Int = 1
        set(value) {
            field = value.coerceIn(1, 4)
            persist()
        }

    var roundsInCurrentLevel: Int = 0
        set(value) {
            field = value.coerceAtLeast(0)
            persist()
        }

    val roundsPerLevel: Int = 5

    var currentPlayerIndex: Int = 0
        set(value) {
            field = value.coerceAtLeast(0)
            persist()
        }

    var quickFireSecondsRemaining: Int = 30
        set(value) {
            field = value.coerceIn(0, 30)
            persist()
        }

    var couplesIntimacyLevel: Int = 3
        set(value) {
            field = value.coerceIn(1, 5)
            persist()
        }

    fun init(context: Context) {
        prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val json = prefs?.getString(KEY_SNAPSHOT, null) ?: return
        runCatching {
            gson.fromJson(json, SessionSnapshot::class.java)
        }.onSuccess { snapshot ->
            restore(snapshot)
        }.onFailure {
            prefs?.edit()?.remove(KEY_SNAPSHOT)?.apply()
        }
    }

    fun hasRestorableSession(): Boolean = players.isNotEmpty() && gameStartTime > 0L

    fun getChallengeDifficulty(): Difficulty = when (currentChallengeLevel) {
        1 -> Difficulty.EASY
        2 -> Difficulty.MEDIUM
        3 -> Difficulty.HARD
        else -> Difficulty.EXTREME
    }

    fun getLevelName(): String = when (currentChallengeLevel) {
        1 -> "Level 1: Easy"
        2 -> "Level 2: Medium"
        3 -> "Level 3: Hard"
        else -> "Level 4: Extreme"
    }

    fun getRoundsUntilLevelUp(): Int = roundsPerLevel - roundsInCurrentLevel

    fun advanceChallengeRound(): Boolean {
        roundsInCurrentLevel++
        if (roundsInCurrentLevel >= roundsPerLevel && currentChallengeLevel < 4) {
            currentChallengeLevel++
            roundsInCurrentLevel = 0
            persist()
            return true
        }
        persist()
        return false
    }

    fun startGame() {
        gameStartTime = System.currentTimeMillis()
        totalRounds = 0
        eliminatedPlayers.clear()
        currentChallengeLevel = 1
        roundsInCurrentLevel = 0
        completionRecorded = false
        persist()
    }

    /**
     * Claims the one-time completion side effect for the active session.
     * Returns false after the same completed session has already been recorded.
     */
    fun markCompletionRecorded(): Boolean {
        if (completionRecorded) return false
        completionRecorded = true
        return true
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

    fun updatePlayerStats(
        playerId: String,
        truthCompleted: Boolean,
        dareCompleted: Boolean,
        skipped: Boolean
    ) {
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

    fun getPlayerScore(playerId: String): Int =
        players.find { it.id == playerId }?.score ?: 0

    fun getLeader(): Player? = getActivePlayers().maxByOrNull { it.score }

    fun isMatchPoint(playerId: String): Boolean {
        val player = players.find { it.id == playerId } ?: return false
        return player.score == targetScore - 1
    }

    fun hasWinner(): Boolean = players.any { it.score >= targetScore }

    fun getWinner(): Player? = players.find { it.score >= targetScore }

    fun getActivePlayers(): List<Player> =
        if (eliminationMode) players.filter { it.id !in eliminatedPlayers } else players

    fun eliminatePlayer(playerId: String) {
        if (playerId !in eliminatedPlayers) {
            eliminatedPlayers.add(playerId)
            persist()
        }
    }

    fun isPlayerEliminated(playerId: String): Boolean = playerId in eliminatedPlayers

    fun getMostDaring(): Player? =
        players.filter { it.daresCompleted > 0 }
            .maxByOrNull {
                it.daresCompleted.toFloat() /
                    (it.daresCompleted + it.truthsCompleted + it.skips)
            }

    fun getMostHonest(): Player? =
        players.filter { it.truthsCompleted > 0 }
            .maxByOrNull {
                it.truthsCompleted.toFloat() /
                    (it.daresCompleted + it.truthsCompleted + it.skips)
            }

    fun clear() {
        restoring = true
        players = emptyList()
        gameMode = GameMode.CLASSIC
        difficulty = Difficulty.MEDIUM
        totalRounds = 0
        gameStartTime = 0L
        isKidsModeFlow = false
        isTournament = false
        completionRecorded = false
        targetScore = 50
        targetRounds = null
        eliminationMode = false
        eliminatedPlayers = mutableListOf()
        currentChallengeLevel = 1
        roundsInCurrentLevel = 0
        currentPlayerIndex = 0
        quickFireSecondsRemaining = 30
        couplesIntimacyLevel = 3
        restoring = false
        prefs?.edit()?.remove(KEY_SNAPSHOT)?.apply()
    }

    private fun persist() {
        if (restoring) return
        val storage = prefs ?: return

        val snapshot = SessionSnapshot(
            players = players.map { player ->
                StoredPlayer(
                    id = player.id,
                    name = player.name,
                    avatar = player.avatar,
                    colorIndex = PlayerColors.colors.indexOf(player.color).coerceAtLeast(0),
                    score = player.score,
                    truthsCompleted = player.truthsCompleted,
                    daresCompleted = player.daresCompleted,
                    skips = player.skips
                )
            },
            gameMode = gameMode.name,
            difficulty = difficulty.name,
            totalRounds = totalRounds,
            gameStartTime = gameStartTime,
            isKidsModeFlow = isKidsModeFlow,
            isTournament = isTournament,
            completionRecorded = completionRecorded,
            targetScore = targetScore,
            targetRounds = targetRounds,
            eliminationMode = eliminationMode,
            eliminatedPlayers = eliminatedPlayers.toList(),
            currentChallengeLevel = currentChallengeLevel,
            roundsInCurrentLevel = roundsInCurrentLevel,
            currentPlayerIndex = currentPlayerIndex,
            quickFireSecondsRemaining = quickFireSecondsRemaining,
            couplesIntimacyLevel = couplesIntimacyLevel
        )

        storage.edit().putString(KEY_SNAPSHOT, gson.toJson(snapshot)).apply()
    }

    private fun restore(snapshot: SessionSnapshot) {
        restoring = true
        players = snapshot.players.map { stored ->
            Player(
                id = stored.id,
                name = stored.name,
                avatar = stored.avatar,
                color = PlayerColors.colors.getOrElse(stored.colorIndex) { PlayerColors.colors.first() },
                score = stored.score,
                truthsCompleted = stored.truthsCompleted,
                daresCompleted = stored.daresCompleted,
                skips = stored.skips
            )
        }
        gameMode = runCatching { GameMode.valueOf(snapshot.gameMode) }.getOrDefault(GameMode.CLASSIC)
        difficulty = runCatching { Difficulty.valueOf(snapshot.difficulty) }.getOrDefault(Difficulty.MEDIUM)
        totalRounds = snapshot.totalRounds
        gameStartTime = snapshot.gameStartTime
        isKidsModeFlow = snapshot.isKidsModeFlow
        isTournament = snapshot.isTournament
        completionRecorded = snapshot.completionRecorded
        targetScore = snapshot.targetScore
        targetRounds = snapshot.targetRounds
        eliminationMode = snapshot.eliminationMode
        eliminatedPlayers = snapshot.eliminatedPlayers.toMutableList()
        currentChallengeLevel = snapshot.currentChallengeLevel
        roundsInCurrentLevel = snapshot.roundsInCurrentLevel
        currentPlayerIndex = snapshot.currentPlayerIndex
        quickFireSecondsRemaining = snapshot.quickFireSecondsRemaining
        couplesIntimacyLevel = snapshot.couplesIntimacyLevel
        restoring = false
    }

    private data class SessionSnapshot(
        val players: List<StoredPlayer> = emptyList(),
        val gameMode: String = GameMode.CLASSIC.name,
        val difficulty: String = Difficulty.MEDIUM.name,
        val totalRounds: Int = 0,
        val gameStartTime: Long = 0L,
        val isKidsModeFlow: Boolean = false,
        val isTournament: Boolean = false,
        val completionRecorded: Boolean = false,
        val targetScore: Int = 50,
        val targetRounds: Int? = null,
        val eliminationMode: Boolean = false,
        val eliminatedPlayers: List<String> = emptyList(),
        val currentChallengeLevel: Int = 1,
        val roundsInCurrentLevel: Int = 0,
        val currentPlayerIndex: Int = 0,
        val quickFireSecondsRemaining: Int = 30,
        val couplesIntimacyLevel: Int = 3
    )

    private data class StoredPlayer(
        val id: String = "",
        val name: String = "",
        val avatar: String = "🙂",
        val colorIndex: Int = 0,
        val score: Int = 0,
        val truthsCompleted: Int = 0,
        val daresCompleted: Int = 0,
        val skips: Int = 0
    )
}
