package com.example.stockscreener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.example.stockscreener.model.*
import com.example.stockscreener.utils.FavoritesManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.content.Intent
import android.widget.Toast
import android.widget.EditText
import android.text.TextWatcher
import android.text.Editable
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var stockAdapter: StockAdapter
    private lateinit var favoritesManager: FavoritesManager
    private lateinit var allStocks: List<Stock>
    private lateinit var filteredStocks: MutableList<Stock>
    private lateinit var searchEditText: EditText
    private lateinit var stockCountTextView: TextView
    private lateinit var stocksLabelTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        favoritesManager = FavoritesManager(this)
        allStocks = loadStocksFromAssets()
        filteredStocks = allStocks.toMutableList()
        searchEditText = findViewById(R.id.etSearch)
        stockCountTextView = findViewById(R.id.tvStockCount)
        stocksLabelTextView = findViewById(R.id.tvStocksLabel)
        setupRecyclerView()
        setupSearch()
        setupFAB()
        updateStockCount()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.stockRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        stockAdapter = StockAdapter(
            stockList = filteredStocks,
            favoritesManager = favoritesManager,
            onFavoriteChanged = {
                invalidateOptionsMenu()
            }
        )

        recyclerView.adapter = stockAdapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                filterStocks(s.toString())
            }
        })
    }

    private fun setupFAB() {
        val fabFavorites = findViewById<FloatingActionButton>(R.id.fabFavorites)
        fabFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun filterStocks(query: String) {
        filteredStocks.clear()

        if (query.isEmpty()) {
            filteredStocks.addAll(allStocks)
            stocksLabelTextView.text = getString(R.string.trending_stocks)
        } else {
            val lowercaseQuery = query.lowercase()
            filteredStocks.addAll(
                allStocks.filter { stock ->
                    stock.symbol.lowercase().contains(lowercaseQuery) ||
                            stock.name.lowercase().contains(lowercaseQuery)
                }
            )
            stocksLabelTextView.text = getString(R.string.search_results)
        }
        stockAdapter.notifyDataSetChanged()
        updateStockCount()
    }

    private fun updateStockCount() {
        val count = filteredStocks.size
        stockCountTextView.text = if (count == 1) "1 stock" else "$count stocks"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
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
        stockAdapter.notifyDataSetChanged()
        invalidateOptionsMenu()
    }

    private fun loadStocksFromAssets(): List<Stock> {
        val jsonString = assets.open("stocks.json").bufferedReader().use { it.readText() }
        val stockResponse = Gson().fromJson(jsonString, StockResponse::class.java)
        return stockResponse.stocks
    }
}