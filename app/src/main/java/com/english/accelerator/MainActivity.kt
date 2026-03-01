package com.english.accelerator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.english.accelerator.ui.navigation.BottomNavigationBar
import com.english.accelerator.ui.navigation.Screen
import com.english.accelerator.ui.settings.SettingsScreen
import com.english.accelerator.ui.speaking.SpeakingScreen
import com.english.accelerator.ui.theme.AcceleratorTheme
import com.english.accelerator.ui.vocabulary.VocabularyScreen
import com.english.accelerator.ui.writing.WritingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AcceleratorTheme {
                AcceleratorApp()
            }
        }
    }
}

@Composable
fun AcceleratorApp() {
    var currentRoute by rememberSaveable { mutableStateOf(Screen.Vocabulary.route) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = { route -> currentRoute = route }
            )
        }
    ) { innerPadding ->
        when (currentRoute) {
            Screen.Vocabulary.route -> VocabularyScreen()
            Screen.Writing.route -> WritingScreen()
            Screen.Speaking.route -> SpeakingScreen()
            Screen.Settings.route -> SettingsScreen()
        }
    }
}