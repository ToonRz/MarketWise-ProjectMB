package com.example.marketwiseproject.ui.dashboard


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.marketwiseproject.data.db.AppDatabase
import com.example.marketwiseproject.data.db.WatchlistEntity
import com.example.marketwiseproject.data.models.CryptoPrice
import com.example.marketwiseproject.data.repository.CryptoRepository
import com.example.marketwiseproject.data.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class DashboardViewModel(application: Application) : AndroidViewModel(application) {


    private val cryptoRepository = CryptoRepository()
    private val stockRepository = StockRepository()
    private val database = AppDatabase.getDatabase(application)

    private val _watchlistPrices = MutableStateFlow<List<CryptoPrice>>(emptyList())
    val watchlistPrices: StateFlow<List<CryptoPrice>> = _watchlistPrices

    private val _fearGreedIndex = MutableLiveData<Int>()
    val fearGreedIndex: LiveData<Int> = _fearGreedIndex

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        observeWatchlist()
        loadFearGreedIndex()
    }

    private fun observeWatchlist() {
        viewModelScope.launch {
            database.watchlistDao().getAllWatchlist().collectLatest { items ->
                loadWatchlistPrices(items)
            }
        }
    }

    fun loadWatchlistPrices(items: List<WatchlistEntity>? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val watchlist = items ?: database.watchlistDao().getAllWatchlist().first()
                if (watchlist.isEmpty()) {
                    _watchlistPrices.value = emptyList()
                    return@launch
                }

                val currentPrices = mutableListOf<CryptoPrice>()
                
                // Fetch top cryptos ONCE for all crypto items in watchlist
                val topCryptos = cryptoRepository.getTopCryptos()
                
                // Fetch stocks in parallel
                val stockDeferreds = watchlist.filter { it.type == "STOCK" }.map { item ->
                    async { stockRepository.getStockDetails(item.symbol) }
                }
                val stockResults = stockDeferreds.awaitAll()

                for (item in watchlist) {
                    if (item.type == "CRYPTO") {
                        val match = topCryptos.find { it.symbol.equals(item.symbol, ignoreCase = true) }
                        match?.let { 
                            currentPrices.add(it.copy(type = "CRYPTO")) 
                        }
                    } 
                }

                // Add successfully fetched stocks
                stockResults.forEach { result ->
                    result.onSuccess { details ->
                        currentPrices.add(CryptoPrice(
                            id = details.symbol,
                            symbol = details.symbol,
                            name = details.name,
                            image = details.logoUrl ?: "",
                            price = details.currentPrice,
                            changePercent24h = details.percentChange,
                            marketCap = details.marketCap,
                            volume24h = details.volume?.toDouble() ?: 0.0,
                            high24h = details.highPrice,
                            low24h = details.lowPrice,
                            type = "STOCK"
                        ))
                    }
                }
                
                _watchlistPrices.value = currentPrices
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }



    private fun loadFearGreedIndex() {
        // Mock data - ในจริงดึงจาก Alternative.me API
        viewModelScope.launch {
            _fearGreedIndex.value = 75
        }
    }

    override fun onCleared() {
        super.onCleared()
        cryptoRepository.disconnect()
    }
}