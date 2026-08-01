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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.DairyGreenLight
import com.example.ui.theme.DairyGreenPrimary

@Composable
fun AddCollectionDialog(
    viewModel: DairyViewModel,
    customers: List<Customer>,
    initialShift: String,
    initialDate: String,
    onDismiss: () -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(customers.firstOrNull()) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var date by remember { mutableStateOf(initialDate) }
    var shift by remember { mutableStateOf(initialShift) }
    var milkType by remember { mutableStateOf(selectedCustomer?.defaultMilkType ?: "Cow") }

    var quantityStr by remember { mutableStateOf("10.0") }
    var fatStr by remember { mutableStateOf("4.5") }
    var snfStr by remember { mutableStateOf("8.5") }
    var notes by remember { mutableStateOf("") }

    val qty = quantityStr.toDoubleOrNull() ?: 0.0
    val fat = fatStr.toDoubleOrNull() ?: 0.0
    val snf = snfStr.toDoubleOrNull() ?: 0.0

    val calculatedRate = remember(milkType, fat, snf, selectedCustomer) {
        viewModel.rateConfig.calculateRate(milkType, fat, snf, selectedCustomer?.fixedRate ?: 0.0)
    }
    val totalAmount = qty * calculatedRate

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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Collect Milk Entry",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DairyGreenPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_add_collection")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Customer Selector
                Text("Select Household / Customer", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCustomer?.let { "${it.name} (${it.code})" } ?: "No Customers Found",
                        onValueChange = {},
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { dropdownExpanded = true }
                            .testTag("customer_dropdown_field"),
                        enabled = false
                    )
                    Box(modifier = Modifier.matchParentSize().clickable { dropdownExpanded = true })
                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        customers.forEach { customer ->
                            DropdownMenuItem(
                                text = { Text("${customer.name} - ${customer.code} (${customer.defaultMilkType})") },
                                onClick = {
                                    selectedCustomer = customer
                                    milkType = customer.defaultMilkType
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Shift & Milk Type Row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Shift Selection
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
                                    Text(
                                        text = s,
                                        color = if (shift == s) Color.White else Color.Black,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }

                    // Milk Type Selection
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
                                    Text(
                                        text = t,
                                        color = if (milkType == t) Color.White else Color.Black,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quantity & FAT & SNF Inputs
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = { quantityStr = it },
                        label = { Text("Quantity (Ltr)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("quantity_input")
                    )

                    OutlinedTextField(
                        value = fatStr,
                        onValueChange = { fatStr = it },
                        label = { Text("FAT %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("fat_input")
                    )

                    OutlinedTextField(
                        value = snfStr,
                        onValueChange = { snfStr = it },
                        label = { Text("SNF %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("snf_input")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Auto-Calculated Rate Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = DairyGreenLight),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Rate / Liter", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("₹${String.format("%.2f", calculatedRate)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Amount", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("₹${String.format("%.2f", totalAmount)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Save Action
                Button(
                    onClick = {
                        selectedCustomer?.let { customer ->
                            if (qty > 0) {
                                viewModel.addCollection(
                                    customer = customer,
                                    date = date,
                                    shift = shift,
                                    milkType = milkType,
                                    quantity = qty,
                                    fat = fat,
                                    snf = snf,
                                    notes = notes
                                )
                            }
                        }
                    },
                    enabled = selectedCustomer != null && qty > 0,
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_collection_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary)
                ) {
                    Text("SAVE MILK COLLECTION", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
