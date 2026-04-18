package com.example.marketwiseproject.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "price_alerts")
data class PriceAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String,
    val name: String,
    val targetPrice: Double,
    val isAbove: Boolean, // Notify if price goes above or below
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
