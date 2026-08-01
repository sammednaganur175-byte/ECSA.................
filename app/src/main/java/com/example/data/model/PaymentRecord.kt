package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payments")
data class PaymentRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val customerId: Int,
    val date: String, // YYYY-MM-DD
    val amount: Double,
    val paymentMode: String = "Cash", // "Cash", "UPI", "Bank Transfer"
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
