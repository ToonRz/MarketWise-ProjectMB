package com.example.marketwiseproject.data.models

data class CryptoPrice(
    val symbol: String,
    val name: String,
    val price: Double,
    val change24h: Double,
    val changePercent24h: Double,
    val high24h: Double,
    val low24h: Double,
    val volume24h: Double,
    val marketCap: Double,
    val lastUpdate: Long = System.currentTimeMillis()
)

data class PriceUpdate(
    val symbol: String,
    val price: Double,
    val timestamp: Long
)