package com.example.marketwiseproject.ui.stock

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.marketwiseproject.R
import com.example.marketwiseproject.data.models.StockQuote
import com.example.marketwiseproject.databinding.ItemStockBinding
import java.text.NumberFormat
import java.util.Locale

class StockListAdapter(
    private val onItemClick: (StockQuote) -> Unit
) : ListAdapter<StockQuote, StockListAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStockBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemStockBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

        fun bind(stock: StockQuote) {
            binding.apply {
                stockSymbol.text = stock.symbol
                stockName.text = stock.name
                stockPrice.text = currencyFormat.format(stock.currentPrice)

                val isPositive = stock.percentChange >= 0
                val changeText = String.format(
                    "%s%.2f%%",
                    if (isPositive) "+" else "",
                    stock.percentChange
                )
                stockChange.text = changeText
                stockChange.setTextColor(
                    ContextCompat.getColor(
                        root.context,
                        if (isPositive) R.color.positive_muted else R.color.negative_muted
                    )
                )

                // Load Logo using Coil
                stockIcon.load(stock.logoUrl) {
                    crossfade(true)
                    placeholder(R.mipmap.ic_launcher)
                    error(R.mipmap.ic_launcher)
                    transformations(CircleCropTransformation())
                }

                root.setOnClickListener {
                    onItemClick(stock)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<StockQuote>() {
        override fun areItemsTheSame(oldItem: StockQuote, newItem: StockQuote): Boolean {
            return oldItem.symbol == newItem.symbol
        }

        override fun areContentsTheSame(oldItem: StockQuote, newItem: StockQuote): Boolean {
            return oldItem == newItem
        }
    }
}
