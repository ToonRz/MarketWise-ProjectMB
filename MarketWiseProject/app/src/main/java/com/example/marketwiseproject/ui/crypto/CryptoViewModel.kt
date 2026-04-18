package com.example.marketwiseproject.ui.crypto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketwiseproject.data.models.CryptoPrice
import com.example.marketwiseproject.data.models.TechnicalIndicator
import com.example.marketwiseproject.data.models.TradingSignal
import com.example.marketwiseproject.data.repository.CryptoRepository
import com.example.marketwiseproject.utils.TechnicalAnalysis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CryptoViewModel : ViewModel() {

    private val repository = CryptoRepository()

    // For detail screen
    private val _currentPrice = MutableStateFlow(0.0)
    val currentPrice: StateFlow<Double> = _currentPrice

    private val _cryptoData = MutableLiveData<CryptoPrice>()
    val cryptoData: LiveData<CryptoPrice> = _cryptoData

    private val _technicalIndicator = MutableLiveData<TechnicalIndicator>()
    val technicalIndicator: LiveData<TechnicalIndicator> = _technicalIndicator

    private val _historicalPrices = MutableLiveData<List<Double>>()
    val historicalPrices: LiveData<List<Double>> = _historicalPrices

    // For list screen
    private val _cryptoList = MutableStateFlow<List<CryptoPrice>>(emptyList())
    val cryptoList: StateFlow<List<CryptoPrice>> = _cryptoList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadCryptoList() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val prices = repository.getTopCryptos()
                _cryptoList.value = prices
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startRealtimeUpdates(symbol: String) {
        viewModelScope.launch {
            repository.getRealTimePrice(symbol).collect { price ->
                _currentPrice.value = price
            }
        }
    }

    fun loadCryptoData(coinId: String) {
        viewModelScope.launch {
            try {
                // Load historical prices from API (returns List<List<Double>>)
                val priceData = repository.getHistoricalPrices(coinId, days = 30)

                // Extract only prices (index 1) from [[timestamp, price], ...]
                val prices = priceData.map { it[1] }  // ✅ ตรงนี้สำคัญ!

                // Update LiveData
                _historicalPrices.value = prices

                // Calculate technical indicators only if we have data
                if (prices.isNotEmpty()) {
                    val rsi = TechnicalAnalysis.calculateRSI(prices)
                    val (macd, macdSignal, _) = TechnicalAnalysis.calculateMACD(prices)
                    val ma50 = TechnicalAnalysis.calculateSMA(prices, period = 50)
                    val ma200 = TechnicalAnalysis.calculateSMA(prices, period = 200)
                    val (upper, middle, lower) = TechnicalAnalysis.calculateBollingerBands(prices)

                    val signal = TechnicalAnalysis.generateSignal(
                        rsi = rsi,
                        macd = macd,
                        macdSignal = macdSignal,
                        price = prices.last(),
                        ma50 = ma50,
                        ma200 = ma200
                    )

                    _technicalIndicator.value = TechnicalIndicator(
                        rsi = rsi,
                        macd = macd,
                        macdSignal = macdSignal,
                        ma50 = ma50,
                        ma200 = ma200,
                        bollingerUpper = upper,
                        bollingerLower = lower,
                        signal = signal
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _historicalPrices.value = emptyList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnect()
    }
}