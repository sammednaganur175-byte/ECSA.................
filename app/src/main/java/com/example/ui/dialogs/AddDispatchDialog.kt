package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.DairyViewModel
import com.example.ui.theme.DairyGreenPrimary

@Composable
fun AddDispatchDialog(
    viewModel: DairyViewModel,
    initialDate: String,
    onDismiss: () -> Unit
) {
    var date by remember { mutableStateOf(initialDate) }
    var shift by remember { mutableStateOf("Morning") }
    var milkType by remember { mutableStateOf("Cow") }
    var quantityStr by remember { mutableStateOf("100.0") }
    var rateStr by remember { mutableStateOf("45.0") }
    var buyerName by remember { mutableStateOf("Main Chilling Center") }

    val qty = quantityStr.toDoubleOrNull() ?: 0.0
    val rate = rateStr.toDoubleOrNull() ?: 0.0
    val totalAmount = qty * rate

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Record Milk Dispatch (Outflow)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = DairyGreenPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_add_dispatch")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = buyerName,
                    onValueChange = { buyerName = it },
                    label = { Text("Buyer / Chilling Center / Destination *") },
                    modifier = Modifier.fillMaxWidth().testTag("buyer_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Shift & Milk Type Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Shift", style = MaterialTheme.typography.labelMedium)
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                            listOf("Morning", "Evening").forEach { s ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (shift == s) DairyGreenPrimary else Color.LightGray.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { shift = s }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(s, color = if (shift == s) Color.White else Color.Black, style = MaterialTheme.typography.labelSmall)
                                }
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Milk Type", style = MaterialTheme.typography.labelMedium)
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                            listOf("Cow", "Buffalo").forEach { t ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (milkType == t) DairyGreenPrimary else Color.LightGray.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { milkType = t }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(t, color = if (milkType == t) Color.White else Color.Black, style = MaterialTheme.typography.labelSmall)
                                }
                                Spacer(modifier = Modifier.width(2.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Dispatched Qty (Ltr)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("dispatch_qty_input")
                    )

                    OutlinedTextField(
                        value = rateStr,
                        onValueChange = { rateStr = it },
                        label = { Text("Sale Rate (₹/Ltr)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("dispatch_rate_input")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Total Dispatch Amount: ₹${String.format("%.2f", totalAmount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = DairyGreenPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (qty > 0) {
                            viewModel.addDispatch(
                                date = date,
                                shift = shift,
                                milkType = milkType,
                                quantity = qty,
                                rate = rate,
                                buyerName = buyerName
                            )
                        }
                    },
                    enabled = qty > 0,
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_dispatch_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary)
                ) {
                    Text("RECORD DISPATCH", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
