package com.example.marketwiseproject.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketwiseproject.R
import com.example.marketwiseproject.data.db.AppDatabase
import com.example.marketwiseproject.data.db.WatchlistEntity
import com.example.marketwiseproject.data.models.CryptoPrice
import com.example.marketwiseproject.databinding.FragmentDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale
import android.widget.Toast

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var watchlistAdapter: WatchlistAdapter
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()

        binding.addToWatchlistBtn.setOnClickListener {
            // Open BottomSheet
            val bottomSheet = AddWatchlistBottomSheet { id, symbol, name, type ->
                addAssetToDatabase(id, symbol, name, type)
            }
            bottomSheet.show(parentFragmentManager, "AddWatchlistBottomSheet")
        }
    }

    private fun addAssetToDatabase(id: String, symbol: String, name: String, type: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(requireContext()).watchlistDao()
            dao.insert(WatchlistEntity(
                symbol = symbol, 
                name = name, 
                type = type, 
                addedAt = System.currentTimeMillis()
            ))

            
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "$symbol added to Watchlist!", Toast.LENGTH_SHORT).show()
                // data refresh will be triggered automatically by the Flow observer in DashboardViewModel
            }
        }
    }

    private fun setupRecyclerView() {
        watchlistAdapter = WatchlistAdapter { asset ->
            if (asset.type == "STOCK") {
                findNavController().navigate(
                    R.id.navigation_stock_detail,
                    bundleOf("symbol" to asset.symbol)
                )
            } else {
                val symbolStream = "${asset.symbol.lowercase(Locale.US)}usdt"
                findNavController().navigate(
                    R.id.navigation_crypto_detail,
                    bundleOf(
                        "symbol" to symbolStream,
                        "coinId" to asset.id
                    )
                )
            }
        }


        binding.watchlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = watchlistAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.watchlistPrices.collect { prices ->
                watchlistAdapter.submitList(prices)
                // Update Quick Pulse with first 3 items
                updateQuickPulse(prices.take(3))
            }
        }

        viewModel.fearGreedIndex.observe(viewLifecycleOwner) { value ->
            binding.fgValue.text = value.toString()
            
            val (label, colorRes) = when {
                value >= 75 -> "Extreme Greed" to R.color.positive_muted
                value >= 55 -> "Greed" to R.color.positive_muted
                value >= 45 -> "Neutral" to R.color.text_gray_warm
                value >= 25 -> "Fear" to R.color.negative_muted
                else -> "Extreme Fear" to R.color.negative_muted
            }
            
            binding.fgLabel.text = label
            binding.fgLabel.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
            
            // Update gauge
            val params = binding.fgGaugeFill.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
            params.matchConstraintPercentWidth = value / 100f
            binding.fgGaugeFill.layoutParams = params
            binding.fgGaugeFill.setBackgroundColor(ContextCompat.getColor(requireContext(), colorRes))
        }


        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun updateQuickPulse(cryptos: List<CryptoPrice>) {
        val container = binding.quickPulseContainer
        container.removeAllViews()

        for (crypto in cryptos) {
            val cardView = LayoutInflater.from(requireContext())
                .inflate(R.layout.widget_quick_pulse, container, false)

            // Set layout params with margin
            val params = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.quick_pulse_card_width),
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = resources.getDimensionPixelSize(R.dimen.quick_pulse_card_margin)
            cardView.layoutParams = params

            // Set real data
            cardView.findViewById<TextView>(R.id.widget_symbol).text = "${crypto.symbol}/USDT"
            cardView.findViewById<TextView>(R.id.widget_price).text = currencyFormat.format(crypto.price)

            val isPositive = crypto.changePercent24h >= 0
            val changeText = String.format(
                "%s%.2f%%",
                if (isPositive) "+" else "",
                crypto.changePercent24h
            )
            val changeTv = cardView.findViewById<TextView>(R.id.widget_change)
            changeTv.text = changeText
            changeTv.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isPositive) R.color.positive_muted else R.color.negative_muted
                )
            )

            val cal = java.util.Calendar.getInstance()
            val timeStr = String.format("%02d:%02d", cal.get(java.util.Calendar.HOUR_OF_DAY), cal.get(java.util.Calendar.MINUTE))
            cardView.findViewById<TextView>(R.id.widget_update_time).text = "Updated: $timeStr"

            container.addView(cardView)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadWatchlistPrices()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
