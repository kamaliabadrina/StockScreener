package com.example.stockscreener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stockscreener.model.Stock
import com.example.stockscreener.utils.FavoritesManager
import kotlin.math.abs

class StockAdapter(
    private val stockList: List<Stock>,
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
        holder.currencyText.text = stock.stock_price.current_price.currency

        // Format price based on currency
        val price = stock.stock_price.current_price.amount
        val currency = stock.stock_price.current_price.currency
        holder.priceText.text = when (currency) {
            "USD" -> "$price"
            "MYR" -> "RM $price"
            else -> "$currency $price"
        }

        // Price change formatting
        val priceChange = stock.stock_price.price_change
        val percentageChange = stock.stock_price.percentage_change

        // Determine if price went up or down
        val isPositive = priceChange >= 0
        val arrow = if (isPositive) "▲" else "▼"
        val sign = if (isPositive) "+" else ""

        // Format price change text
        holder.priceChangeText.text = String.format(
            "%s %s%.2f (%.2f%%)",
            arrow,
            sign,
            priceChange,
            percentageChange
        )

        // Set text color to white for all cases
        holder.priceChangeText.setTextColor(ContextCompat.getColor(context, android.R.color.white))

        // Use your existing selector drawable with states
        holder.priceChangeContainer.setBackgroundResource(R.drawable.price_change_background)

        // Set the appropriate state based on price movement
        if (isPositive) {
            holder.priceChangeContainer.isSelected = true
            holder.priceChangeContainer.isActivated = false
        } else {
            holder.priceChangeContainer.isSelected = false
            holder.priceChangeContainer.isActivated = true
        }

        // Handle favorite button
        val isFavorite = favoritesManager.isFavorite(stock.id)
        updateFavoriteButton(holder.favoriteButton, isFavorite)

        holder.favoriteButton.setOnClickListener {
            if (isFavorite) {
                favoritesManager.removeFromFavorites(stock.id)
                updateFavoriteButton(holder.favoriteButton, false)
            } else {
                favoritesManager.addToFavorites(stock.id)
                updateFavoriteButton(holder.favoriteButton, true)
            }
            onFavoriteChanged?.invoke()
        }

        // Load company logo with Glide
        Glide.with(context)
            .load(stock.logo_url)
            .into(holder.logoImage)
    }

    private fun updateFavoriteButton(button: ImageButton, isFavorite: Boolean) {
        if (isFavorite) {
            button.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            button.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    override fun getItemCount(): Int = stockList.size
}