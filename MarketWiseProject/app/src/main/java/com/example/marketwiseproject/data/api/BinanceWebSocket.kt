package com.example.marketwiseproject.data.api


import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.*
import org.json.JSONObject

class BinanceWebSocket {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    fun connectPriceStream(symbol: String): Flow<Double> = callbackFlow {
        val request = Request.Builder()
            .url("wss://stream.binance.com:9443/ws/${symbol.lowercase()}@trade")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    val price = json.getString("p").toDouble()
                    trySend(price)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                close(t)
            }
        })

        awaitClose {
            webSocket?.close(1000, "Flow closed")
        }
    }

    fun connectKlineStream(symbol: String, interval: String = "1m"): Flow<Kline> = callbackFlow {
        val request = Request.Builder()
            .url("wss://stream.binance.com:9443/ws/${symbol.lowercase()}@kline_$interval")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    val k = json.getJSONObject("k")
                    val kline = Kline(
                        openTime = k.getLong("t"),
                        open = k.getString("o").toDouble(),
                        high = k.getString("h").toDouble(),
                        low = k.getString("l").toDouble(),
                        close = k.getString("c").toDouble(),
                        volume = k.getString("v").toDouble(),
                        closeTime = k.getLong("T"),
                        isClosed = k.getBoolean("x")
                    )
                    trySend(kline)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                close(t)
            }
        })

        awaitClose {
            webSocket?.close(1000, "Flow closed")
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Disconnected")
    }
}

data class Kline(
    val openTime: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double,
    val volume: Double,
    val closeTime: Long,
    val isClosed: Boolean
)