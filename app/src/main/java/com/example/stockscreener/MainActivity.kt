package com.example.stockscreener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.stockscreener.model.*
import com.example.stockscreener.utils.FavoritesManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var stockAdapter: StockAdapter
    private lateinit var favoritesManager: FavoritesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize FavoritesManager
        favoritesManager = FavoritesManager(this)

        val stocks = loadStocksFromAssets()
        val recyclerView = findViewById<RecyclerView>(R.id.stockRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Create adapter with FavoritesManager and callback
        stockAdapter = StockAdapter(
            stockList = stocks,
            favoritesManager = favoritesManager,
            onFavoriteChanged = {
                // Update the menu to show new favorite count
                invalidateOptionsMenu()
            }
        )

        // Set up FAB for favorites navigation
        val fabFavorites = findViewById<FloatingActionButton>(R.id.fabFavorites)
        fabFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }

        recyclerView.adapter = stockAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // Update favorites menu item with count
        val favoritesItem = menu?.findItem(R.id.action_favorites)
        val favoriteCount = favoritesManager.getFavoriteCount()
        if (favoriteCount > 0) {
            favoritesItem?.title = "Favorites ($favoriteCount)"
        } else {
            favoritesItem?.title = "Favorites"
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_favorites -> {
                val intent = Intent(this, FavoritesActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_clear_favorites -> {
                if (favoritesManager.hasFavorites()) {
                    favoritesManager.clearAllFavorites()
                    stockAdapter.notifyDataSetChanged()
                    invalidateOptionsMenu()
                    Toast.makeText(this, "All favorites cleared", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No favorites to clear", Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the adapter when returning from favorites screen
        stockAdapter.notifyDataSetChanged()
        invalidateOptionsMenu()
    }

    private fun loadStocksFromAssets(): List<Stock> {
        val jsonString = assets.open("stocks.json").bufferedReader().use { it.readText() }
        val stockResponse = Gson().fromJson(jsonString, StockResponse::class.java)
        return stockResponse.stocks
    }
}