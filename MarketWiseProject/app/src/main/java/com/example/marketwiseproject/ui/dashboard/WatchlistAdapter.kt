package com.example.marketwiseproject.ui.dashboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.marketwiseproject.R
import com.example.marketwiseproject.data.models.CryptoPrice
import com.example.marketwiseproject.databinding.ItemWatchlistBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

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
                // Set basic info
                symbolText.text = crypto.symbol
                nameText.text = crypto.name
                priceText.text = currencyFormat.format(crypto.price)

                // Set crypto icon (placeholder)
                cryptoIcon.setImageResource(R.mipmap.ic_launcher)

                // Set price change info
                val changePercent = crypto.changePercent24h
                val isPositive = changePercent >= 0

                changeText.text = String.format(
                    "%s%.2f%%",
                    if (isPositive) "↗️ " else "↘️ ",
                    if (isPositive) changePercent else -changePercent
                )

                val changeColor = if (isPositive) Color.parseColor("#00FF41") else Color.parseColor("#FF0040")
                changeText.setTextColor(changeColor)

                // Setup mini chart
                setupMiniChart(isPositive, changeColor)


                root.setOnClickListener {
                    onItemClick(crypto)
                }
            }
        }

        private fun setupMiniChart(isPositive: Boolean, chartColor: Int) {
            binding.miniChart.apply {
                // 1. Generate dummy data (replace with real data)
                val entries = ArrayList<Entry>()
                var lastVal = (20..50).random().toFloat()
                entries.add(Entry(0f, lastVal))
                for (i in 1..10) {
                    lastVal += (-5..5).random().toFloat()
                    if (lastVal < 0) lastVal = 0f
                    entries.add(Entry(i.toFloat(), lastVal))
                }
                val finalVal = if(isPositive) lastVal + 5 else lastVal - 5
                entries.add(Entry(11f, if(finalVal > 0) finalVal else 0f))

                // 2. Create and style dataset
                val dataSet = LineDataSet(entries, "Price trend")
                dataSet.apply {
                    color = chartColor
                    lineWidth = 1.8f
                    setDrawValues(false)
                    setDrawCircles(false)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawFilled(true)
                    fillColor = chartColor
                    fillAlpha = 40
                }

                // 3. Configure chart
                data = LineData(dataSet)
                description.isEnabled = false
                legend.isEnabled = false
                xAxis.isEnabled = false
                axisLeft.isEnabled = false
                axisRight.isEnabled = false
                setTouchEnabled(false)
                isDragEnabled = false
                setScaleEnabled(false)
                setDrawGridBackground(false)

                // 4. Refresh chart
                invalidate()
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
