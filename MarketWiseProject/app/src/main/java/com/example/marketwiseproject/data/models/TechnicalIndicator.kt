package com.example.marketwiseproject.data.models


data class TechnicalIndicator(
    val rsi: Double,
    val macd: Double,
    val macdSignal: Double,
    val ma50: Double,
    val ma200: Double,
    val bollingerUpper: Double,
    val bollingerLower: Double,
    val signal: TradingSignal
)

enum class TradingSignal {
    STRONG_BUY,
    BUY,
    NEUTRAL,
    SELL,
    STRONG_SELL
}

data class CandlestickPattern(
    val name: String,
    val type: PatternType,
    val confidence: Double
)

enum class PatternType {
    HAMMER,
    DOJI,
    ENGULFING_BULLISH,
    ENGULFING_BEARISH,
    SHOOTING_STAR
}