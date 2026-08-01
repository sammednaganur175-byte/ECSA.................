package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dispatches")
data class Dispatch(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // YYYY-MM-DD
    val shift: String, // "Morning", "Evening"
    val milkType: String, // "Cow", "Buffalo", "Mixed"
    val quantityLiters: Double,
    val ratePerLiter: Double,
    val buyerName: String, // e.g. "Main Chilling Center", "Retail Shop"
    val totalAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
