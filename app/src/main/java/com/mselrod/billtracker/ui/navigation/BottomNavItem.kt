package com.mselrod.billtracker.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Sealed class representing bottom navigation items
 * Designed to be scalable for adding more tabs in the future
 */
sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Home : BottomNavItem(
        route = "home",
        icon = Icons.Default.Home,
        label = "Home"
    )

    object Bills : BottomNavItem(
        route = "bills",
        icon = Icons.AutoMirrored.Filled.List,
        label = "Bills"
    )

    object PayDays : BottomNavItem(
        route = "payDays",
        icon = Icons.Default.DateRange,
        label = "Pay Days"
    )

    object Settings : BottomNavItem(
        route = "settings",
        icon = Icons.Default.Settings,
        label = "Settings"
    )

    companion object {
        val items = listOf(Home, Bills, PayDays, Settings)
    }
}
