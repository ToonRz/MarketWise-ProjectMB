package com.example.marketwiseproject.ui.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketwiseproject.data.api.FinnhubApi
import com.example.marketwiseproject.data.models.StockQuote
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.random.Random

class StockListViewModel : ViewModel() {

    private val finnhubApi = FinnhubApi.getInstance()

    // Popular US stocks to display
    private val popularStocks = listOf(
        Pair("AAPL", "Apple Inc."),
        Pair("MSFT", "Microsoft Corp."),
        Pair("GOOGL", "Alphabet Inc."),
        Pair("AMZN", "Amazon.com Inc."),
        Pair("TSLA", "Tesla Inc."),
        Pair("NVDA", "NVIDIA Corp."),
        Pair("META", "Meta Platforms Inc."),
        Pair("NFLX", "Netflix Inc."),
        Pair("DIS", "Walt Disney Co."),
        Pair("PYPL", "PayPal Holdings"),
        Pair("AMD", "Advanced Micro Devices"),
        Pair("INTC", "Intel Corp."),
        Pair("SBUX", "Starbucks Corp."),
        Pair("COIN", "Coinbase Global"),
        Pair("BABA", "Alibaba Group")
    )

    private val _stockList = MutableLiveData<List<StockQuote>>()
    val stockList: LiveData<List<StockQuote>> = _stockList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadStocks()
    }

    fun loadStocks() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val stocks = supervisorScope {
                    popularStocks.map { (symbol, name) ->
                        async {
                            try {
                                val quoteTask = async { finnhubApi.getQuote(symbol) }
                                val profileTask = async { try { finnhubApi.getProfile(symbol) } catch(e: Exception) { null } }
                                
                                val quote = quoteTask.await()
                                val profile = profileTask.await()

                                if (quote.currentPrice != null && quote.currentPrice > 0) {
                                    StockQuote(
                                        symbol = symbol,
                                        name = name,
                                        currentPrice = quote.currentPrice,
                                        change = quote.change ?: 0.0,
                                        percentChange = quote.percentChange ?: 0.0,
                                        highPrice = quote.highPrice ?: 0.0,
                                        lowPrice = quote.lowPrice ?: 0.0,
                                        openPrice = quote.openPrice ?: 0.0,
                                        previousClosePrice = quote.previousClosePrice ?: 0.0,
                                        logoUrl = profile?.logo
                                    )
                                } else {
                                    null
                                }
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }.awaitAll().filterNotNull()
                }

                if (stocks.isNotEmpty()) {
                    _stockList.value = stocks
                } else {
                    _stockList.value = generateMockStocks()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _stockList.value = generateMockStocks()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun generateMockStocks(): List<StockQuote> {
        return popularStocks.map { (symbol, name) ->
            val basePrice = when(symbol) {
                "AAPL" -> 175.0
                "MSFT" -> 420.0
                "GOOGL" -> 150.0
                "AMZN" -> 180.0
                "TSLA" -> 170.0
                else -> 100.0
            }
            val randomChange = Random.nextDouble(-5.0, 5.0)
            StockQuote(
                symbol = symbol,
                name = name,
                currentPrice = basePrice + randomChange,
                change = randomChange,
                percentChange = (randomChange / basePrice) * 100,
                highPrice = basePrice + 10,
                lowPrice = basePrice - 10,
                openPrice = basePrice,
                previousClosePrice = basePrice - randomChange,
                logoUrl = "" // Empty will use placeholder
            )
        }
    }
}
