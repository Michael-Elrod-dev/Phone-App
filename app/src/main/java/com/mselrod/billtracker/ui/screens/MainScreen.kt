package com.mselrod.billtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mselrod.billtracker.ui.components.CalendarView
import com.mselrod.billtracker.viewmodel.BillViewModel
import java.text.NumberFormat
import java.util.*

/**
 * Main screen displaying calendar, bills list, and totals
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: BillViewModel
) {
    val bills by viewModel.bills.collectAsState()
    val payDays by viewModel.payDays.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()
    val undoDeletedBill by viewModel.undoDeletedBill.collectAsState()

    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    // Calculate totals for each day
    val billTotals = bills.groupBy { it.day ?: -1 }
        .filterKeys { it != -1 }
        .mapValues { (_, bills) -> bills.sumOf { it.amount } }

    val payDayAmounts = payDays.groupBy { it.day }
        .mapValues { (_, paydays) -> paydays.sumOf { it.amount } }

    // Snackbar host for undo functionality
    val snackbarHostState = remember { SnackbarHostState() }

    // Show undo snackbar when bill is deleted
    LaunchedEffect(undoDeletedBill) {
        undoDeletedBill?.let { bill ->
            val result = snackbarHostState.showSnackbar(
                message = "${bill.name} deleted",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.clearUndo()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Calendar
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    CalendarView(
                        currentMonth = currentMonth,
                        onDaySelected = { viewModel.setSelectedDay(it) },
                        onPreviousMonth = { viewModel.navigatePreviousMonth() },
                        onNextMonth = { viewModel.navigateNextMonth() },
                        billTotals = billTotals,
                        payDayAmounts = payDayAmounts,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Upcoming Bills header
            item {
                Text(
                    text = "Upcoming Bills",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Totals section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TotalRow(
                            label = "Before Next Payday:",
                            amount = currencyFormatter.format(viewModel.calculateBillsBeforeNextPayDay()),
                            color = MaterialTheme.colorScheme.primary
                        )

                        TotalRow(
                            label = "Before Next 2 Paydays:",
                            amount = currencyFormatter.format(viewModel.calculateBillsBeforeNext2PayDays()),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun TotalRow(
    label: String,
    amount: String,
    color: androidx.compose.ui.graphics.Color,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = color
        )
    }
}
