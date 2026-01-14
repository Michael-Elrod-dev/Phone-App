package com.mselrod.billtracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mselrod.billtracker.data.entity.PayDay

@Composable
fun PayDayDialog(
    payDay: PayDay?,
    onDismiss: () -> Unit,
    onSave: (day: Int, amount: Double) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var day by remember(payDay) { mutableStateOf(payDay?.day?.toString() ?: "") }
    var amount by remember(payDay) { mutableStateOf(payDay?.amount?.toString() ?: "") }

    var dayError by remember { mutableStateOf<String?>(null) }
    var amountError by remember { mutableStateOf<String?>(null) }

    val isEditMode = payDay != null

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
                    text = if (isEditMode) "Edit Pay Day" else "Add Pay Day",
                    style = MaterialTheme.typography.headlineSmall
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

                // Save button
                Button(
                    onClick = {
                        // Validate inputs
                        var hasError = false

                        val dayValue = day.toIntOrNull()
                        if (dayValue == null || dayValue !in 1..31) {
                            dayError = "Enter a day between 1 and 31"
                            hasError = true
                        }

                        val amountValue = amount.toDoubleOrNull()
                        if (amountValue == null || amountValue <= 0) {
                            amountError = "Enter a valid amount"
                            hasError = true
                        }

                        if (!hasError) {
                            onSave(dayValue!!, amountValue!!)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isEditMode) "Update Pay Day" else "Add Pay Day")
                }

                // Delete button (only in edit mode)
                if (isEditMode && onDelete != null) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete Pay Day")
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
