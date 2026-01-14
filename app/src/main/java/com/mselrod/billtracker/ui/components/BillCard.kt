package com.mselrod.billtracker.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mselrod.billtracker.data.entity.Bill
import java.text.NumberFormat
import java.util.Locale

/**
 * Converts a number to its ordinal string (1st, 2nd, 3rd, etc.)
 */
private fun Int.toOrdinal(): String {
    return when {
        this % 100 in 11..13 -> "${this}th"
        this % 10 == 1 -> "${this}st"
        this % 10 == 2 -> "${this}nd"
        this % 10 == 3 -> "${this}rd"
        else -> "${this}th"
    }
}

/**
 * Card component for displaying a single bill
 */
@Composable
fun BillCard(
    bill: Bill,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    showAutopayTag: Boolean = true
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US)

    Card(
        modifier = modifier.fillMaxWidth(),
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
                    text = bill.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currencyFormatter.format(bill.amount),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (bill.recurring && bill.day != null) {
                        Text(
                            text = "Recurring - ${bill.day.toOrdinal()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = androidx.compose.ui.graphics.Color(0xFFCCCCCC)
                        )
                    } else if (bill.date != null) {
                        Text(
                            text = "One-time - ${bill.date}",
                            style = MaterialTheme.typography.bodySmall,
                            color = androidx.compose.ui.graphics.Color(0xFFCCCCCC)
                        )
                    }

                    if (bill.autopay && showAutopayTag) {
                        if (bill.recurring || bill.date != null) {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodySmall,
                                color = androidx.compose.ui.graphics.Color(0xFFCCCCCC)
                            )
                        }
                        Text(
                            text = "AutoPay",
                            style = MaterialTheme.typography.bodySmall,
                            color = androidx.compose.ui.graphics.Color(0xFFCCCCCC)
                        )
                    }
                }
            }

            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit bill",
                    tint = androidx.compose.ui.graphics.Color(0xFFCCCCCC)
                )
            }
        }
    }
}
