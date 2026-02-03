package com.example.marketwiseproject.data.api


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface FinnhubApi {

    @GET("quote")
    suspend fun getQuote(
        @Query("symbol") symbol: String,
        @Query("token") token: String = "d612gj1r01qjrrugmu10d612gj1r01qjrrugmu1g"
    ): FinnhubQuote

    @GET("stock/profile2")
    suspend fun getProfile(
        @Query("symbol") symbol: String,
        @Query("token") token: String = "d612gj1r01qjrrugmu10d612gj1r01qjrrugmu1g"
    ): FinnhubProfile

    companion object {
        private const val BASE_URL = "https://finnhub.io/api/v1/"

        fun create(): FinnhubApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FinnhubApi::class.java)
        }
    }
}

data class FinnhubQuote(
    val c: Double, // Current price
    val d: Double, // Change
    val dp: Double, // Percent change
    val h: Double, // High
    val l: Double, // Low
    val o: Double, // Open
    val pc: Double // Previous close
)

data class FinnhubProfile(
    val name: String,
    val ticker: String,
    val marketCapitalization: Double?,
    val shareOutstanding: Double?
)