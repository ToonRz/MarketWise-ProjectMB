package com.example.marketwiseproject.ui.stock

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
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
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.marketwiseproject.data.db.AppDatabase
import com.example.marketwiseproject.data.db.PriceAlertEntity
import com.example.marketwiseproject.ui.crypto.PriceAlertBottomSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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

        val symbol = arguments?.getString("symbol") ?: "AAPL"

        observeViewModel()
        
        binding.btnRetry.setOnClickListener {
            viewModel.fetchStockDetails(symbol)
        }

        viewModel.fetchStockDetails(symbol)

        binding.btnPriceAlert.setOnClickListener {
            val currentPrice = (viewModel.stockDetailState.value as? StockDetailState.Success)?.data?.currentPrice ?: 0.0
            val bottomSheet = PriceAlertBottomSheet(symbol, currentPrice) { targetPrice, isAbove ->
                setupPriceAlert(symbol, targetPrice, isAbove)
            }
            bottomSheet.show(parentFragmentManager, "PriceAlertBottomSheet")
        }
    }

    private fun setupPriceAlert(symbol: String, target: Double, isAbove: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            db.priceAlertDao().insertAlert(
                PriceAlertEntity(
                    symbol = symbol,
                    name = symbol,
                    targetPrice = target,
                    isAbove = isAbove
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Alert set for $symbol at $$target", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun observeViewModel() {
        viewModel.stockDetailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is StockDetailState.Loading -> {
                    binding.loadingProgress.visibility = View.VISIBLE
                    binding.stockDetailContent.visibility = View.GONE
                    binding.errorLayout.visibility = View.GONE
                }
                is StockDetailState.Success -> {
                    binding.loadingProgress.visibility = View.GONE
                    binding.stockDetailContent.visibility = View.VISIBLE
                    binding.errorLayout.visibility = View.GONE
                    updateUi(state.data)
                }
                is StockDetailState.Error -> {
                    binding.loadingProgress.visibility = View.GONE
                    binding.stockDetailContent.visibility = View.GONE
                    binding.errorLayout.visibility = View.VISIBLE
                    binding.errorText.text = state.message
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
            stockChange.setTextColor(ContextCompat.getColor(requireContext(), if (isPositive) R.color.positive_muted else R.color.negative_muted))

            // Key Stats
            statOpen.text = currencyFormat.format(details.openPrice)
            statHigh.text = currencyFormat.format(details.highPrice)
            statLow.text = currencyFormat.format(details.lowPrice)
            statPrevClose.text = currencyFormat.format(details.previousClosePrice)
            statMarketCap.text = formatLargeNumber(details.marketCap)
            statVolume.text = formatLargeNumber(details.volume?.toDouble() ?: 0.0)
            statPeRatio.text = details.peRatio?.let { String.format("%.2fx", it) } ?: "N/A"
            statSharesOutstanding.text = formatLargeNumber(details.sharesOutstanding)

            // Logo with Coil
            companyLogo.load(details.logoUrl) {
                crossfade(true)
                placeholder(R.mipmap.ic_launcher)
                error(R.mipmap.ic_launcher)
                transformations(CircleCropTransformation())
            }

            // Chart
            setupPriceChart(details)
        }
    }

    private fun setupPriceChart(details: StockDetails) {
        val entries = ArrayList<Entry>()
        details.candles.closePrices?.forEachIndexed { index, price ->
            entries.add(Entry(index.toFloat(), price.toFloat()))
        }

        if (entries.isEmpty()) return

        val isPositive = details.change >= 0
        val chartColor = ContextCompat.getColor(requireContext(), if (isPositive) R.color.positive_muted else R.color.negative_muted)

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
            axisLeft.apply {
                textColor = ContextCompat.getColor(requireContext(), R.color.text_gray_warm)
                gridColor = Color.parseColor("#E9E5D9")
            }
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
