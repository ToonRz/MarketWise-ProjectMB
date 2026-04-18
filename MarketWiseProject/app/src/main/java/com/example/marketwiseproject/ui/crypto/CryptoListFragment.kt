package com.example.marketwiseproject.ui.crypto

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
import com.example.marketwiseproject.R
import com.example.marketwiseproject.databinding.FragmentCryptoListBinding
import com.example.marketwiseproject.ui.dashboard.WatchlistAdapter
import kotlinx.coroutines.launch
import java.util.Locale

class CryptoListFragment : Fragment() {

    private var _binding: FragmentCryptoListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CryptoViewModel by viewModels()
    private lateinit var cryptoAdapter: WatchlistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCryptoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()

        // Load crypto list
        viewModel.loadCryptoList()
    }

    private fun setupRecyclerView() {
        cryptoAdapter = WatchlistAdapter { crypto ->
            val symbolStream = "${crypto.symbol.lowercase(Locale.US)}usdt"
            findNavController().navigate(
                R.id.navigation_crypto_detail,
                bundleOf(
                    "symbol" to symbolStream,
                    "coinId" to crypto.id
                )
            )
        }

        binding.cryptoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cryptoAdapter
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.cryptoList.collect { prices ->
                cryptoAdapter.submitList(prices)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadCryptoList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
