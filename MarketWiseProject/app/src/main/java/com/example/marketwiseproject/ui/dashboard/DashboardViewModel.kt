package com.example.marketwiseproject.ui.dashboard


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketwiseproject.data.db.AppDatabase
import com.example.marketwiseproject.data.db.WatchlistEntity
import com.example.marketwiseproject.data.models.CryptoPrice
import com.example.marketwiseproject.data.repository.CryptoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val cryptoRepository = CryptoRepository()

    private val _watchlistPrices = MutableStateFlow<List<CryptoPrice>>(emptyList())
    val watchlistPrices: StateFlow<List<CryptoPrice>> = _watchlistPrices

    private val _fearGreedIndex = MutableLiveData<Int>()
    val fearGreedIndex: LiveData<Int> = _fearGreedIndex

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadWatchlistPrices()
        loadFearGreedIndex()
    }

    fun loadWatchlistPrices() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val prices = cryptoRepository.getTopCryptos()
                _watchlistPrices.value = prices.take(10)
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