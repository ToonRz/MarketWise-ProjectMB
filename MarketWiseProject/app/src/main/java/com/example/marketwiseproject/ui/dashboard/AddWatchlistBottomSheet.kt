package com.example.marketwiseproject.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketwiseproject.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddWatchlistBottomSheet(private val onAssetSelected: (String, String, String, String) -> Unit) : BottomSheetDialogFragment() {

    // Simple mock data for presentation
    private val availableAssets = listOf(
        // Crypto
        Triple("bitcoin", "BTC", "Bitcoin"),
        Triple("ethereum", "ETH", "Ethereum"),
        Triple("binancecoin", "BNB", "Binance Coin"),
        Triple("solana", "SOL", "Solana"),
        Triple("cardano", "ADA", "Cardano"),
        // Stocks
        Triple("AAPL", "AAPL", "Apple Inc."),
        Triple("TSLA", "TSLA", "Tesla, Inc."),
        Triple("GOOGL", "GOOGL", "Alphabet Inc."),
        Triple("MSFT", "MSFT", "Microsoft Corp."),
        Triple("AMZN", "AMZN", "Amazon.com, Inc."),
        Triple("META", "META", "Meta Platforms")
    )

    private lateinit var searchAdapter: SearchAdapter
    private var filteredAssets = availableAssets.toList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_watchlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.search_results_rv)
        val searchInput = view.findViewById<EditText>(R.id.search_edit_text)

        searchAdapter = SearchAdapter(filteredAssets) { id, symbol, name ->
            val type = if (id.first().isLowerCase()) "CRYPTO" else "STOCK"
            onAssetSelected(id, symbol, name, type)
            dismiss()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchAdapter
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.lowercase() ?: ""
                filteredAssets = if (query.isEmpty()) {
                    availableAssets
                } else {
                    availableAssets.filter { 
                        it.second.lowercase().contains(query) || it.third.lowercase().contains(query) 
                    }
                }
                searchAdapter.updateData(filteredAssets)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private inner class SearchAdapter(
        private var items: List<Triple<String, String, String>>,
        private val onItemClick: (String, String, String) -> Unit
    ) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val nameText: TextView = view.findViewById(R.id.cryptoName)
            val symbolText: TextView = view.findViewById(R.id.cryptoSymbol)

            init {
                view.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = items[position]
                        onItemClick(item.first, item.second, item.third)
                    }
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_coin, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.symbolText.text = item.second
            holder.nameText.text = item.third
        }

        override fun getItemCount() = items.size

        fun updateData(newItems: List<Triple<String, String, String>>) {
            items = newItems
            notifyDataSetChanged()
        }
    }

}
