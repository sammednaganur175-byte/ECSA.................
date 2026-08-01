package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.DairyViewModel
import com.example.ui.dialogs.AddCollectionDialog
import com.example.ui.dialogs.AddCustomerDialog
import com.example.ui.dialogs.AddDispatchDialog
import com.example.ui.dialogs.AddPaymentDialog
import com.example.ui.dialogs.RateCalculatorDialog
import com.example.ui.screens.BillingScreen
import com.example.ui.screens.CollectionsScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.theme.DairyGreenLight
import com.example.ui.theme.DairyGreenPrimary
import com.example.ui.theme.DairyKhataTheme
import com.example.ui.theme.NavContainerColor
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

    private val viewModel: DairyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DairyKhataTheme {
                DairyAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DairyAppContent(viewModel: DairyViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val customers by viewModel.customers.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedShift by viewModel.selectedShift.collectAsState()
    val selectedCustomerForBill by viewModel.selectedCustomerForBill.collectAsState()

    val showAddCollection by viewModel.showAddCollectionDialog.collectAsState()
    val showAddCustomer by viewModel.showAddCustomerDialog.collectAsState()
    val showAddDispatch by viewModel.showAddDispatchDialog.collectAsState()
    val showAddPayment by viewModel.showAddPaymentDialog.collectAsState()
    val showRateCalc by viewModel.showRateCalculatorDialog.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = NavContainerColor,
                tonalElevation = 4.dp,
                modifier = Modifier.navigationBarsPadding().testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF001D36),
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = TextPrimary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = DairyGreenLight
                    ),
                    modifier = Modifier.testTag("nav_tab_home")
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.ListAlt, contentDescription = "Collections") },
                    label = { Text("Records", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF001D36),
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = TextPrimary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = DairyGreenLight
                    ),
                    modifier = Modifier.testTag("nav_tab_records")
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.People, contentDescription = "Households") },
                    label = { Text("Farmers", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF001D36),
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = TextPrimary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = DairyGreenLight
                    ),
                    modifier = Modifier.testTag("nav_tab_farmers")
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Bills") },
                    label = { Text("Bills", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF001D36),
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = TextPrimary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = DairyGreenLight
                    ),
                    modifier = Modifier.testTag("nav_tab_bills")
                )

                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = "Stock") },
                    label = { Text("Stock", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF001D36),
                        unselectedIconColor = TextSecondary,
                        selectedTextColor = TextPrimary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = DairyGreenLight
                    ),
                    modifier = Modifier.testTag("nav_tab_stock")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(viewModel = viewModel, onNavigateToTab = { selectedTab = it })
                1 -> CollectionsScreen(viewModel = viewModel)
                2 -> CustomersScreen(viewModel = viewModel, onNavigateToBillingForCustomer = { selectedTab = 3 })
                3 -> BillingScreen(viewModel = viewModel)
                4 -> InventoryScreen(viewModel = viewModel)
            }
        }

        // --- DIALOGS & OVERLAYS ---
        if (showAddCollection) {
            AddCollectionDialog(
                viewModel = viewModel,
                customers = customers,
                initialShift = selectedShift,
                initialDate = selectedDate,
                onDismiss = { viewModel.showAddCollectionDialog.value = false }
            )
        }

        if (showAddCustomer) {
            AddCustomerDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.showAddCustomerDialog.value = false }
            )
        }

        if (showAddDispatch) {
            AddDispatchDialog(
                viewModel = viewModel,
                initialDate = selectedDate,
                onDismiss = { viewModel.showAddDispatchDialog.value = false }
            )
        }

        if (showAddPayment) {
            AddPaymentDialog(
                viewModel = viewModel,
                customers = customers,
                initialCustomer = selectedCustomerForBill,
                initialDate = selectedDate,
                onDismiss = { viewModel.showAddPaymentDialog.value = false }
            )
        }

        if (showRateCalc) {
            RateCalculatorDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.showRateCalculatorDialog.value = false }
            )
        }
    }
}
