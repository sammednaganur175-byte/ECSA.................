package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milk_collections")
data class MilkCollection(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val customerName: String,
    val date: String, // YYYY-MM-DD
    val shift: String, // "Morning", "Evening"
    val milkType: String, // "Cow", "Buffalo", "Mixed"
    val quantityLiters: Double,
    val fatPercentage: Double,
    val snfPercentage: Double,
    val ratePerLiter: Double,
    val totalAmount: Double,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
