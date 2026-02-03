package com.example.marketwiseproject.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntity(
    @PrimaryKey
    val symbol: String,
    val name: String,
    val type: String, // "CRYPTO" or "STOCK"
    val addedAt: Long = System.currentTimeMillis()
)