package com.example.marketwiseproject.data.repository

import com.example.marketwiseproject.data.api.BinanceWebSocket
import com.example.marketwiseproject.data.api.CoinGeckoApi
import com.example.marketwiseproject.data.models.CryptoPrice
import kotlinx.coroutines.flow.Flow

class CryptoRepository {

    private val coinGeckoApi = CoinGeckoApi.create()
    private val binanceWebSocket = BinanceWebSocket()

    suspend fun getTopCryptos(): List<CryptoPrice> {
        return try {
            val markets = coinGeckoApi.getMarkets(perPage = 50)
            markets.map { market ->
                CryptoPrice(
                    id = market.id,
                    symbol = market.symbol.uppercase(),
                    name = market.name,
                    price = market.current_price,
                    change24h = market.price_change_24h,
                    changePercent24h = market.price_change_percentage_24h,
                    high24h = market.high_24h,
                    low24h = market.low_24h,
                    volume24h = market.total_volume,
                    marketCap = market.market_cap
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // ✅ Return List<List<Double>> (ข้อมูลดิบจาก API)
    suspend fun getHistoricalPrices(coinId: String, days: Int = 30): List<List<Double>> {
        return try {
            val chart = coinGeckoApi.getMarketChart(coinId, days = days)
            chart.prices  // Return [[timestamp, price], ...]
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
