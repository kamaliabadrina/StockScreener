package com.example.stockscreener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockscreener.model.Stock
import com.bumptech.glide.Glide
import android.widget.ImageView


class StockAdapter(private val stockList: List<Stock>) :
    RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

    class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logoImage: ImageView = itemView.findViewById(R.id.imageLogo)
        val nameText: TextView = itemView.findViewById(R.id.tvName)
        val symbolText: TextView = itemView.findViewById(R.id.tvSymbol)
        val priceText: TextView = itemView.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false)
        return StockViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
        val stock = stockList[position]
        holder.nameText.text = stock.name
        holder.symbolText.text = stock.symbol
        holder.priceText.text = "RM ${stock.stock_price.current_price.amount}"

        // Load image with Glide
        Glide.with(holder.itemView.context)
            .load(stock.logo_url) // Make sure logoUrl is a valid field
            .into(holder.logoImage)
    }

    override fun getItemCount(): Int = stockList.size
}
