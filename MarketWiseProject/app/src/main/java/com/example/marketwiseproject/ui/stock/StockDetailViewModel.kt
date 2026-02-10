package com.example.marketwiseproject.ui.stock

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.marketwiseproject.data.models.StockDetails
import com.example.marketwiseproject.data.repository.StockRepository
import kotlinx.coroutines.launch

sealed class StockDetailState {
    object Loading : StockDetailState()
    data class Success(val data: StockDetails) : StockDetailState()
    data class Error(val message: String) : StockDetailState()
}

class StockDetailViewModel(private val stockRepository: StockRepository) : ViewModel() {

    private val _stockDetailState = MutableLiveData<StockDetailState>()
    val stockDetailState: LiveData<StockDetailState> = _stockDetailState

    fun fetchStockDetails(symbol: String) {
        _stockDetailState.value = StockDetailState.Loading
        viewModelScope.launch {
            val result = stockRepository.getStockDetails(symbol)
            result.onSuccess {
                _stockDetailState.postValue(StockDetailState.Success(it))
            }.onFailure {
                _stockDetailState.postValue(StockDetailState.Error(it.message ?: "An unknown error occurred"))
            }
        }
    }
}

class StockDetailViewModelFactory(private val stockRepository: StockRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StockDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StockDetailViewModel(stockRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
