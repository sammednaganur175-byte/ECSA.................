package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val code: String, // e.g. "C101"
    val name: String, // e.g. "Bahubali", "Katappa", "Shivgami", "Devsena"
    val phone: String = "",
    val address: String = "",
    val defaultMilkType: String = "Cow", // "Cow", "Buffalo", "Mixed"
    val fixedRate: Double = 0.0, // 0.0 for Rate Chart calculation, or > 0 for flat rate
    val initialBalance: Double = 0.0, // Existing due or advance balance
    val createdAt: Long = System.currentTimeMillis()
)
