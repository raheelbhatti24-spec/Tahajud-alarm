package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import com.example.ui.MainViewModel
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.MidnightBackground

@Composable
fun MainContainer(viewModel: MainViewModel) {
    val isBlackoutModeActive by viewModel.isBlackoutModeActive.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                if (!isBlackoutModeActive) {
                    NavigationBar(
                        containerColor = Color(0xFF0F172A),
                        contentColor = Color.White,
                        modifier = Modifier.testTag("navigation_bar")
                    ) {
                        NavigationBarItem(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            icon = { Icon(Icons.Default.Bedtime, contentDescription = "Home") },
                            label = { Text("Home", fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = EmeraldPrimary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            ),
                            modifier = Modifier.testTag("nav_home")
                        )

                        NavigationBarItem(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                            label = { Text("Settings", fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = EmeraldPrimary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            ),
                            modifier = Modifier.testTag("nav_settings")
                        )

                        NavigationBarItem(
                            selected = selectedTabIndex == 2,
                            onClick = { selectedTabIndex = 2 },
                            icon = { Icon(Icons.Default.Book, contentDescription = "Guide") },
                            label = { Text("Guide", fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = EmeraldPrimary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            ),
                            modifier = Modifier.testTag("nav_guide")
                        )

                        NavigationBarItem(
                            selected = selectedTabIndex == 3,
                            onClick = { selectedTabIndex = 3 },
                            icon = { Icon(Icons.Default.History, contentDescription = "Logs") },
                            label = { Text("Logs", fontWeight = if (selectedTabIndex == 3) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = EmeraldPrimary,
                                indicatorColor = EmeraldPrimary,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            ),
                            modifier = Modifier.testTag("nav_logs")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (selectedTabIndex) {
                    0 -> DashboardScreen(
                        viewModel = viewModel,
                        onEnterBlackoutRequested = { viewModel.setBlackoutActive(true) }
                    )
                    1 -> SettingsScreen(viewModel = viewModel)
                    2 -> TahajjudGuideScreen()
                    3 -> HistoryScreen(viewModel = viewModel)
                }
            }
        }

        // Overlay Pitch-Black OLED Screen when Blackout Mode is Active
        AnimatedVisibility(
            visible = isBlackoutModeActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            BlackoutScreen(
                viewModel = viewModel,
                onExitBlackout = { viewModel.setBlackoutActive(false) }
            )
        }
    }
}
