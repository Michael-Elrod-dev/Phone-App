package com.mselrod.billtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Settings screen placeholder
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFFCCCCCC)
            )
        }
    }
}
