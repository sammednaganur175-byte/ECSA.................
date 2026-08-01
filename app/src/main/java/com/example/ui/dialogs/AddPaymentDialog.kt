package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import com.example.data.model.Customer
import com.example.ui.DairyViewModel
import com.example.ui.theme.DairyGreenPrimary

@Composable
fun AddPaymentDialog(
    viewModel: DairyViewModel,
    customers: List<Customer>,
    initialCustomer: Customer?,
    initialDate: String,
    onDismiss: () -> Unit
) {
    var selectedCustomer by remember { mutableStateOf(initialCustomer ?: customers.firstOrNull()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var date by remember { mutableStateOf(initialDate) }
    var amountStr by remember { mutableStateOf("") }
    var paymentMode by remember { mutableStateOf("Cash") }
    var notes by remember { mutableStateOf("") }

    val amt = amountStr.toDoubleOrNull() ?: 0.0

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
                        text = "Record Customer Payment",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DairyGreenPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_add_payment")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Customer", style = MaterialTheme.typography.labelMedium)
                Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    OutlinedTextField(
                        value = selectedCustomer?.let { "${it.name} (${it.code})" } ?: "Select Customer",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().clickable { dropdownExpanded = true },
                        enabled = false
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { dropdownExpanded = true })
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        customers.forEach { cust ->
                            DropdownMenuItem(
                                text = { Text("${cust.name} (${cust.code})") },
                                onClick = {
                                    selectedCustomer = cust
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Payment Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("payment_amount_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Payment Reference / Receipt Note") },
                    modifier = Modifier.fillMaxWidth().testTag("payment_notes_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        selectedCustomer?.let { cust ->
                            if (amt > 0) {
                                viewModel.addPayment(
                                    customerId = cust.id,
                                    date = date,
                                    amount = amt,
                                    mode = paymentMode,
                                    notes = notes
                                )
                            }
                        }
                    },
                    enabled = selectedCustomer != null && amt > 0,
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_payment_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary)
                ) {
                    Text("RECORD PAYMENT", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
