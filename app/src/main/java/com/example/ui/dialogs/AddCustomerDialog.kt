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
fun AddCustomerDialog(
    viewModel: DairyViewModel,
    onDismiss: () -> Unit
) {
    var code by remember { mutableStateOf("C${(101..999).random()}") }
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var defaultMilkType by remember { mutableStateOf("Cow") }
    var fixedRateStr by remember { mutableStateOf("0.0") }

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
                        text = "Register New Household",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DairyGreenPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_add_customer")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Customer Code") },
                    modifier = Modifier.fillMaxWidth().testTag("customer_code_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Household / Farmer Name *") },
                    modifier = Modifier.fillMaxWidth().testTag("customer_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth().testTag("customer_phone_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address / Village Sector") },
                    modifier = Modifier.fillMaxWidth().testTag("customer_address_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Default Milk Type", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    listOf("Cow", "Buffalo", "Mixed").forEach { t ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (defaultMilkType == t) DairyGreenPrimary else Color.LightGray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { defaultMilkType = t }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = t,
                                color = if (defaultMilkType == t) Color.White else Color.Black,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = fixedRateStr,
                    onValueChange = { fixedRateStr = it },
                    label = { Text("Fixed Rate Override (₹/Ltr, 0 for Rate Chart)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth().testTag("customer_fixed_rate_input")
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addCustomer(
                                code = code,
                                name = name,
                                phone = phone,
                                address = address,
                                defaultMilkType = defaultMilkType,
                                fixedRate = fixedRateStr.toDoubleOrNull() ?: 0.0
                            )
                        }
                    },
                    enabled = name.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(50.dp).testTag("save_customer_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DairyGreenPrimary)
                ) {
                    Text("REGISTER HOUSEHOLD", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
