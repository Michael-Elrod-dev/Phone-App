package com.mselrod.billtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mselrod.billtracker.ui.theme.CalendarDayNormal
import com.mselrod.billtracker.ui.theme.CalendarDayWithBills
import com.mselrod.billtracker.ui.theme.CalendarBorder
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

/**
 * Calendar view component for displaying and selecting dates
 */
@Composable
fun CalendarView(
    currentMonth: YearMonth,
    onDaySelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    billTotals: Map<Int, Double> = emptyMap(),
    payDayAmounts: Map<Int, Double> = emptyMap(),
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Month header with navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous month"
                )
            }

            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onNextMonth) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next month"
                )
            }
        }

        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFCCCCCC)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val firstDayOfMonth = currentMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 = Sunday
        val daysInMonth = currentMonth.lengthOfMonth()
        val totalCells = firstDayOfWeek + daysInMonth
        val numberOfRows = (totalCells + 6) / 7
        val rowHeight = 70.dp
        val gridHeight = rowHeight * numberOfRows + 16.dp

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight),
            contentPadding = PaddingValues(4.dp),
            userScrollEnabled = false
        ) {
            items(totalCells) { index ->
                if (index < firstDayOfWeek) {
                    Spacer(modifier = Modifier.aspectRatio(1f))
                } else {
                    val day = index - firstDayOfWeek + 1
                    val date = currentMonth.atDay(day)
                    val isToday = date == LocalDate.now()
                    val billTotal = billTotals[day] ?: 0.0
                    val payDayAmount = payDayAmounts[day] ?: 0.0

                    DayCell(
                        day = day,
                        isToday = isToday,
                        billTotal = billTotal,
                        payDayAmount = payDayAmount,
                        onClick = { onDaySelected(date) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    day: Int,
    isToday: Boolean,
    billTotal: Double,
    payDayAmount: Double,
    onClick: () -> Unit
) {
    val hasBills = billTotal > 0
    val backgroundColor = when {
        hasBills -> CalendarDayWithBills
        else -> CalendarDayNormal
    }
    val borderColor = when {
        isToday -> Color.White
        hasBills -> CalendarBorder
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .height(68.dp)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .then(
                if (borderColor != Color.Transparent) {
                    Modifier.border(2.dp, borderColor, RoundedCornerShape(8.dp))
                } else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 4.dp, horizontal = 2.dp)
        ) {
            // Day number at top
            Text(
                text = day.toString(),
                color = when {
                    hasBills -> CalendarBorder
                    isToday -> Color.White
                    else -> Color(0xFFCCCCCC)
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (hasBills || isToday) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 2.dp)
            )

            // Bill total in center (if any)
            if (billTotal > 0) {
                Text(
                    text = "$${billTotal.toInt()}",
                    color = CalendarBorder,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Payday amount at absolute bottom (if any)
            if (payDayAmount > 0) {
                Text(
                    text = "$${payDayAmount.toInt()}",
                    color = Color(0xFF2E7D32),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp)
                )
            }
        }
    }
}
