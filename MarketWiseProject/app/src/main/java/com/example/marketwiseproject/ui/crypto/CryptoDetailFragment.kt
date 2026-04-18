package com.example.marketwiseproject.ui.crypto

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.example.marketwiseproject.R
import com.example.marketwiseproject.data.db.AppDatabase
import com.example.marketwiseproject.data.db.PriceAlertEntity
import com.example.marketwiseproject.databinding.FragmentCryptoDetailBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale
import android.widget.Toast
import com.example.marketwiseproject.data.models.TradingSignal

class CryptoDetailFragment : Fragment() {

    private var _binding: FragmentCryptoDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CryptoViewModel by viewModels()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCryptoDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val symbol = arguments?.getString("symbol") ?: "btcusdt"
        val coinId = arguments?.getString("coinId") ?: "bitcoin"

        setupChart()
        setupObservers()

        viewModel.startRealtimeUpdates(symbol)
        viewModel.loadCryptoData(coinId)
    }

    private fun setupChart() {
        binding.priceChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(false)
            setPinchZoom(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                textColor = ContextCompat.getColor(requireContext(), R.color.text_gray_warm)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E9E5D9")
                textColor = ContextCompat.getColor(requireContext(), R.color.text_gray_warm)
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
        }
    }

    private fun setupObservers() {
        // Observe current price and historical data to update price/change
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentPrice.collect { price ->
                binding.cryptoPrice.text = currencyFormat.format(price)
                
                // Also update the change based on historical data if available
                val hist = viewModel.historicalPrices.value
                if (hist != null && hist.isNotEmpty()) {
                    val firstPrice = hist.first()
                    val change = price - firstPrice
                    val percent = (change / firstPrice) * 100
                    
                    val isPositive = change >= 0
                    binding.cryptoChange.text = String.format(
                        "%s%s (%.2f%%)",
                        if (isPositive) "+" else "",
                        currencyFormat.format(change),
                        percent
                    )
                    binding.cryptoChange.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            if (isPositive) R.color.positive_muted else R.color.negative_muted
                        )
                    )
                }
            }
        }

        viewModel.cryptoData.observe(viewLifecycleOwner) outcome@{ crypto ->
            binding.cryptoSymbol.text = crypto.symbol
            binding.cryptoName.text = crypto.name
            
            // Stats
            binding.statMarketCap.text = formatLargeNumber(crypto.marketCap)
            binding.statVolume.text = formatLargeNumber(crypto.volume24h)
            binding.statHigh.text = currencyFormat.format(crypto.high24h)
            binding.statLow.text = currencyFormat.format(crypto.low24h)
            
            // Logo
            binding.cryptoLogo.load(crypto.image) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }

        viewModel.technicalIndicator.observe(viewLifecycleOwner) { indicator ->
            binding.rsiValue.text = String.format("%.1f", indicator.rsi)
            binding.macdValue.text = String.format("%.4f", indicator.macd)
            binding.signalText.text = "Overall Signal: ${indicator.signal}"
            
            val signalColor = when (indicator.signal) {
                TradingSignal.STRONG_BUY, TradingSignal.BUY -> R.color.positive_muted
                TradingSignal.STRONG_SELL, TradingSignal.SELL -> R.color.negative_muted
                else -> R.color.text_gray_warm
            }
            binding.signalText.setTextColor(ContextCompat.getColor(requireContext(), signalColor))
        }

        viewModel.historicalPrices.observe(viewLifecycleOwner) { prices ->
            if (prices.isNotEmpty()) {
                updateChart(prices)
            }
        }

        binding.btnPriceAlert.setOnClickListener {
            val symbol = arguments?.getString("symbol")?.uppercase() ?: "BTC"
            val currentPrice = viewModel.currentPrice.value
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
                    name = symbol, // Simplified
                    targetPrice = target,
                    isAbove = isAbove
                )
            )
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Alert set for $symbol at $$target", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun updateChart(prices: List<Double>) {
        val entries = prices.mapIndexed { index, price ->
            Entry(index.toFloat(), price.toFloat())
        }

        val isPositive = if (prices.size >= 2) prices.last() >= prices.first() else true
        val chartColor = ContextCompat.getColor(
            requireContext(),
            if (isPositive) R.color.positive_muted else R.color.negative_muted
        )

        val dataSet = LineDataSet(entries, "Price").apply {
            color = chartColor
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            fillColor = chartColor
            fillAlpha = 40
            setDrawFilled(true)
        }

        binding.priceChart.data = LineData(dataSet)
        binding.priceChart.invalidate()
    }

    private fun formatLargeNumber(number: Double): String {
        return when {
            number >= 1_000_000_000_000 -> String.format("%.2fT", number / 1_000_000_000_000)
            number >= 1_000_000_000 -> String.format("%.2fB", number / 1_000_000_000)
            number >= 1_000_000 -> String.format("%.2fM", number / 1_000_000)
            else -> NumberFormat.getNumberInstance(Locale.US).format(number)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}