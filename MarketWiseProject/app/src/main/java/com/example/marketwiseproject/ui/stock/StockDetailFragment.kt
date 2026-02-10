package com.example.marketwiseproject.ui.stock

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.marketwiseproject.R
import com.example.marketwiseproject.data.api.FinnhubApi
import com.example.marketwiseproject.data.models.StockDetails
import com.example.marketwiseproject.data.repository.StockRepository
import com.example.marketwiseproject.databinding.FragmentStockDetailBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.NumberFormat
import java.util.Locale

class StockDetailFragment : Fragment() {

    private var _binding: FragmentStockDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StockDetailViewModel

    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)
    private val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStockDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val finnhubApi = FinnhubApi.getInstance()
        val stockRepository = StockRepository(finnhubApi)
        val viewModelFactory = StockDetailViewModelFactory(stockRepository)
        viewModel = ViewModelProvider(this, viewModelFactory)[StockDetailViewModel::class.java]

        val symbol = arguments?.getString("symbol") ?: "AAPL" // Default to AAPL if no symbol is passed

        observeViewModel()
        viewModel.fetchStockDetails(symbol)
    }

    private fun observeViewModel() {
        viewModel.stockDetailState.observe(viewLifecycleOwner) {
            when (it) {
                is StockDetailState.Loading -> {
                    // Show loading indicator
                }
                is StockDetailState.Success -> {
                    updateUi(it.data)
                }
                is StockDetailState.Error -> {
                    // Show error message
                }
            }
        }
    }

    private fun updateUi(details: StockDetails) {
        binding.apply {
            // Header
            stockSymbol.text = details.symbol
            stockName.text = details.name
            stockPrice.text = currencyFormat.format(details.currentPrice)
            
            val isPositive = details.change >= 0
            val changeTextFormatted = String.format(
                "%s%s (%.2f%%)",
                if(isPositive) "+" else "",
                currencyFormat.format(details.change),
                details.percentChange
            )
            stockChange.text = changeTextFormatted
            stockChange.setTextColor(ContextCompat.getColor(requireContext(), if (isPositive) R.color.neon_green else R.color.neon_red))

            // Key Stats
            statOpen.text = currencyFormat.format(details.openPrice)
            statHigh.text = currencyFormat.format(details.highPrice)
            statLow.text = currencyFormat.format(details.lowPrice)
            statPrevClose.text = currencyFormat.format(details.previousClosePrice)
            statMarketCap.text = formatLargeNumber(details.marketCap)
            statVolume.text = formatLargeNumber(details.volume?.toDouble() ?: 0.0)
            statPeRatio.text = details.peRatio?.let { String.format("%.2fx", it) } ?: "N/A"
            statSharesOutstanding.text = formatLargeNumber(details.sharesOutstanding)

            // Chart
            setupPriceChart(details)

            // Placeholder for logo
            companyLogo.setImageResource(R.mipmap.ic_launcher)
        }
    }

    private fun setupPriceChart(details: StockDetails) {
        val entries = ArrayList<Entry>()
        details.candles.closePrices?.forEachIndexed { index, price ->
            entries.add(Entry(index.toFloat(), price.toFloat()))
        }

        val isPositive = details.change >= 0
        val chartColor = ContextCompat.getColor(requireContext(), if (isPositive) R.color.neon_green else R.color.neon_red)

        val dataSet = LineDataSet(entries, "Stock Price").apply {
            color = chartColor
            lineWidth = 2f
            setDrawValues(false)
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            fillColor = chartColor
            fillAlpha = 40
        }

        binding.priceChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            xAxis.isEnabled = false
            axisLeft.textColor = Color.WHITE
            axisRight.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setDrawGridBackground(false)
            invalidate()
        }
    }

    private fun formatLargeNumber(number: Double): String {
        return when {
            number >= 1_000_000_000_000 -> String.format("%.2fT", number / 1_000_000_000_000)
            number >= 1_000_000_000 -> String.format("%.2fB", number / 1_000_000_000)
            number >= 1_000_000 -> String.format("%.2fM", number / 1_000_000)
            number >= 1_000 -> String.format("%.2fK", number / 1_000)
            else -> numberFormat.format(number)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
