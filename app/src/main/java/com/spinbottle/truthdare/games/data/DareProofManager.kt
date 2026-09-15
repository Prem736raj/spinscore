package com.spinbottle.truthdare.games.data

import android.content.Context
import android.net.Uri
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Represents a proof photo/video for a dare
 */
data class DareProof(
    val id: String = UUID.randomUUID().toString(),
    val dareText: String,
    val playerName: String,
    val playerEmoji: String,
    val photoUri: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Manager for storing and retrieving dare proof photos
 * Images are saved permanently in app's internal storage
 */
object DareProofManager {
    private val proofs = mutableListOf<DareProof>()
    
    fun addProof(proof: DareProof) {
        proofs.add(proof)
    }
    
    fun getProofs(): List<DareProof> = proofs.toList()
    
    fun getProofCount(): Int = proofs.size
    
    fun deleteProof(id: String) {
        proofs.removeAll { it.id == id }
    }
    
    fun clearSession() {
        proofs.clear()
    }
    
    fun hasProofs(): Boolean = proofs.isNotEmpty()
    
    /**
     * Get proof directory for this app - files stored here persist permanently
     */
    fun getProofDirectory(context: Context): File {
        val dir = File(context.filesDir, "dare_proofs")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
    
    /**
     * Create a new file for a proof photo with date/time in filename
     */
    fun createProofFile(context: Context): File {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        return File(getProofDirectory(context), "dare_proof_${timestamp}.jpg")
    }
    
    /**
     * Load all saved proofs from storage
     */
    fun loadProofsFromStorage(context: Context) {
        val dir = getProofDirectory(context)
        if (dir.exists()) {
            dir.listFiles()?.forEach { file ->
                if (file.extension == "jpg" && proofs.none { it.photoUri.contains(file.name) }) {
                    // Add existing photos as proofs
                    proofs.add(
                        DareProof(
                            dareText = "Previous dare",
                            playerName = "Player",
                            playerEmoji = "🎮",
                            photoUri = Uri.fromFile(file).toString(),
                            timestamp = file.lastModified()
                        )
                    )
                }
            }
        }
    }
}
