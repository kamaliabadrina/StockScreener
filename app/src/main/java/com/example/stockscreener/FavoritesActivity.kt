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

        // Set up toolbar/action bar
        supportActionBar?.apply {
            title = "Favorite Stocks"
            setDisplayHomeAsUpEnabled(true)
        }

        // Initialize views
        recyclerView = findViewById(R.id.favoritesRecyclerView)
        emptyStateText = findViewById(R.id.emptyStateText)

        // Initialize favorites manager
        favoritesManager = FavoritesManager(this)

        // Load all stocks and set up RecyclerView
        allStocks = loadStocksFromAssets()
        setupRecyclerView()

        // Load and display favorites
        loadFavorites()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadFavorites() {
        val favoriteStocks = favoritesManager.getFavoriteStocks(allStocks)

        if (favoriteStocks.isEmpty()) {
            // Show empty state
            recyclerView.visibility = RecyclerView.GONE
            emptyStateText.visibility = TextView.VISIBLE
            emptyStateText.text = "No favorite stocks yet.\nAdd some from the main screen!"
        } else {
            // Show favorites
            recyclerView.visibility = RecyclerView.VISIBLE
            emptyStateText.visibility = TextView.GONE

            stockAdapter = StockAdapter(
                stockList = favoriteStocks,
                favoritesManager = favoritesManager,
                onFavoriteChanged = {
                    // Refresh the favorites list when a favorite is removed
                    loadFavorites()
                    Toast.makeText(this, "Favorites updated", Toast.LENGTH_SHORT).show()
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
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        // Refresh favorites when returning to this activity
        loadFavorites()
    }
}