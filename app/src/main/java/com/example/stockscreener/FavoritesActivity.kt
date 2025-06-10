package com.example.stockscreener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.example.stockscreener.model.*
import com.example.stockscreener.utils.FavoritesManager

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var stockAdapter: StockAdapter
    private lateinit var favoritesManager: FavoritesManager
    private lateinit var allStocks: List<Stock>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)
        supportActionBar?.apply {
            title = "Favorite Stocks"
            setDisplayHomeAsUpEnabled(true)
        }

        // Initialize views
        recyclerView = findViewById(R.id.favoritesRecyclerView)
        emptyStateText = findViewById(R.id.emptyStateText)
        favoritesManager = FavoritesManager(this)
        allStocks = loadStocksFromAssets()
        setupRecyclerView()
        loadFavorites()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadFavorites() {
        val favoriteStocks = favoritesManager.getFavoriteStocks(allStocks)

        if (favoriteStocks.isEmpty()) {
            recyclerView.visibility = RecyclerView.GONE
            emptyStateText.visibility = TextView.VISIBLE
            emptyStateText.text = getString(R.string.no_favorite_stocks)
        } else {
            recyclerView.visibility = RecyclerView.VISIBLE
            emptyStateText.visibility = TextView.GONE
            val mutableFavoriteStocks = favoriteStocks.toMutableList()
            stockAdapter = StockAdapter(
                stockList = mutableFavoriteStocks,
                favoritesManager = favoritesManager,
                onFavoriteChanged = {
                    loadFavorites()
                    Toast.makeText(this, getString(R.string.favorites_updated), Toast.LENGTH_SHORT).show()
                }
            )
            recyclerView.adapter = stockAdapter
        }
    }

    private fun loadStocksFromAssets(): List<Stock> {
        val jsonString = assets.open("stocks.json").bufferedReader().use { it.readText() }
        val stockResponse = Gson().fromJson(jsonString, StockResponse::class.java)
        return stockResponse.stocks
    }

    override fun onSupportNavigateUp(): Boolean {
        return true
    }

    override fun onResume() {
        super.onResume()
        loadFavorites()
    }
}