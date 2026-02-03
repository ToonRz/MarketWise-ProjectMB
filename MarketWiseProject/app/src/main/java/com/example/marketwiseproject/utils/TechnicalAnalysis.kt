package com.example.marketwiseproject.utils

import com.example.marketwiseproject.data.models.TechnicalIndicator
import com.example.marketwiseproject.data.models.TradingSignal
import kotlin.math.pow
import kotlin.math.sqrt

object TechnicalAnalysis {

    // RSI Calculation (Relative Strength Index)
    fun calculateRSI(prices: List<Double>, period: Int = 14): Double {
        if (prices.size < period + 1) return 50.0

        val changes = prices.zipWithNext { a, b -> b - a }
        val gains = changes.map { if (it > 0) it else 0.0 }
        val losses = changes.map { if (it < 0) -it else 0.0 }

        val avgGain = gains.takeLast(period).average()
        val avgLoss = losses.takeLast(period).average()

        if (avgLoss == 0.0) return 100.0

        val rs = avgGain / avgLoss
        return 100 - (100 / (1 + rs))
    }

    // MACD Calculation
    fun calculateMACD(prices: List<Double>): Triple<Double, Double, Double> {
        if (prices.size < 26) return Triple(0.0, 0.0, 0.0)

        val ema12 = calculateEMA(prices, 12)
        val ema26 = calculateEMA(prices, 26)
        val macd = ema12 - ema26

        // Signal line is 9-period EMA of MACD
        // Simplified: use current MACD value as signal
        val signal = macd * 0.9
        val histogram = macd - signal

        return Triple(macd, signal, histogram)
    }

    // EMA Calculation
    private fun calculateEMA(prices: List<Double>, period: Int): Double {
        if (prices.isEmpty()) return 0.0
        if (prices.size < period) return prices.average()

        val multiplier = 2.0 / (period + 1)
        var ema = prices.take(period).average()

        prices.drop(period).forEach { price ->
            ema = (price - ema) * multiplier + ema
        }

        return ema
    }

    // SMA (Simple Moving Average)
    fun calculateSMA(prices: List<Double>, period: Int): Double {
        if (prices.size < period) return prices.average()
        return prices.takeLast(period).average()
    }

    // Bollinger Bands
    fun calculateBollingerBands(prices: List<Double>, period: Int = 20): Triple<Double, Double, Double> {
        if (prices.size < period) {
            val avg = prices.average()
            return Triple(avg, avg, avg)
        }

        val sma = calculateSMA(prices, period)
        val recentPrices = prices.takeLast(period)
        val variance = recentPrices.map { (it - sma).pow(2) }.average()
        val stdDev = sqrt(variance)

        val upper = sma + (2 * stdDev)
        val lower = sma - (2 * stdDev)

        return Triple(upper, sma, lower)
    }

    // Generate Trading Signal
    fun generateSignal(
        rsi: Double,
        macd: Double,
        macdSignal: Double,
        price: Double,
        ma50: Double,
        ma200: Double
    ): TradingSignal {
        var bullishSignals = 0
        var bearishSignals = 0

        // RSI Signals
        when {
            rsi < 30 -> bullishSignals += 2
            rsi > 70 -> bearishSignals += 2
            rsi < 40 -> bullishSignals += 1
            rsi > 60 -> bearishSignals += 1
        }

        // MACD Signals
        if (macd > macdSignal) bullishSignals += 2 else bearishSignals += 2

        // Moving Average Signals
        if (price > ma50) bullishSignals += 1 else bearishSignals += 1
        if (ma50 > ma200) bullishSignals += 2 else bearishSignals += 2

        return when {
            bullishSignals >= 6 -> TradingSignal.STRONG_BUY
            bullishSignals >= 4 -> TradingSignal.BUY
            bearishSignals >= 6 -> TradingSignal.STRONG_SELL
            bearishSignals >= 4 -> TradingSignal.SELL
            else -> TradingSignal.NEUTRAL
        }
    }

    // Detect Candlestick Patterns
    fun detectHammer(open: Double, high: Double, low: Double, close: Double): Boolean {
        val body = kotlin.math.abs(close - open)
        val lowerShadow = minOf(open, close) - low
        val upperShadow = high - maxOf(open, close)

        return lowerShadow > body * 2 && upperShadow < body * 0.5
    }

    fun detectDoji(open: Double, high: Double, low: Double, close: Double): Boolean {
        val body = kotlin.math.abs(close - open)
        val range = high - low

        return body < range * 0.1
    }

    fun detectEngulfing(
        prevOpen: Double, prevClose: Double,
        currOpen: Double, currClose: Double
    ): String? {
        val prevBullish = prevClose > prevOpen
        val currBullish = currClose > currOpen

        return when {
            !prevBullish && currBullish &&
                    currOpen < prevClose && currClose > prevOpen -> "BULLISH_ENGULFING"
            prevBullish && !currBullish &&
                    currOpen > prevClose && currClose < prevOpen -> "BEARISH_ENGULFING"
            else -> null
        }
    }
}