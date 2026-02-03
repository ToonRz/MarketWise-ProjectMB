package com.example.marketwiseproject.ui.crypto

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.example.marketwiseproject.R  // ✅ เพิ่มบรรทัดนี้
import com.example.marketwiseproject.data.models.TradingSignal
import com.example.marketwiseproject.databinding.FragmentCryptoDetailBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

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
                textColor = Color.WHITE
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#1A1F3A")
                textColor = Color.WHITE
            }

            axisRight.isEnabled = false
            legend.textColor = Color.WHITE
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentPrice.collect { price ->
                binding.currentPrice.text = currencyFormat.format(price)
            }
        }

        viewModel.historicalPrices.observe(viewLifecycleOwner) { prices ->
            if (prices.isNotEmpty()) {
                updateChart(prices)
            }
        }

        viewModel.technicalIndicator.observe(viewLifecycleOwner) { indicator ->
            updateIndicators(indicator)
        }
    }

    private fun updateChart(prices: List<Double>) {
        val entries = prices.mapIndexed { index, price ->
            Entry(index.toFloat(), price.toFloat())
        }

        val dataSet = LineDataSet(entries, "Price").apply {
            color = Color.parseColor("#00D9FF")
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            fillColor = Color.parseColor("#00D9FF")
            fillAlpha = 50
            setDrawFilled(true)
        }

        binding.priceChart.data = LineData(dataSet)
        binding.priceChart.invalidate()
    }

    private fun updateIndicators(indicator: com.example.marketwiseproject.data.models.TechnicalIndicator) {
        binding.apply {
            // RSI
            rsiValue.text = String.format("%.2f", indicator.rsi)
            rsiSignal.text = when {
                indicator.rsi < 30 -> "OVERSOLD"
                indicator.rsi > 70 -> "OVERBOUGHT"
                else -> "NEUTRAL"
            }
            rsiSignal.setTextColor(when {
                indicator.rsi < 30 -> Color.parseColor("#00FF41")
                indicator.rsi > 70 -> Color.parseColor("#FF0040")
                else -> Color.parseColor("#8B93A7")
            })

            // MACD
            macdValue.text = String.format("%.2f", indicator.macd)
            macdSignal.text = if (indicator.macd > indicator.macdSignal) "BUY" else "SELL"
            macdSignal.setTextColor(
                if (indicator.macd > indicator.macdSignal) Color.parseColor("#00FF41")
                else Color.parseColor("#FF0040")
            )

            // Moving Averages
            maValue.text = String.format(
                "$%.0f / $%.0f",
                indicator.ma50,
                indicator.ma200
            )

            // Overall Signal
            updateSignalCard(indicator.signal)
        }
    }

    private fun updateSignalCard(signal: TradingSignal) {
        binding.apply {
            signalText.text = signal.name.replace("_", " ")

            val (backgroundColor, textColor) = when (signal) {
                TradingSignal.STRONG_BUY -> Pair("#00FF41", "#0A0E27")
                TradingSignal.BUY -> Pair("#00D9FF", "#0A0E27")
                TradingSignal.NEUTRAL -> Pair("#8B93A7", "#FFFFFF")
                TradingSignal.SELL -> Pair("#FF9500", "#0A0E27")
                TradingSignal.STRONG_SELL -> Pair("#FF0040", "#FFFFFF")
            }

            signalCard.setCardBackgroundColor(Color.parseColor(backgroundColor))
            signalText.setTextColor(Color.parseColor(textColor))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}