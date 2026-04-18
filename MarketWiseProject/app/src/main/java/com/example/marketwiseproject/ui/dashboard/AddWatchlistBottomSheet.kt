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

class AddWatchlistBottomSheet(private val onCoinSelected: (String, String, String) -> Unit) : BottomSheetDialogFragment() {

    // Simple mock data for presentation
    private val availableCoins = listOf(
        Pair("bitcoin", "BTC - Bitcoin"),
        Pair("ethereum", "ETH - Ethereum"),
        Pair("binancecoin", "BNB - Binance Coin"),
        Pair("solana", "SOL - Solana"),
        Pair("cardano", "ADA - Cardano"),
        Pair("ripple", "XRP - Ripple"),
        Pair("dogecoin", "DOGE - Dogecoin"),
        Pair("polkadot", "DOT - Polkadot"),
        Pair("matic-network", "MATIC - Polygon")
    )

    private lateinit var searchAdapter: SearchAdapter
    private var filteredCoins = availableCoins.toList()

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

        searchAdapter = SearchAdapter(filteredCoins) { coinId, symbol, name ->
            onCoinSelected(coinId, symbol, name)
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
                filteredCoins = if (query.isEmpty()) {
                    availableCoins
                } else {
                    availableCoins.filter { it.second.lowercase().contains(query) }
                }
                searchAdapter.updateData(filteredCoins)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private inner class SearchAdapter(
        private var items: List<Pair<String, String>>,
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
                        val parts = item.second.split(" - ")
                        onItemClick(item.first, parts[0], parts[1])
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
            val parts = item.second.split(" - ")
            holder.symbolText.text = parts[0]
            holder.nameText.text = parts[1]
        }

        override fun getItemCount() = items.size

        fun updateData(newItems: List<Pair<String, String>>) {
            items = newItems
            notifyDataSetChanged()
        }
    }
}
