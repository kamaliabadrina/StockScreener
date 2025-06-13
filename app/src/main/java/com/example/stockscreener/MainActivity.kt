package com.example.stockscreener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
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
import com.example.stockscreener.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var stockAdapter: StockAdapter
    private lateinit var searchEditText: EditText
    private lateinit var stockCountTextView: TextView
    private lateinit var stocksLabelTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initializeViews()
        setupRecyclerView()
        setupSearch()
        setupFAB()
        observeViewModel()
    }

    private fun initializeViews() {
        searchEditText = findViewById(R.id.etSearch)
        stockCountTextView = findViewById(R.id.tvStockCount)
        stocksLabelTextView = findViewById(R.id.tvStocksLabel)
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.stockRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        stockAdapter = StockAdapter(
            stockList = mutableListOf(),
            favoritesManager = viewModel.getFavoritesManager(),
            onFavoriteChanged = {
                viewModel.onFavoriteChanged()
                invalidateOptionsMenu()
            }
        )

        recyclerView.adapter = stockAdapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.filterStocks(s.toString())
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

    private fun observeViewModel() {
        viewModel.filteredStocks.observe(this) { stocks ->
            stockAdapter.updateStocks(stocks)
        }

        viewModel.stockCount.observe(this) {
            stockCountTextView.text = viewModel.getStockCountText()
        }

        viewModel.stocksLabel.observe(this) { label ->
            stocksLabelTextView.text = label
        }

        viewModel.favoriteCount.observe(this) {
            invalidateOptionsMenu()
        }

        viewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val favoritesItem = menu?.findItem(R.id.action_favorites)
        favoritesItem?.title = viewModel.getFavoriteMenuTitle()
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
                viewModel.clearAllFavorites()
                stockAdapter.notifyFavoritesCleared()
                invalidateOptionsMenu()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
        invalidateOptionsMenu()
    }
}