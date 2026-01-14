package com.mselrod.billtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mselrod.billtracker.data.entity.PayDay
import com.mselrod.billtracker.ui.components.PayDayDialog
import com.mselrod.billtracker.viewmodel.BillViewModel
import java.text.NumberFormat
import java.util.*

/**
 * Screen for configuring pay days and income
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PayDaysScreen(
    viewModel: BillViewModel
) {
    val payDays by viewModel.payDays.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingPayDay by remember { mutableStateOf<PayDay?>(null) }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingPayDay = null
                showAddDialog = true
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add pay day"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Configure Pay Days",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            if (payDays.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No pay days configured.\nTap + to add one!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = androidx.compose.ui.graphics.Color(0xFFCCCCCC)
                            )
                        }
                    }
                }
            } else {
                items(payDays, key = { it.id }) { payDay ->
                    PayDayCard(
                        payDay = payDay,
                        onEdit = {
                            editingPayDay = payDay
                            showAddDialog = true
                        }
                    )
                }
            }

        }
    }

    if (showAddDialog) {
        PayDayDialog(
            payDay = editingPayDay,
            onDismiss = {
                editingPayDay = null
            },
            onSave = { day, amount ->
                if (editingPayDay != null) {
                    viewModel.deletePayDay(editingPayDay!!)
                    viewModel.addPayDay(day, amount)
                } else {
                    viewModel.addPayDay(day, amount)
                }
                editingPayDay = null
            },
            onDelete = if (editingPayDay != null) {
                {
                    viewModel.deletePayDay(editingPayDay!!)
                    editingPayDay = null
                }
            } else null
        )
    }
}

@Composable
private fun PayDayCard(
    payDay: PayDay,
    onEdit: () -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Day ${payDay.day}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currencyFormatter.format(payDay.amount),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit pay day",
                    tint = androidx.compose.ui.graphics.Color(0xFFCCCCCC)
                )
            }
        }
    }
}