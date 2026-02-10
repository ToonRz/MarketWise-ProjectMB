package com.example.marketwiseproject.data.repository

import com.example.marketwiseproject.data.api.FinnhubApi
import com.example.marketwiseproject.data.models.StockDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class StockRepository(private val finnhubApi: FinnhubApi) {

    suspend fun getStockDetails(symbol: String): Result<StockDetails> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Get current time for candle request
                val calendar = Calendar.getInstance()
                val to = calendar.timeInMillis / 1000
                calendar.add(Calendar.YEAR, -1)
                val from = calendar.timeInMillis / 1000

                // 2. Fetch all data concurrently (if you use async/await)
                // For simplicity, we do sequential calls here.
                val quote = finnhubApi.getQuote(symbol)
                val profile = finnhubApi.getProfile(symbol)
                val candles = finnhubApi.getStockCandles(symbol, "D", from, to)
                val financials = finnhubApi.getBasicFinancials(symbol)

                // 3. Extract P/E ratio
                val peRatio = (financials.metric?.get("peNormalizedAnnual")) as? Double

                // 4. Combine into a single model
                val stockDetails = StockDetails(
                    symbol = profile.ticker ?: symbol,
                    name = profile.name ?: "N/A",
                    logoUrl = profile.logo ?: "",
                    currentPrice = quote.currentPrice ?: 0.0,
                    change = quote.change ?: 0.0,
                    percentChange = quote.percentChange ?: 0.0,
                    openPrice = quote.openPrice ?: 0.0,
                    highPrice = quote.highPrice ?: 0.0,
                    lowPrice = quote.lowPrice ?: 0.0,
                    previousClosePrice = quote.previousClosePrice ?: 0.0,
                    marketCap = profile.marketCapitalization ?: 0.0,
                    sharesOutstanding = profile.shareOutstanding ?: 0.0,
                    peRatio = peRatio,
                    volume = candles.volumes?.lastOrNull(),
                    candles = candles
                )
                Result.success(stockDetails)
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }
}
