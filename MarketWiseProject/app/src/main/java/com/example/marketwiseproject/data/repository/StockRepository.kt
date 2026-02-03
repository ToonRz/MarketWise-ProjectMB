package com.example.marketwiseproject.data.repository


import com.example.marketwiseproject.data.api.FinnhubApi
import com.example.marketwiseproject.data.models.StockPrice

class StockRepository {

    private val finnhubApi = FinnhubApi.create()

    suspend fun getStockQuote(symbol: String): StockPrice? {
        return try {
            val quote = finnhubApi.getQuote(symbol)
            val profile = finnhubApi.getProfile(symbol)

            StockPrice(
                symbol = symbol,
                name = profile.name,
                price = quote.c,
                change = quote.d,
                changePercent = quote.dp,
                open = quote.o,
                high = quote.h,
                low = quote.l,
                volume = 0L,
                pe = null,
                eps = null,
                marketCap = profile.marketCapitalization?.toLong()
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}