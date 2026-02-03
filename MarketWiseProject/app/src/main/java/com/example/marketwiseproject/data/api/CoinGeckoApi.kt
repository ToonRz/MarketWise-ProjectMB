package com.example.marketwiseproject.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CoinGeckoApi {

    @GET("coins/markets")
    suspend fun getMarkets(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 100,
        @Query("page") page: Int = 1
    ): List<CoinGeckoMarket>

    @GET("coins/{id}/market_chart")
    suspend fun getMarketChart(
        @Path("id") id: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: Int = 30
    ): MarketChartResponse

    companion object {
        private const val BASE_URL = "https://api.coingecko.com/api/v3/"

        fun create(): CoinGeckoApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CoinGeckoApi::class.java)
        }
    }
}

data class CoinGeckoMarket(
    val id: String,
    val symbol: String,
    val name: String,
    val current_price: Double,
    val price_change_24h: Double,
    val price_change_percentage_24h: Double,
    val high_24h: Double,
    val low_24h: Double,
    val total_volume: Double,
    val market_cap: Double
)

data class MarketChartResponse(
    val prices: List<List<Double>>  // ✅ [[timestamp, price], ...]
)