package com.spinbottle.truthdare.games.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.ui.theme.*

@Composable
fun GlassButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = AccentPurple,
    height: Dp = 60.dp,
    fullWidth: Boolean = true
) {
    val shape = RoundedCornerShape(20.dp)
    
    Box(
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .height(height)
            .heightIn(min = 48.dp)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassWhite,
                        GlassDark
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassBorder,
                        Color.Transparent
                    )
                ),
                shape = shape
            )
            .semantics {
                role = Role.Button
                contentDescription = text
            }
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextWhite
            )
        }
    }
}

@Composable
fun GlassIconButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = TextLight
) {
    val shape = RoundedCornerShape(16.dp)
    
    Column(
        modifier = modifier
            .heightIn(min = 56.dp)
            .widthIn(min = 64.dp)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassWhite.copy(alpha = 0.05f),
                        GlassDark.copy(alpha = 0.1f)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = GlassBorder.copy(alpha = 0.3f),
                shape = shape
            )
            .semantics {
                role = Role.Button
                contentDescription = text
            }
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextLight
        )
    }
}
