package com.example.marketwiseproject.ui.dashboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.marketwiseproject.data.models.CryptoPrice
import com.example.marketwiseproject.databinding.ItemWatchlistBinding
import java.text.NumberFormat
import java.util.*

class WatchlistAdapter(
    private val onItemClick: (CryptoPrice) -> Unit
) : ListAdapter<CryptoPrice, WatchlistAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWatchlistBinding.inflate(
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
        private val binding: ItemWatchlistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.US)

        fun bind(crypto: CryptoPrice) {
            binding.apply {
                symbolText.text = crypto.symbol
                nameText.text = crypto.name
                priceText.text = currencyFormat.format(crypto.price)

                val changePercent = crypto.changePercent24h
                val isPositive = changePercent >= 0

                changeText.text = String.format(
                    "%s%.2f%%",
                    if (isPositive) "↗️ +" else "↘️ ",
                    changePercent
                )

                changeText.setTextColor(
                    if (isPositive) Color.parseColor("#00FF41")
                    else Color.parseColor("#FF0040")
                )

                root.setOnClickListener {
                    onItemClick(crypto)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CryptoPrice>() {
        override fun areItemsTheSame(oldItem: CryptoPrice, newItem: CryptoPrice): Boolean {
            return oldItem.symbol == newItem.symbol
        }

        override fun areContentsTheSame(oldItem: CryptoPrice, newItem: CryptoPrice): Boolean {
            return oldItem == newItem
        }
    }
}
