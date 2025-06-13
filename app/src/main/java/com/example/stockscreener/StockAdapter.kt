package com.example.stockscreener

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stockscreener.model.Stock
import com.example.stockscreener.utils.FavoritesManager
import java.util.Locale

class StockAdapter(
    private val stockList: MutableList<Stock>,
    private val favoritesManager: FavoritesManager,
    private val onFavoriteChanged: (() -> Unit)? = null
) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logoImage: ImageView = itemView.findViewById(R.id.imageLogo)
        val nameText: TextView = itemView.findViewById(R.id.tvName)
        val symbolText: TextView = itemView.findViewById(R.id.tvSymbol)
        val priceText: TextView = itemView.findViewById(R.id.tvPrice)
        val priceChangeText: TextView = itemView.findViewById(R.id.tvPriceChange)
        val currencyText: TextView = itemView.findViewById(R.id.tvCurrency)
        val priceChangeContainer: LinearLayout = itemView.findViewById(R.id.priceChangeContainer)
        val favoriteButton: ImageButton = itemView.findViewById(R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = stockList[position]
        val context = holder.itemView.context

        // Basic stock information
        holder.nameText.text = stock.name
        holder.symbolText.text = stock.symbol

        val price = stock.stock_price.current_price.amount
        val currency = stock.stock_price.current_price.currency

        val formattedPrice = formatCurrencyPrice(price, currency)

        Log.d("StockAdapter", "Stock: ${stock.symbol}, Price: $price, Currency: $currency, Formatted: $formattedPrice")

        holder.priceText.text = formattedPrice
        holder.currencyText.visibility = View.GONE

        val priceChange = stock.stock_price.price_change
        val percentageChange = stock.stock_price.percentage_change

        val isPositive = priceChange >= 0
        val arrow = if (isPositive) "▲" else "▼"
        val sign = if (isPositive) "+" else ""

        holder.priceChangeText.text = String.format(
            Locale.US,
            "%s %s%.2f (%.2f%%)",
            arrow,
            sign,
            priceChange,
            percentageChange
        )

        holder.priceChangeText.setTextColor(ContextCompat.getColor(context, android.R.color.white))

        if (isPositive) {
            holder.priceChangeContainer.setBackgroundResource(R.drawable.price_change_background)
            holder.priceChangeContainer.isSelected = true
            holder.priceChangeContainer.isActivated = false
        } else {
            holder.priceChangeContainer.setBackgroundResource(R.drawable.price_change_background)
            holder.priceChangeContainer.isSelected = false
            holder.priceChangeContainer.isActivated = true
        }

        val isFavorite = favoritesManager.isFavorite(stock.id)
        updateFavoriteButton(holder.favoriteButton, isFavorite)

        holder.favoriteButton.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                if (isFavorite) {
                    favoritesManager.removeFromFavorites(stock.id)
                } else {
                    favoritesManager.addToFavorites(stock.id)
                }
                // Update only this specific item instead of the entire list
                notifyItemChanged(currentPosition)
                onFavoriteChanged?.invoke()
            }
        }

        Glide.with(context)
            .load(stock.logo_url)
            .into(holder.logoImage)
    }

    private fun formatCurrencyPrice(amount: String, currency: String): String {
        return try {
            val price = amount.toDoubleOrNull() ?: 0.0

            when (currency.uppercase()) {
                "USD" -> String.format(Locale.US, "$%.2f", price)
                "MYR" -> String.format(Locale.US, "RM %.2f", price)
                "EUR" -> String.format(Locale.US, "€%.2f", price)
                "GBP" -> String.format(Locale.UK, "£%.2f", price)
                "JPY" -> String.format(Locale.JAPAN, "¥%.0f", price)
                "CNY" -> String.format(Locale.CHINA, "¥%.2f", price)
                "INR" -> String.format(Locale.US, "₹%.2f", price)
                "SGD" -> String.format(Locale.US, "S$%.2f", price)
                "HKD" -> String.format(Locale.US, "HK$%.2f", price)
                "CAD" -> String.format(Locale.CANADA, "C$%.2f", price)
                "AUD" -> String.format(Locale.US, "A$%.2f", price)
                "KRW" -> String.format(Locale.KOREA, "₩%.0f", price)
                "THB" -> String.format(Locale.US, "฿%.2f", price)
                else -> String.format(Locale.US, "%s %.2f", currency, price)
            }
        } catch (e: Exception) {
            Log.e("StockAdapter", "Error formatting price: $amount $currency", e)
            "$currency $amount"
        }
    }

    private fun updateFavoriteButton(button: ImageButton, isFavorite: Boolean) {
        if (isFavorite) {
            button.setImageResource(R.drawable.ic_favorite_filled)
            button.contentDescription = "Remove from favorites"
        } else {
            button.setImageResource(R.drawable.ic_favorite_border)
            button.contentDescription = "Add to favorites"
        }
    }

    // Efficient method to update the stock list using DiffUtil
    fun updateStocks(newStocks: List<Stock>) {
        val diffCallback = StockDiffCallback(stockList, newStocks)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        stockList.clear()
        stockList.addAll(newStocks)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateItemAt(position: Int) {
        if (position in 0 until stockList.size) {
            notifyItemChanged(position)
        }
    }

    fun notifyFavoritesCleared() {
        notifyItemRangeChanged(0, stockList.size)
    }

    override fun getItemCount(): Int = stockList.size

    private class StockDiffCallback(
        private val oldList: List<Stock>,
        private val newList: List<Stock>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldStock = oldList[oldItemPosition]
            val newStock = newList[newItemPosition]

            return oldStock.id == newStock.id &&
                    oldStock.name == newStock.name &&
                    oldStock.symbol == newStock.symbol &&
                    oldStock.stock_price.current_price.amount == newStock.stock_price.current_price.amount &&
                    oldStock.stock_price.current_price.currency == newStock.stock_price.current_price.currency &&
                    oldStock.stock_price.price_change == newStock.stock_price.price_change &&
                    oldStock.stock_price.percentage_change == newStock.stock_price.percentage_change &&
                    oldStock.logo_url == newStock.logo_url
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldStock = oldList[oldItemPosition]
            val newStock = newList[newItemPosition]
            val changes = mutableListOf<String>()

            if (oldStock.stock_price.current_price.amount != newStock.stock_price.current_price.amount) {
                changes.add("price")
            }
            if (oldStock.stock_price.price_change != newStock.stock_price.price_change) {
                changes.add("change")
            }
            if (oldStock.stock_price.percentage_change != newStock.stock_price.percentage_change) {
                changes.add("percentage")
            }

            return changes.ifEmpty { null }
        }
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val stock = stockList[position]
            val changes = payloads[0] as List<*>

            if (changes.contains("price")) {
                val price = stock.stock_price.current_price.amount
                val currency = stock.stock_price.current_price.currency
                holder.priceText.text = formatCurrencyPrice(price, currency)
            }

            if (changes.contains("change") || changes.contains("percentage")) {
                val priceChange = stock.stock_price.price_change
                val percentageChange = stock.stock_price.percentage_change
                val isPositive = priceChange >= 0
                val arrow = if (isPositive) "▲" else "▼"
                val sign = if (isPositive) "+" else ""

                holder.priceChangeText.text = String.format(
                    Locale.US,
                    "%s %s%.2f (%.2f%%)",
                    arrow,
                    sign,
                    priceChange,
                    percentageChange
                )

                if (isPositive) {
                    holder.priceChangeContainer.isSelected = true
                    holder.priceChangeContainer.isActivated = false
                } else {
                    holder.priceChangeContainer.isSelected = false
                    holder.priceChangeContainer.isActivated = true
                }
            }
        }
    }
}