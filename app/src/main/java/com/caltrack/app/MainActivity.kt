package com.caltrack.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.caltrack.app.ui.CalTrackViewModel
import com.caltrack.app.ui.CalTrackViewModelFactory
import com.caltrack.app.ui.components.BottomNavBar
import com.caltrack.app.ui.components.NavScreen
import com.caltrack.app.ui.screens.HistoryScreen
import com.caltrack.app.ui.screens.ProfileScreen
import com.caltrack.app.ui.screens.SnapAndLogScreen
import com.caltrack.app.ui.screens.TodayScreen
import com.caltrack.app.ui.theme.CalTrackBg
import com.caltrack.app.ui.theme.CalTrackTheme

class MainActivity : ComponentActivity() {

    private val viewModel: CalTrackViewModel by viewModels {
        val app = application as CalTrackApp
        val db = app.database
        CalTrackViewModelFactory(
            db.mealDao(),
            db.weightDao(),
            db.profileDao(),
            app.foodVisionService,
            app.apiKeyManager
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalTrackTheme {
                CalTrackMainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CalTrackMainContent(viewModel: CalTrackViewModel) {
    var currentScreen by remember { mutableStateOf(NavScreen.TODAY) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CalTrackBg),
        bottomBar = {
            BottomNavBar(
                currentScreen = currentScreen,
                onNavigate = { currentScreen = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                NavScreen.TODAY -> TodayScreen(
                    viewModel = viewModel,
                    onNavigateToSnapLog = { currentScreen = NavScreen.SNAP_LOG },
                    onNavigateToProfile = { currentScreen = NavScreen.PROFILE }
                )
                NavScreen.SNAP_LOG -> SnapAndLogScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = NavScreen.TODAY },
                    onNavigateToProfile = { currentScreen = NavScreen.PROFILE }
                )
                NavScreen.HISTORY -> HistoryScreen(
                    viewModel = viewModel,
                    onNavigateToProfile = { currentScreen = NavScreen.PROFILE }
                )
                NavScreen.PROFILE -> ProfileScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
