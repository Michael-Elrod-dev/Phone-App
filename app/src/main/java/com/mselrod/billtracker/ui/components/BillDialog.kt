package com.mselrod.billtracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mselrod.billtracker.data.entity.Bill

@Composable
fun BillDialog(
    bill: Bill?,
    onDismiss: () -> Unit,
    onSave: (name: String, amount: Double, day: Int, recurring: Boolean, autopay: Boolean) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var name by remember(bill) { mutableStateOf(bill?.name ?: "") }
    var amount by remember(bill) { mutableStateOf(bill?.amount?.toString() ?: "") }
    var day by remember(bill) { mutableStateOf(bill?.day?.toString() ?: "") }
    var recurring by remember(bill) { mutableStateOf(bill?.recurring ?: true) }
    var autopay by remember(bill) { mutableStateOf(bill?.autopay ?: false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }
    var dayError by remember { mutableStateOf<String?>(null) }

    val isEditMode = bill != null

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = if (isEditMode) "Edit Bill" else "Add Bill",
                    style = MaterialTheme.typography.headlineSmall
                )

                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                    },
                    label = { Text("Bill Name") },
                    isError = nameError != null,
                    supportingText = nameError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Amount field
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        amountError = null
                    },
                    label = { Text("Amount") },
                    isError = amountError != null,
                    supportingText = amountError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    prefix = { Text("$") },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Day field
                OutlinedTextField(
                    value = day,
                    onValueChange = {
                        day = it
                        dayError = null
                    },
                    label = { Text("Day of Month (1-31)") },
                    isError = dayError != null,
                    supportingText = dayError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )

                // Recurring checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recurring monthly",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Checkbox(
                        checked = recurring,
                        onCheckedChange = { recurring = it }
                    )
                }

                // AutoPay checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AutoPay enabled",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Checkbox(
                        checked = autopay,
                        onCheckedChange = { autopay = it }
                    )
                }

                // Save button
                Button(
                    onClick = {
                        var hasError = false

                        if (name.isBlank()) {
                            nameError = "Name is required"
                            hasError = true
                        }

                        val amountValue = amount.toDoubleOrNull()
                        if (amountValue == null || amountValue <= 0) {
                            amountError = "Enter a valid amount"
                            hasError = true
                        }

                        val dayValue = day.toIntOrNull()
                        if (dayValue == null || dayValue !in 1..31) {
                            dayError = "Enter a day between 1 and 31"
                            hasError = true
                        }

                        if (!hasError) {
                            onSave(name, amountValue!!, dayValue!!, recurring, autopay)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditMode) "Update Bill" else "Add Bill")
                }

                // Delete button
                if (isEditMode && onDelete != null) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete Bill")
                    }
                }

                // Cancel button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
