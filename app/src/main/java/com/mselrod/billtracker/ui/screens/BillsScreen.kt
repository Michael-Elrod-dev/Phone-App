package com.mselrod.billtracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mselrod.billtracker.data.entity.Bill
import com.mselrod.billtracker.ui.components.BillCard
import com.mselrod.billtracker.ui.components.BillDialog
import com.mselrod.billtracker.viewmodel.BillViewModel

/**
 * Bills screen for managing all bills
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillsScreen(
    viewModel: BillViewModel
) {
    val bills by viewModel.bills.collectAsState()
    val undoDeletedBill by viewModel.undoDeletedBill.collectAsState()
    var showBillDialog by remember { mutableStateOf(false) }
    var editingBill by remember { mutableStateOf<Bill?>(null) }
    val autoPayBills = remember(bills) {
        bills.filter { it.autopay }.sortedByDescending { it.amount }
    }
    val nonAutoPayBills = remember(bills) {
        bills.filter { !it.autopay }.sortedByDescending { it.amount }
    }
    val snackbarHostState = remember { SnackbarHostState() }

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
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingBill = null
                showBillDialog = true
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add bill"
                )
            }
        },
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
            item {
                Text(
                    text = "All Bills (${bills.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bills list
            if (bills.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No bills yet.\nTap + to add one!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = androidx.compose.ui.graphics.Color(0xFFCCCCCC)
                            )
                        }
                    }
                }
            } else {
                // Autopay Bills Section
                if (autoPayBills.isNotEmpty()) {
                    item {
                        Text(
                            text = "Autopay Enabled",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    items(autoPayBills, key = { it.id }) { bill ->
                        BillCard(
                            bill = bill,
                            onEdit = {
                                editingBill = bill
                                showBillDialog = true
                            },
                            showAutopayTag = false
                        )
                    }
                }

                // Non-Autopay Bills Section
                if (nonAutoPayBills.isNotEmpty()) {
                    item {
                        Text(
                            text = "Manual Payment",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = if (autoPayBills.isNotEmpty()) 16.dp else 8.dp)
                        )
                    }
                    items(nonAutoPayBills, key = { it.id }) { bill ->
                        BillCard(
                            bill = bill,
                            onEdit = {
                                editingBill = bill
                                showBillDialog = true
                            },
                            showAutopayTag = false
                        )
                    }
                }
            }

        }
    }

    if (showBillDialog) {
        BillDialog(
            bill = editingBill,
            onDismiss = {
                editingBill = null
            },
            onSave = { name, amount, day, recurring, autopay ->
                if (editingBill != null) {
                    viewModel.updateBill(
                        editingBill!!.copy(
                            name = name,
                            amount = amount,
                            day = day,
                            recurring = recurring,
                            autopay = autopay
                        )
                    )
                } else {
                    viewModel.addBill(
                        name = name,
                        amount = amount,
                        day = day,
                        recurring = recurring,
                        autopay = autopay
                    )
                }
                editingBill = null
            },
            onDelete = if (editingBill != null) {
                {
                    viewModel.deleteBill(editingBill!!)
                    editingBill = null
                }
            } else null
        )
    }
}
