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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DairyViewModel
import com.example.ui.theme.DairyGreenAccent
import com.example.ui.theme.DairyGreenLight
import com.example.ui.theme.DairyGreenPrimary
import com.example.ui.theme.DairyGreenSecondary
import com.example.ui.theme.MilkBlueAccent
import com.example.ui.theme.MilkBlueLight

data class QuickActionItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badgeColor: Color,
    val onClick: () -> Unit
)

@Composable
fun DashboardScreen(
    viewModel: DairyViewModel,
    onNavigateToTab: (Int) -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedShift by viewModel.selectedShift.collectAsState()

    val todayMetrics by viewModel.todayMetrics.collectAsState()
    val inventoryStock by viewModel.inventoryStock.collectAsState()
    val collections by viewModel.allCollections.collectAsState()

    val recentCollections = collections.take(5)

    val quickActions = listOf(
        QuickActionItem(
            title = "Collect Milk",
            subtitle = "Add Entry",
            icon = Icons.Default.Opacity,
            badgeColor = DairyGreenPrimary,
            onClick = { viewModel.showAddCollectionDialog.value = true }
        ),
        QuickActionItem(
            title = "Households",
            subtitle = "Customers",
            icon = Icons.Default.People,
            badgeColor = DairyGreenSecondary,
            onClick = { onNavigateToTab(2) } // Customers tab
        ),
        QuickActionItem(
            title = "Rate Chart",
            subtitle = "FAT/SNF Calc",
            icon = Icons.Default.Calculate,
            badgeColor = MilkBlueAccent,
            onClick = { viewModel.showRateCalculatorDialog.value = true }
        ),
        QuickActionItem(
            title = "Milk Bills",
            subtitle = "Customer Statements",
            icon = Icons.Default.Receipt,
            badgeColor = Color(0xFFE65100),
            onClick = { onNavigateToTab(3) } // Billing tab
        ),
        QuickActionItem(
            title = "Real-Time Stock",
            subtitle = "Inventory & Dispatch",
            icon = Icons.Default.LocalShipping,
            badgeColor = Color(0xFF6A1B9A),
            onClick = { onNavigateToTab(4) } // Inventory tab
        ),
        QuickActionItem(
            title = "Record Payment",
            subtitle = "Customer Ledger",
            icon = Icons.Default.Payments,
            badgeColor = Color(0xFF2E7D32),
            onClick = { viewModel.showAddPaymentDialog.value = true }
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // --- 1. GREEN HEADER BANNER ---
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(DairyGreenPrimary, DairyGreenSecondary)
                        ),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DAIRY KHATA",
                                style = MaterialTheme.typography.labelSmall,
                                color = DairyGreenAccent,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "Milk Management",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Shift Switch Toggle
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.2f))
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf("Morning", "Evening").forEach { shiftName ->
                                val isSelected = selectedShift == shiftName
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) Color.White else Color.Transparent)
                                        .clickable { viewModel.setShift(shiftName) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = shiftName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) DairyGreenPrimary else Color.White
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date selector row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Collection Date: $selectedDate",
                            color = Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // --- TODAY'S METRICS CARDS (Total Milk, Avg FAT, Avg SNF) ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Total Milk Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Total Milk", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${String.format("%.1f", todayMetrics.totalMilkLt)} Lt",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DairyGreenPrimary
                                )
                            }
                        }

                        // Avg Fat Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Average Fat", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format("%.1f", todayMetrics.avgFat),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DairyGreenPrimary
                                )
                            }
                        }

                        // Avg SNF Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Average SNF", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format("%.1f", todayMetrics.avgSnf),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = DairyGreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. REAL-TIME INVENTORY STOCK CARD ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MilkBlueLight),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = MilkBlueAccent)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Real-Time Dairy Inventory", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Live Stock",
                            style = MaterialTheme.typography.labelSmall,
                            color = MilkBlueAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Cow Stock", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("${String.format("%.1f", inventoryStock.cowStock)} Lt", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                        }

                        Column {
                            Text("Buffalo Stock", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("${String.format("%.1f", inventoryStock.buffaloStock)} Lt", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Available", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text("${String.format("%.1f", inventoryStock.totalStock)} Lt", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold, color = MilkBlueAccent)
                        }
                    }
                }
            }
        }

        // --- 3. QUICK ACTION TILES GRID ---
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Quick Services",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.height(280.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    userScrollEnabled = false
                ) {
                    items(quickActions) { action ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { action.onClick() }
                                .testTag("quick_action_${action.title.lowercase().replace(" ", "_")}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(action.badgeColor.copy(alpha = 0.15f), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(action.icon, contentDescription = null, tint = action.badgeColor, modifier = Modifier.size(22.dp))
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column {
                                    Text(action.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                    Text(action.subtitle, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 4. RECENT MILK COLLECTION ENTRIES ---
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Collections", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelLarge,
                        color = DairyGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToTab(1) } // Collections tab
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        if (recentCollections.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No milk collections logged today yet.\nTap '+ Collect Milk' to add an entry.", color = Color.Gray)
                }
            }
        } else {
            items(recentCollections) { entry ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(DairyGreenLight, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = entry.milkType.take(1),
                                    fontWeight = FontWeight.Bold,
                                    color = DairyGreenPrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(entry.customerName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                Text("${entry.date} | ${entry.shift} | FAT: ${entry.fatPercentage} SNF: ${entry.snfPercentage}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("${entry.quantityLiters} Lt", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = DairyGreenPrimary)
                            Text("₹${String.format("%.2f", entry.totalAmount)}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                        }
                    }
                }
            }
        }
    }
}
