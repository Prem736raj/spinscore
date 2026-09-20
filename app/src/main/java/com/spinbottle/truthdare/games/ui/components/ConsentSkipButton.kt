package com.spinbottle.truthdare.games.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.couples.CouplesPromptTag
import com.spinbottle.truthdare.games.ui.theme.DarkCard
import com.spinbottle.truthdare.games.ui.theme.SkipRed
import com.spinbottle.truthdare.games.ui.theme.TextMuted
import com.spinbottle.truthdare.games.ui.theme.TextWhite

@Composable
fun ConsentSkipButton(
    onSkip: () -> Unit,
    onBlockTag: ((CouplesPromptTag) -> Unit)? = null,
    promptTags: Set<CouplesPromptTag> = emptySet(),
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        TextButton(
            onClick = {
                if (promptTags.isNotEmpty() && onBlockTag != null) {
                    showMenu = true
                } else {
                    onSkip()
                }
            },
            modifier = Modifier
                .heightIn(min = 48.dp)
                .semantics {
                    role = Role.Button
                }
        ) {
            Text(
                text = "Skip / Not Comfortable",
                color = TextMuted,
                fontSize = 14.sp
            )
        }

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            modifier = Modifier.widthIn(min = 220.dp)
        ) {
            DropdownMenuItem(
                text = { Text("Skip this prompt", color = TextWhite) },
                onClick = {
                    showMenu = false
                    onSkip()
                }
            )
            promptTags.firstOrNull()?.let { tag ->
                DropdownMenuItem(
                    text = {
                        Text(
                            "Don't show this topic again",
                            color = SkipRed
                        )
                    },
                    onClick = {
                        showMenu = false
                        onBlockTag?.invoke(tag)
                    }
                )
            }
        }
    }
}
