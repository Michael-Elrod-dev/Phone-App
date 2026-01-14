package com.mselrod.billtracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    // Primary - terracotta orange for main actions
    primary = TerracottaOrange,
    onPrimary = TextPrimary,
    primaryContainer = CalendarDayWithBills,
    onPrimaryContainer = TerracottaOrange,

    // Secondary - for less prominent actions
    secondary = TextSecondary,
    onSecondary = AppBackground,

    // Tertiary - green for payday
    tertiary = PaydayGreen,
    onTertiary = TextPrimary,

    // Background
    background = AppBackground,
    onBackground = TextPrimary,

    // Surface - for cards and elevated components
    surface = CalendarDayNormal,
    onSurface = TextPrimary,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextTertiary,

    // Error - for bills and destructive actions
    error = ErrorRed,
    onError = TextPrimary,

    // Outline
    outline = BorderDark,
    outlineVariant = BorderMedium
)

@Composable
fun BillTrackerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = AppColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}