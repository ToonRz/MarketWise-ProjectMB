package com.example.marketwiseproject.ui.crypto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.marketwiseproject.databinding.BottomSheetPriceAlertBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PriceAlertBottomSheet(
    private val symbol: String,
    private val currentPrice: Double,
    private val onAlertSet: (Double, Boolean) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPriceAlertBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPriceAlertBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.alertSubtitle.text = "Notify me when $symbol hits..."
        binding.priceInput.setText(String.format("%.2f", currentPrice))

        binding.btnSaveAlert.setOnClickListener {
            val input = binding.priceInput.text.toString()
            if (input.isEmpty()) {
                Toast.makeText(context, "Please enter a price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val targetPrice = input.toDoubleOrNull() ?: 0.0
            val isAbove = binding.radioAbove.isChecked

            onAlertSet(targetPrice, isAbove)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
