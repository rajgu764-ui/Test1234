package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MovaViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MovaViewModel = viewModel()
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = "screen_transition"
                ) { screen ->
                    when (screen) {
                        is Screen.Splash -> SplashScreen(viewModel)
                        is Screen.Onboarding -> OnboardingScreen(viewModel)
                        is Screen.Login -> LoginScreen(viewModel)
                        is Screen.Main -> MainScreen(viewModel)
                        is Screen.Details -> MovieDetailsScreen(viewModel)
                        is Screen.Player -> VideoPlaybackScreen(viewModel, screen.movieId, screen.title)
                    }
                }
            }
        }
    }
}
