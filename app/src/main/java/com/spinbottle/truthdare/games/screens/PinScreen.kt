package com.spinbottle.truthdare.games.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spinbottle.truthdare.games.data.PinManager
import com.spinbottle.truthdare.games.data.PinResult
import com.spinbottle.truthdare.games.ui.components.PinNumpad
import com.spinbottle.truthdare.games.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class PinScreenMode {
    SETUP,        // First time setting PIN
    CONFIRM,      // Confirm PIN during setup
    VERIFY        // Enter PIN to access content
}

@Composable
fun PinScreen(
    mode: PinScreenMode,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current
    val pinManager = remember { PinManager(context) }
    val scope = rememberCoroutineScope()
    
    var pin by remember { mutableStateOf("") }
    var firstPin by remember { mutableStateOf("") }
    var currentMode by remember { mutableStateOf(mode) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLocked by remember { mutableStateOf(false) }
    var lockTimeRemaining by remember { mutableStateOf(0L) }
    
    // Lockout countdown timer
    LaunchedEffect(isLocked, lockTimeRemaining) {
        if (isLocked && lockTimeRemaining > 0) {
            delay(1000)
            lockTimeRemaining -= 1000
            if (lockTimeRemaining <= 0) {
                isLocked = false
            }
        }
    }
    
    // Handle PIN entry completion
    LaunchedEffect(pin) {
        if (pin.length == 4) {
            when (currentMode) {
                PinScreenMode.SETUP -> {
                    firstPin = pin
                    pin = ""
                    currentMode = PinScreenMode.CONFIRM
                    errorMessage = null
                }
                PinScreenMode.CONFIRM -> {
                    if (pin == firstPin) {
                        pinManager.setPin(pin)
                        pinManager.setAgeVerified(true)
                        onSuccess()
                    } else {
                        errorMessage = "PINs don't match. Try again."
                        pin = ""
                        firstPin = ""
                        currentMode = PinScreenMode.SETUP
                    }
                }
                PinScreenMode.VERIFY -> {
                    when (val result = pinManager.verifyPin(pin)) {
                        is PinResult.SUCCESS -> {
                            onSuccess()
                        }
                        is PinResult.WRONG_PIN -> {
                            errorMessage = "Wrong PIN. ${result.attemptsRemaining} attempts left."
                            pin = ""
                        }
                        is PinResult.LOCKED_OUT -> {
                            isLocked = true
                            lockTimeRemaining = result.remainingMs
                            errorMessage = null
                            pin = ""
                        }
                        is PinResult.NO_PIN_SET -> {
                            currentMode = PinScreenMode.SETUP
                        }
                    }
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBackground, DarkBackgroundSecondary)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
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
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Lock icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AccentPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = when (currentMode) {
                    PinScreenMode.SETUP -> "Create PIN"
                    PinScreenMode.CONFIRM -> "Confirm PIN"
                    PinScreenMode.VERIFY -> "Enter PIN"
                },
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = when (currentMode) {
                    PinScreenMode.SETUP -> "Set a 4-digit PIN to access adult content"
                    PinScreenMode.CONFIRM -> "Enter your PIN again to confirm"
                    PinScreenMode.VERIFY -> "Enter your PIN to continue"
                },
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Error message or lockout
            if (isLocked) {
                val minutes = (lockTimeRemaining / 60000).toInt()
                val seconds = ((lockTimeRemaining % 60000) / 1000).toInt()
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(SkipRed.copy(alpha = 0.2f))
                        .padding(16.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Too many attempts",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SkipRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try again in ${minutes}:${seconds.toString().padStart(2, '0')}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }
                }
            } else {
                errorMessage?.let { error ->
                    Text(
                        text = error,
                        fontSize = 14.sp,
                        color = SkipRed,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // PIN numpad
                PinNumpad(
                    pin = pin,
                    onPinChange = { pin = it }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
