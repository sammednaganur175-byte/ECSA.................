package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.DairyViewModel
import com.example.ui.theme.DairyGreenLight
import com.example.ui.theme.DairyGreenPrimary
import com.example.ui.theme.MilkBlueAccent
import com.example.ui.theme.WarningAmberLight

@Composable
fun BillingScreen(
    viewModel: DairyViewModel
) {
    val context = LocalContext.current
    val customers by viewModel.customers.collectAsState()
    val selectedCustomer by viewModel.selectedCustomerForBill.collectAsState()
    val startDate by viewModel.billingStartDate.collectAsState()
    val endDate by viewModel.billingEndDate.collectAsState()
    val activeBill by viewModel.activeBillSummary.collectAsState()

    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DairyGreenPrimary)
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Household Milk Bill & Statement",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Customer Selection Dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = selectedCustomer?.let { "${it.name} (${it.code})" } ?: "Select Household...",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White) },
                        modifier = Modifier.fillMaxWidth().clickable { dropdownExpanded = true }.testTag("billing_customer_select"),
                        enabled = false,
                        shape = RoundedCornerShape(12.dp)
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
                                    viewModel.selectedCustomerForBill.value = cust
                                    viewModel.generateBillForCustomer(cust, startDate, endDate)
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Date Range Bar
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {
                            viewModel.billingStartDate.value = it
                            selectedCustomer?.let { cust -> viewModel.generateBillForCustomer(cust, it, endDate) }
                        },
                        label = { Text("From Date (YYYY-MM-DD)", color = Color.White.copy(alpha = 0.8f)) },
                        modifier = Modifier.weight(1f).testTag("billing_start_date"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = endDate,
                        onValueChange = {
                            viewModel.billingEndDate.value = it
                            selectedCustomer?.let { cust -> viewModel.generateBillForCustomer(cust, startDate, it) }
                        },
                        label = { Text("To Date (YYYY-MM-DD)", color = Color.White.copy(alpha = 0.8f)) },
                        modifier = Modifier.weight(1f).testTag("billing_end_date"),
                        singleLine = true
                    )
                }
            }
        }

        if (selectedCustomer == null || activeBill == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Select a household/customer above to view or generate their itemized milk bill statement.", color = Color.Gray)
            }
        } else {
            val bill = activeBill!!

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Bill Summary Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(bill.customer.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                                    Text("Statement Period: ${bill.startDate} to ${bill.endDate}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }

                                Button(
                                    onClick = {
                                        viewModel.showAddPaymentDialog.value = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pay", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider()
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Total Milk Supplied", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text("${String.format("%.1f", bill.totalQuantityLt)} Lt", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }

                                Column {
                                    Text("Avg FAT / SNF", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text("${String.format("%.1f", bill.avgFat)} / ${String.format("%.1f", bill.avgSnf)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Gross Milk Bill", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    Text("₹${String.format("%.2f", bill.totalGrossBill)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DairyGreenLight, shape = RoundedCornerShape(12.dp))
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Payments Received: ₹${String.format("%.2f", bill.totalPayments)}", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                                    Text("Net Balance Payable / Due", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    text = "₹${String.format("%.2f", bill.netBalanceDue)}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (bill.netBalanceDue > 0) Color(0xFFD32F2F) else DairyGreenPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Share Statement Button
                            OutlinedButton(
                                onClick = {
                                    val statementText = buildString {
                                        appendLine("🥛 DAIRY KHATA - MILK STATEMENT 🥛")
                                        appendLine("Customer: ${bill.customer.name} (${bill.customer.code})")
                                        appendLine("Period: ${bill.startDate} to ${bill.endDate}")
                                        appendLine("--------------------------------")
                                        appendLine("Total Quantity: ${String.format("%.1f", bill.totalQuantityLt)} Liters")
                                        appendLine("Average FAT: ${String.format("%.1f", bill.avgFat)} %")
                                        appendLine("Average SNF: ${String.format("%.1f", bill.avgSnf)} %")
                                        appendLine("Gross Bill Amount: ₹${String.format("%.2f", bill.totalGrossBill)}")
                                        appendLine("Total Paid: ₹${String.format("%.2f", bill.totalPayments)}")
                                        appendLine("Net Balance Due: ₹${String.format("%.2f", bill.netBalanceDue)}")
                                        appendLine("--------------------------------")
                                        appendLine("Thank you for your business!")
                                    }

                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, "Milk Bill Statement - ${bill.customer.name}")
                                        putExtra(Intent.EXTRA_TEXT, statementText)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Milk Bill Statement"))
                                },
                                modifier = Modifier.fillMaxWidth().testTag("share_bill_statement"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SHARE / COPY BILL STATEMENT TEXT")
                            }
                        }
                    }
                }

                // Itemized Collections Header
                item {
                    Text(
                        text = "Itemized Daily Collections (${bill.collections.size} entries)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                itemsIndexed(bill.collections) { index, entry ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${entry.date} (${entry.shift})", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("Type: ${entry.milkType} | FAT: ${entry.fatPercentage}% | SNF: ${entry.snfPercentage}%", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("${entry.quantityLiters} Lt @ ₹${entry.ratePerLiter}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("₹${String.format("%.2f", entry.totalAmount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
