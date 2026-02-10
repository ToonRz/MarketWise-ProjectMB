package com.example.marketwiseproject.data.models

import com.example.marketwiseproject.data.api.FinnhubCandles

// Represents the combined data for the stock detail screen
data class StockDetails(
    val symbol: String,
    val name: String,
    val logoUrl: String,
    val currentPrice: Double,
    val change: Double,
    val percentChange: Double,
    val openPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val previousClosePrice: Double,
    val marketCap: Double,
    val sharesOutstanding: Double,
    val peRatio: Double? = null, // P/E can sometimes be null
    val volume: Long? = null, // Volume from candles
    val candles: FinnhubCandles
)
