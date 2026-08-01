package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.DairyViewModel
import com.example.ui.theme.DairyGreenLight
import com.example.ui.theme.DairyGreenPrimary
import com.example.ui.theme.MilkBlueAccent
import com.example.ui.theme.MilkBlueLight

@Composable
fun InventoryScreen(
    viewModel: DairyViewModel
) {
    val stockState by viewModel.inventoryStock.collectAsState()
    val dispatches by viewModel.allDispatches.collectAsState()

    Scaffold(
        floatingActionButton = {
            Button(
                onClick = { viewModel.showAddDispatchDialog.value = true },
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .height(52.dp)
                    .testTag("fab_record_dispatch"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("RECORD DISPATCH", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DairyGreenPrimary)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Real-Time Dairy Inventory Stock",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Track live milk stock level (Inflow vs Outflow / Sales)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Live Stock Hero Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MilkBlueLight),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MilkBlueAccent, modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Current Stock Balance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    text = "${String.format("%.1f", stockState.totalStock)} Lt",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DairyGreenPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Cow Milk Available", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                        Text("${String.format("%.1f", stockState.cowStock)} Lt", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                                    }
                                }

                                Card(
                                    modifier = Modifier.weight(1f),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Buffalo Milk Available", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                        Text("${String.format("%.1f", stockState.buffaloStock)} Lt", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Inflow Collected: ${String.format("%.1f", stockState.totalInflowLiters)} Lt", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                                Text("Total Dispatched Out: ${String.format("%.1f", stockState.totalOutflowLiters)} Lt", style = MaterialTheme.typography.labelSmall, color = Color.DarkGray)
                            }
                        }
                    }
                }

                // Dispatch History Title
                item {
                    Text(
                        text = "Milk Dispatches & Sales Log (Stock Outflows)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                if (dispatches.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No dispatches recorded yet.\nTap 'RECORD DISPATCH' when milk is sent out.", color = Color.Gray)
                        }
                    }
                } else {
                    items(dispatches) { dispatch ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(MilkBlueLight, shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MilkBlueAccent, modifier = Modifier.size(20.dp))
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(dispatch.buyerName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                        Text("${dispatch.date} (${dispatch.shift}) • ${dispatch.milkType}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("${dispatch.quantityLiters} Lt", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                                        Text("₹${String.format("%.2f", dispatch.totalAmount)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = { viewModel.deleteDispatch(dispatch) },
                                        modifier = Modifier.size(24.dp).testTag("delete_dispatch_${dispatch.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
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
