package com.spinbottle.truthdare.games

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.spinbottle.truthdare.games.data.CustomPromptsManager
import com.spinbottle.truthdare.games.data.FavoritesManager
import com.spinbottle.truthdare.games.data.PlayerProfileManager
import com.spinbottle.truthdare.games.data.PromptHistoryManager
import com.spinbottle.truthdare.games.data.PromptPackManager
import com.spinbottle.truthdare.games.data.ThemeManager
import com.spinbottle.truthdare.games.ui.theme.SpinBottleTheme

import androidx.lifecycle.lifecycleScope
import com.spinbottle.truthdare.games.billing.BillingManager

class MainActivity : ComponentActivity() {
    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize managers for persistence
        CustomPromptsManager.init(this)
        PlayerProfileManager.init(this)
        FavoritesManager.init(this)
        PromptHistoryManager.init(this)
        ThemeManager.init(this)
        PromptPackManager.init(this)
        
        billingManager = BillingManager(this, lifecycleScope)
        
        enableEdgeToEdge()
        setContent {
            SpinBottleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    SpinBottleApp(billingManager = billingManager)
                }
            }
        }
    }
}
