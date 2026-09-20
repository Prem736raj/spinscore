package com.spinbottle.truthdare.games.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.ui.theme.AccentPink
import com.spinbottle.truthdare.games.ui.theme.DarkCard
import com.spinbottle.truthdare.games.ui.theme.GlassBorder
import com.spinbottle.truthdare.games.ui.theme.TextMuted
import com.spinbottle.truthdare.games.ui.theme.TextWhite

@Composable
fun ComfortChip(
    label: String,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    emoji: String = "✨"
) {
    Surface(
        selected = isSelected,
        onClick = onToggle,
        modifier = modifier
            .heightIn(min = 48.dp)
            .semantics {
                role = Role.Checkbox
                selected = isSelected
                stateDescription = if (isSelected) "$label allowed" else "$label not allowed"
            },
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) AccentPink.copy(alpha = 0.25f) else DarkCard,
        border = BorderStroke(
            width = 1.5.dp,
            color = if (isSelected) AccentPink else GlassBorder.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) TextWhite else TextMuted
            )
        }
    }
}

