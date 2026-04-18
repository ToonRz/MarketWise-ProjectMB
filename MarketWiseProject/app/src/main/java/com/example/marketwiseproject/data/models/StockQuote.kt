package com.example.marketwiseproject.data.models

data class StockQuote(
    val symbol: String,
    val name: String,
    val currentPrice: Double,
    val change: Double,
    val percentChange: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val openPrice: Double,
    val previousClosePrice: Double,
    val logoUrl: String? = null
)

