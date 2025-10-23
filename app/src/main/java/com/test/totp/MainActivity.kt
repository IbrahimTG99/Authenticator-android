package com.test.totp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import androidx.navigation.compose.rememberNavController
import com.test.totp.presentation.navigation.TotpNavigation
import com.test.totp.presentation.viewmodel.SettingsViewModel
import com.test.totp.ui.theme.TotpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
val settingsViewModel: SettingsViewModel = koinViewModel()
            val prefs by settingsViewModel.preferences.collectAsState(initial = null)
            val dark = prefs?.isDarkModeEnabled ?: isSystemInDarkTheme()

            TotpTheme(darkTheme = dark) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    TotpNavigation(navController = navController)
                }
            }
        }
    }
}
