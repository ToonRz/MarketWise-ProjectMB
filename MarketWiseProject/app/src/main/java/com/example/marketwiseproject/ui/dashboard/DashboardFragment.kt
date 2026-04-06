package com.example.marketwiseproject.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketwiseproject.databinding.FragmentDashboardBinding
import java.util.Locale
import kotlinx.coroutines.launch
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.animation.DecelerateInterpolator
import androidx.lifecycle.lifecycleScope
import com.example.marketwiseproject.data.db.AppDatabase
import com.example.marketwiseproject.data.db.WatchlistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.widget.Toast

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var watchlistAdapter: WatchlistAdapter

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

        binding.fabAdd.setOnClickListener { view ->
            // Scale Animation for Gimmick
            val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 0.9f),
                PropertyValuesHolder.ofFloat("scaleY", 0.9f)
            )
            scaleDown.duration = 100
            scaleDown.repeatCount = 1
            scaleDown.repeatMode = ObjectAnimator.REVERSE
            scaleDown.start()

            // Open BottomSheet
            val bottomSheet = AddWatchlistBottomSheet { coinId, symbol, name ->
                addCoinToDatabase(symbol, name)
            }
            bottomSheet.show(parentFragmentManager, "AddWatchlistBottomSheet")
        }
    }

    private fun addCoinToDatabase(symbol: String, name: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = AppDatabase.getDatabase(requireContext()).watchlistDao()
            dao.insert(WatchlistEntity(symbol = symbol, name = name, type = "CRYPTO", addedAt = System.currentTimeMillis()))
            
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "$symbol added to Watchlist!", Toast.LENGTH_SHORT).show()
                // Refresh data
                viewModel.loadWatchlistPrices()
            }
        }
    }

    private fun setupRecyclerView() {
        watchlistAdapter = WatchlistAdapter { crypto ->
            val symbolStream = "${crypto.symbol.lowercase(Locale.US)}usdt"
            findNavController().navigate(
                com.example.marketwiseproject.R.id.navigation_crypto,
                bundleOf(
                    "symbol" to symbolStream,
                    "coinId" to crypto.id
                )
            )
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
            }
        }

        viewModel.fearGreedIndex.observe(viewLifecycleOwner) { index ->
            binding.fearGreedValue.text = when {
                index < 25 -> "$index - Extreme Fear"
                index < 45 -> "$index - Fear"
                index < 55 -> "$index - Neutral"
                index < 75 -> "$index - Greed"
                else -> "$index - Extreme Greed"
            }

            // Gimmick: Animate Progress Bar exactly to the value
            val progressAnimator = ObjectAnimator.ofInt(binding.fearGreedProgress, "progress", 0, index)
            progressAnimator.duration = 1500 // 1.5 seconds animation
            progressAnimator.interpolator = DecelerateInterpolator()
            progressAnimator.start()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
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
