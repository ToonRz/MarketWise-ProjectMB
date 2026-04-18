package com.example.marketwiseproject.data.repository

import com.example.marketwiseproject.data.api.BinanceWebSocket
import com.example.marketwiseproject.data.api.CoinGeckoApi
import com.example.marketwiseproject.data.models.CryptoPrice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Locale

class CryptoRepository(
    private val coinGeckoApi: CoinGeckoApi = CoinGeckoApi.create(),
    private val binanceWebSocket: BinanceWebSocket = BinanceWebSocket()
) {

    suspend fun getTopCryptos(): List<CryptoPrice> = withContext(Dispatchers.IO) {
        try {
            val markets = coinGeckoApi.getMarkets(perPage = 50)
            markets.map { market ->
                CryptoPrice(
                    id = market.id,
                    symbol = market.symbol.uppercase(),
                    name = market.name,
                    price = market.current_price ?: 0.0,
                    change24h = market.price_change_24h ?: 0.0,
                    changePercent24h = market.price_change_percentage_24h ?: 0.0,
                    high24h = market.high_24h ?: 0.0,
                    low24h = market.low_24h ?: 0.0,
                    volume24h = market.total_volume ?: 0.0,
                    marketCap = market.market_cap ?: 0.0,
                    image = market.image
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getCoinDetails(coinId: String): CryptoPrice? = withContext(Dispatchers.IO) {
        val cleanId = coinId.lowercase(Locale.US)
        try {
            // 1. Try specific ID query first
            val markets = coinGeckoApi.getMarkets(ids = cleanId)
            val result = markets.firstOrNull()?.let { market ->
                CryptoPrice(
                    id = market.id,
                    symbol = market.symbol.uppercase(),
                    name = market.name,
                    price = market.current_price ?: 0.0,
                    change24h = market.price_change_24h ?: 0.0,
                    changePercent24h = market.price_change_percentage_24h ?: 0.0,
                    high24h = market.high_24h ?: 0.0,
                    low24h = market.low_24h ?: 0.0,
                    volume24h = market.total_volume ?: 0.0,
                    marketCap = market.market_cap ?: 0.0,
                    image = market.image
                )
            }
            
            if (result != null) return@withContext result

            // 2. Fallback: search in top cryptos if specific query returns nothing
            val topCoins = getTopCryptos()
            topCoins.find { it.id.lowercase() == cleanId }
            
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getHistoricalPrices(coinId: String, days: Int = 30): List<List<Double>> = withContext(Dispatchers.IO) {
        try {
            val chart = coinGeckoApi.getMarketChart(coinId, days = days)
            chart.prices
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun getRealTimePrice(symbol: String): Flow<Double> {
        return binanceWebSocket.connectPriceStream(symbol)
    }

    fun disconnect() {
        binanceWebSocket.disconnect()
    }
}
