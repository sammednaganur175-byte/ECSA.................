package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.DairyViewModel
import com.example.ui.theme.DairyGreenLight
import com.example.ui.theme.DairyGreenPrimary

@Composable
fun CustomersScreen(
    viewModel: DairyViewModel,
    onNavigateToBillingForCustomer: (Int) -> Unit
) {
    val customers by viewModel.customers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredCustomers = customers.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.code.contains(searchQuery, ignoreCase = true) ||
        it.phone.contains(searchQuery)
    }

    Scaffold(
        floatingActionButton = {
            Button(
                onClick = { viewModel.showAddCustomerDialog.value = true },
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .height(52.dp)
                    .testTag("fab_register_household"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("REGISTER HOUSEHOLD", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DairyGreenPrimary)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Registered Households & Farmers",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by name or code...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = DairyGreenPrimary) },
                        modifier = Modifier.fillMaxWidth().testTag("search_customer_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredCustomers.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No household records found.\nTap 'REGISTER HOUSEHOLD' to add.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp, top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredCustomers) { customer ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(DairyGreenLight, shape = CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = customer.name.take(1).uppercase(),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = DairyGreenPrimary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Text("Code: ${customer.code} • ${customer.defaultMilkType} Milk", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                                        }
                                    }

                                    if (customer.fixedRate > 0.0) {
                                        Box(
                                            modifier = Modifier
                                                .background(DairyGreenLight, shape = RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text("₹${customer.fixedRate}/Lt", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                                        }
                                    }
                                }

                                if (customer.phone.isNotBlank() || customer.address.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (customer.phone.isNotBlank()) {
                                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(customer.phone, style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                                            Spacer(modifier = Modifier.width(16.dp))
                                        }
                                        if (customer.address.isNotBlank()) {
                                            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(customer.address, style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.showAddCollectionDialog.value = true
                                        },
                                        modifier = Modifier.weight(1f).testTag("customer_collect_milk_${customer.id}"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Opacity, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Collect Milk", style = MaterialTheme.typography.labelMedium)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.selectedCustomerForBill.value = customer
                                            viewModel.generateBillForCustomer(customer, viewModel.billingStartDate.value, viewModel.billingEndDate.value)
                                            onNavigateToBillingForCustomer(customer.id)
                                        },
                                        modifier = Modifier.weight(1f).testTag("customer_view_bill_${customer.id}"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary)
                                    ) {
                                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("View Milk Bill", style = MaterialTheme.typography.labelMedium, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
