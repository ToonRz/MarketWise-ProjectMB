package com.example.marketwiseproject.data.models

data class StockPrice(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: Double,
    val changePercent: Double,
    val open: Double,
    val high: Double,
    val low: Double,
    val volume: Long,
    val pe: Double?,
    val eps: Double?,
    val marketCap: Long?
)