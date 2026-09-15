package com.spinbottle.truthdare.games.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.ui.theme.*

@Composable
fun PinNumpad(
    pin: String,
    maxLength: Int = 4,
    onPinChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // PIN dots display
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            repeat(maxLength) { index ->
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            if (index < pin.length) AccentPurple
                            else GlassWhite.copy(alpha = 0.3f)
                        )
                        .border(
                            2.dp,
                            if (index < pin.length) AccentPurple else GlassBorder,
                            CircleShape
                        )
                )
            }
        }
        
        // Number pad
        val buttons = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("", "0", "⌫")
        )
        
        buttons.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                row.forEach { digit ->
                    if (digit.isEmpty()) {
                        Spacer(modifier = Modifier.size(72.dp))
                    } else if (digit == "⌫") {
                        // Backspace button
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(GlassWhite.copy(alpha = 0.1f))
                                .clickable {
                                    if (pin.isNotEmpty()) {
                                        onPinChange(pin.dropLast(1))
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "Delete",
                                tint = TextWhite,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        // Number button
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(GlassWhite.copy(alpha = 0.1f))
                                .border(1.dp, GlassBorder, CircleShape)
                                .clickable {
                                    if (pin.length < maxLength) {
                                        onPinChange(pin + digit)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextWhite
                            )
                        }
                    }
                }
            }
        }
    }
}
