package com.example.marketwiseproject.data.api

import com.example.marketwiseproject.BuildConfig
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApi {

    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String = BuildConfig.FINNHUB_API_KEY
    ): FinnhubQuote

    @GET("stock/profile2")
    suspend fun getProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String = BuildConfig.FINNHUB_API_KEY
    ): FinnhubProfile

    @GET("stock/candle")
    suspend fun getStockCandles(
        @Query("symbol") symbol: String,
        @Query("resolution") resolution: String,
        @Query("from") from: Long,
        @Query("to") to: Long,
        @Query("token") token: String = BuildConfig.FINNHUB_API_KEY
    ): FinnhubCandles

    @GET("stock/metric")
    suspend fun getBasicFinancials(
        @Query("symbol") symbol: String,
        @Query("metric") metric: String = "all",
        @Query("token") token: String = BuildConfig.FINNHUB_API_KEY
    ): FinnhubBasicFinancials

    companion object {
        private const val BASE_URL = "https://finnhub.io/api/v1/"

        @Volatile
        private var INSTANCE: FinnhubApi? = null

        fun getInstance(): FinnhubApi {
            return INSTANCE ?: synchronized(this) {
                val instance = create()
                INSTANCE = instance
                instance
            }
        }

        private fun create(): FinnhubApi {
            val logger = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FinnhubApi::class.java)
        }
    }
}

data class FinnhubQuote(
    @SerializedName("c") val currentPrice: Double?,
    @SerializedName("d") val change: Double?,
    @SerializedName("dp") val percentChange: Double?,
    @SerializedName("h") val highPrice: Double?,
    @SerializedName("l") val lowPrice: Double?,
    @SerializedName("o") val openPrice: Double?,
    @SerializedName("pc") val previousClosePrice: Double?
)

data class FinnhubProfile(
    val name: String?,
    val ticker: String?,
    val logo: String?,
    val marketCapitalization: Double?,
    val shareOutstanding: Double?
)

data class FinnhubCandles(
    @SerializedName("c") val closePrices: List<Double>?,
    @SerializedName("h") val highPrices: List<Double>?,
    @SerializedName("l") val lowPrices: List<Double>?,
    @SerializedName("o") val openPrices: List<Double>?,
    @SerializedName("v") val volumes: List<Long>?,
    @SerializedName("t") val timestamps: List<Long>?,
    @SerializedName("s") val status: String?
)

data class FinnhubBasicFinancials(
    val metric: Map<String, Any>?
)
