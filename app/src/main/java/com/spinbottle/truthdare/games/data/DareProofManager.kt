package com.spinbottle.truthdare.games.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class DareProof(
    val id: String = UUID.randomUUID().toString(),
    val dareText: String,
    val playerName: String,
    val playerEmoji: String,
    val fileName: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Owns dare-proof metadata and the corresponding app-private image files.
 *
 * Only a file name is persisted. Absolute paths and FileProvider URIs are derived
 * at runtime so metadata remains valid across installs/build variants and cannot
 * escape the dedicated dare_proofs directory.
 */
object DareProofManager {
    private const val METADATA_FILE = "dare_proofs_metadata.json"
    private const val PREFS_NAME = "dare_proof_migration_prefs"
    private const val KEY_LEGACY_IMPORT_COMPLETE = "legacy_import_complete"

    private val gson = Gson()
    private val proofs = mutableListOf<DareProof>()
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        loadProofsFromStorage(context.applicationContext)
    }

    fun addProof(proof: DareProof) {
        val context = requireContext()
        val file = proofFile(context, proof.fileName)
        if (!file.isFile) return

        proofs.removeAll { it.id == proof.id || it.fileName == proof.fileName }
        proofs.add(proof)
        persistMetadata(context)
    }

    fun getProofs(): List<DareProof> = proofs.toList()

    fun getProofCount(): Int = proofs.size

    fun hasProofs(): Boolean = proofs.isNotEmpty()

    fun getProofUri(context: Context, proof: DareProof): Uri {
        val file = proofFile(context, proof.fileName)
        require(file.isFile) { "Dare proof file does not exist" }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    /**
     * Deletes both metadata and the underlying private image.
     * Returns true only when the file is absent after the operation.
     */
    fun deleteProof(id: String): Boolean {
        val context = requireContext()
        val proof = proofs.firstOrNull { it.id == id } ?: return false
        val file = proofFile(context, proof.fileName)
        val fileRemoved = !file.exists() || file.delete()

        if (fileRemoved) {
            proofs.removeAll { it.id == id }
            persistMetadata(context)
        }
        return fileRemoved
    }

    /**
     * Removes a pre-created camera file when capture is cancelled/denied.
     * Tracked proof files are never removed by this method.
     */
    fun deleteUntrackedFile(fileName: String?) {
        if (fileName.isNullOrBlank()) return
        if (proofs.any { it.fileName == fileName }) return

        val context = requireContext()
        val file = proofFile(context, fileName)
        if (file.exists()) {
            file.delete()
        }
    }

    fun clearSession() {
        // Proofs are intentionally persistent; a game-session reset must not
        // silently delete user photos.
    }

    fun getProofDirectory(context: Context): File {
        val dir = File(context.filesDir, "dare_proofs")
        if (!dir.exists() && !dir.mkdirs()) {
            check(dir.isDirectory) { "Unable to create dare proof directory" }
        }
        return dir
    }

    fun createProofFile(context: Context): File {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
        val timestamp = dateFormat.format(Date())
        val uniqueSuffix = UUID.randomUUID().toString().take(8)
        return File(
            getProofDirectory(context),
            "dare_proof_${timestamp}_${uniqueSuffix}.jpg"
        )
    }

    private fun loadProofsFromStorage(context: Context) {
        proofs.clear()

        val metadata = metadataFile(context)
        if (metadata.isFile) {
            runCatching {
                val type = object : TypeToken<List<DareProof>>() {}.type
                val stored: List<DareProof> = gson.fromJson(metadata.readText(), type)
                stored.filterTo(proofs) { proof ->
                    runCatching { proofFile(context, proof.fileName).isFile }.getOrDefault(false)
                }
            }
        }

        // One-time legacy import and stale orphan cleanup
        importLegacyProofFilesOnce(context)
        cleanupOrphanFiles(context)

        proofs.sortBy { it.timestamp }
        persistMetadata(context)
    }

    private fun importLegacyProofFilesOnce(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(KEY_LEGACY_IMPORT_COMPLETE, false)) return

        val knownFiles = proofs.mapTo(mutableSetOf()) { it.fileName }

        getProofDirectory(context)
            .listFiles()
            ?.filter { it.isFile && it.extension.equals("jpg", ignoreCase = true) }
            ?.filterNot { it.name in knownFiles }
            ?.forEach { file ->
                proofs.add(
                    DareProof(
                        dareText = "Previous dare",
                        playerName = "Player",
                        playerEmoji = "🎮",
                        fileName = file.name,
                        timestamp = file.lastModified()
                    )
                )
            }

        prefs.edit().putBoolean(KEY_LEGACY_IMPORT_COMPLETE, true).apply()
    }

    private fun cleanupOrphanFiles(context: Context) {
        val tracked = proofs.mapTo(mutableSetOf()) { it.fileName }
        val cutoff = System.currentTimeMillis() - 24L * 60L * 60L * 1000L

        getProofDirectory(context)
            .listFiles()
            ?.filter { it.isFile }
            ?.filter { it.name !in tracked && it.lastModified() < cutoff }
            ?.forEach { it.delete() }
    }

    fun deleteAllProofs(): Boolean {
        val context = requireContext()
        val directory = getProofDirectory(context)

        val filesDeleted = directory
            .listFiles()
            .orEmpty()
            .all { !it.exists() || it.delete() }

        if (filesDeleted) {
            proofs.clear()
            persistMetadata(context)
        }

        return filesDeleted
    }

    private fun persistMetadata(context: Context) {
        val target = metadataFile(context)
        val temp = File(context.filesDir, "$METADATA_FILE.tmp")
        temp.writeText(gson.toJson(proofs))

        if (!temp.renameTo(target)) {
            target.writeText(temp.readText())
            temp.delete()
        }
    }

    private fun metadataFile(context: Context): File = File(context.filesDir, METADATA_FILE)

    private fun proofFile(context: Context, fileName: String): File {
        require(fileName.isNotBlank()) { "Proof file name is blank" }
        require(File(fileName).name == fileName) { "Invalid proof file name" }

        val directory = getProofDirectory(context).canonicalFile
        val file = File(directory, fileName).canonicalFile
        require(file.parentFile == directory) { "Proof path escapes storage directory" }
        return file
    }

    private fun requireContext(): Context =
        checkNotNull(appContext) { "DareProofManager.init(context) must be called first" }
}
