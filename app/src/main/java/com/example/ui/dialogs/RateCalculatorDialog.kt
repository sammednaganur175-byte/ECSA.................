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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.DairyGreenLight
import com.example.ui.theme.DairyGreenPrimary

@Composable
fun RateCalculatorDialog(
    viewModel: DairyViewModel,
    onDismiss: () -> Unit
) {
    var milkType by remember { mutableStateOf("Cow") }
    var fatStr by remember { mutableStateOf("4.5") }
    var snfStr by remember { mutableStateOf("8.5") }

    val fat = fatStr.toDoubleOrNull() ?: 0.0
    val snf = snfStr.toDoubleOrNull() ?: 0.0

    val calculatedRate = remember(milkType, fat, snf) {
        viewModel.rateConfig.calculateRate(milkType, fat, snf)
    }

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
                        text = "Rate Chart Calculator",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DairyGreenPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_rate_calc")) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Text(
                    text = "Calculate price per liter based on FAT & SNF quality parameters.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Milk Type", style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                    listOf("Cow", "Buffalo").forEach { type ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (milkType == type) DairyGreenPrimary else Color.LightGray.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    milkType = type
                                    if (type == "Buffalo" && fatStr == "4.5") fatStr = "6.5"
                                    if (type == "Cow" && fatStr == "6.5") fatStr = "4.5"
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                color = if (milkType == type) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = fatStr,
                        onValueChange = { fatStr = it },
                        label = { Text("FAT %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("calc_fat_input")
                    )

                    OutlinedTextField(
                        value = snfStr,
                        onValueChange = { snfStr = it },
                        label = { Text("SNF %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f).testTag("calc_snf_input")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = DairyGreenLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Calculated Milk Rate", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "₹ ${String.format("%.2f", calculatedRate)} / Ltr",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = DairyGreenPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Standard $milkType Base: ${if (milkType == "Cow") "3.5 FAT / 8.5 SNF @ ₹38" else "6.0 FAT / 9.0 SNF @ ₹55"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}
