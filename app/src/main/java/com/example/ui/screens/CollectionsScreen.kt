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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.sp
import com.example.ui.DairyViewModel
import com.example.ui.theme.DairyGreenLight
import com.example.ui.theme.DairyGreenPrimary

@Composable
fun CollectionsScreen(
    viewModel: DairyViewModel
) {
    val collections by viewModel.allCollections.collectAsState()
    val selectedMilkTypeFilter by viewModel.selectedMilkTypeFilter.collectAsState()

    val filteredCollections = collections.filter {
        if (selectedMilkTypeFilter == "All") true else it.milkType == selectedMilkTypeFilter
    }

    val totalMilkLt = filteredCollections.sumOf { it.quantityLiters }
    val avgFat = if (filteredCollections.isNotEmpty()) filteredCollections.map { it.fatPercentage }.average() else 0.0
    val avgSnf = if (filteredCollections.isNotEmpty()) filteredCollections.map { it.snfPercentage }.average() else 0.0

    Scaffold(
        floatingActionButton = {
            Button(
                onClick = { viewModel.showAddCollectionDialog.value = true },
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .height(52.dp)
                    .testTag("fab_add_collection"),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ADD COLLECTION", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DairyGreenPrimary)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Collections Log",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Filter Tabs: All, Cow, Buffalo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "Cow", "Buffalo").forEach { type ->
                            val isSelected = selectedMilkTypeFilter == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) Color.White else Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.selectedMilkTypeFilter.value = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    color = if (isSelected) DairyGreenPrimary else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }

            // Summary Bar Card matching photo ("Total Milk 740 Lt | Average Fat 4.5 | Average SNF 11")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = DairyGreenLight),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(14.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Milk", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text("${String.format("%.1f", totalMilkLt)} Lt", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                    }

                    Column {
                        Text("Average Fat", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(String.format("%.1f", avgFat), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                    }

                    Column {
                        Text("Average SNF", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(String.format("%.1f", avgSnf), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                    }
                }
            }

            // Data Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE0E0E0))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("S.No", modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Text("Date/Name", modifier = Modifier.weight(2.2f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Text("Type", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Text("Qty (Ltr)", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                Text("Amount", modifier = Modifier.weight(1.3f), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            }

            // Entries List
            if (filteredCollections.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No collection records found.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    itemsIndexed(filteredCollections) { index, entry ->
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${index + 1}", modifier = Modifier.weight(0.7f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)

                                Column(modifier = Modifier.weight(2.2f)) {
                                    Text(entry.customerName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text("${entry.date} (${entry.shift})", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }

                                Text(entry.milkType, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)

                                Text("${entry.quantityLiters} Lt", modifier = Modifier.weight(1.2f), fontWeight = FontWeight.Bold, color = DairyGreenPrimary, style = MaterialTheme.typography.bodyMedium)

                                Row(
                                    modifier = Modifier.weight(1.3f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("₹${String.format("%.0f", entry.totalAmount)}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    IconButton(
                                        onClick = { viewModel.deleteCollection(entry) },
                                        modifier = Modifier.size(24.dp).testTag("delete_collection_${entry.id}")
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}
