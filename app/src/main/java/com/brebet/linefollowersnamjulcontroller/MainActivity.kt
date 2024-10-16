package com.brebet.linefollowersnamjulcontroller

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.brebet.linefollowersnamjulcontroller.screen.MainScreen
import com.brebet.linefollowersnamjulcontroller.ui.theme.LineFollowerSnamjulControllerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme = remember { mutableStateOf(false) }

            LineFollowerSnamjulControllerTheme (darkTheme = isDarkTheme.value) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    MainScreen(
                        isDarkTheme = isDarkTheme.value,
                        onThemeToggle = { isDarkTheme.value = !isDarkTheme.value }
                    )
                }
            }
        }
    }
}