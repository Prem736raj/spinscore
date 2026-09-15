package com.spinbottle.truthdare.games.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.spinbottle.truthdare.games.data.DareProof
import com.spinbottle.truthdare.games.data.DareProofManager
import com.spinbottle.truthdare.games.ui.theme.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DareGalleryScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var proofs by remember { mutableStateOf(DareProofManager.getProofs()) }
    var showDeleteDialog by remember { mutableStateOf<DareProof?>(null) }
    var showFullImage by remember { mutableStateOf<String?>(null) }
    
    // Full image viewer dialog
    showFullImage?.let { imageUri ->
        AlertDialog(
            onDismissRequest = { showFullImage = null },
            modifier = Modifier.fillMaxWidth(),
            title = null,
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showFullImage = null }
                ) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Full image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showFullImage = null }) {
                    Text("Close", color = AccentTeal)
                }
            },
            containerColor = Color.Black.copy(alpha = 0.95f)
        )
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { proofToDelete ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Proof?", fontWeight = FontWeight.Bold, color = TextWhite) },
            text = { Text("This will permanently delete this dare proof.", color = TextMuted) },
            confirmButton = {
                Button(
                    onClick = {
                        DareProofManager.deleteProof(proofToDelete.id)
                        // Delete file
                        try {
                            File(Uri.parse(proofToDelete.photoUri).path ?: "").delete()
                        } catch (_: Exception) { }
                        proofs = DareProofManager.getProofs()
                        showDeleteDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SkipRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = DarkCard
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkBackgroundSecondary, DarkBackground)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(GlassWhite)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "📸 Dare Gallery",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${proofs.size} proof${if (proofs.size != 1) "s" else ""} captured",
                        fontSize = 14.sp,
                        color = AccentTeal
                    )
                }
            }
            
            if (proofs.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📷", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No dare proofs yet!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tap the camera icon during dares\nto capture proof photos",
                            fontSize = 14.sp,
                            color = TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // Proofs list
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(proofs.reversed()) { proof ->
                        DareProofCard(
                            proof = proof,
                            onImageClick = { showFullImage = proof.photoUri },
                            onShare = {
                                try {
                                    val file = File(Uri.parse(proof.photoUri).path ?: "")
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        file
                                    )
                                    val shareIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        type = "image/jpeg"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        putExtra(Intent.EXTRA_TEXT, "🔥 Dare: ${proof.dareText}")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Proof"))
                                } catch (e: Exception) {
                                    // Handle share error
                                }
                            },
                            onDelete = { showDeleteDialog = proof }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun DareProofCard(
    proof: DareProof,
    onImageClick: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Photo - tap to view fullscreen
            AsyncImage(
                model = proof.photoUri,
                contentDescription = "Dare proof - tap to enlarge",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .clickable { onImageClick() },
                contentScale = ContentScale.Crop
            )
            
            // Info
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Player and time
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = proof.playerEmoji,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = proof.playerName,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = dateFormat.format(Date(proof.timestamp)),
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Dare text
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(DareOrange.copy(alpha = 0.2f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "🔥 ${proof.dareText}",
                        fontSize = 14.sp,
                        color = TextWhite
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Action buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = AccentTeal
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share", color = AccentTeal)
                    }
                    
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = SkipRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Delete", color = SkipRed)
                    }
                }
            }
        }
    }
}
