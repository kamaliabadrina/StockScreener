package com.example.stockscreener.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stockscreener.model.Stock
import com.example.stockscreener.model.StockResponse
import com.example.stockscreener.utils.FavoritesManager
import com.google.gson.Gson

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val favoritesManager = FavoritesManager(application)

    private val _allStocks = MutableLiveData<List<Stock>>()
    val allStocks: LiveData<List<Stock>> = _allStocks

    private val _filteredStocks = MutableLiveData<List<Stock>>()
    val filteredStocks: LiveData<List<Stock>> = _filteredStocks

    private val _stockCount = MutableLiveData<Int>()
    val stockCount: LiveData<Int> = _stockCount

    private val _stocksLabel = MutableLiveData<String>()
    val stocksLabel: LiveData<String> = _stocksLabel

    private val _favoriteCount = MutableLiveData<Int>()
    val favoriteCount: LiveData<Int> = _favoriteCount

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    private var currentQuery = ""

    init {
        loadStocks()
        updateFavoriteCount()
    }

    fun getFavoritesManager(): FavoritesManager = favoritesManager

    private fun loadStocks() {
        try {
            val jsonString = getApplication<Application>().assets.open("stocks.json")
                .bufferedReader().use { it.readText() }
            val stockResponse = Gson().fromJson(jsonString, StockResponse::class.java)
            _allStocks.value = stockResponse.stocks
            _filteredStocks.value = stockResponse.stocks
            _stockCount.value = stockResponse.stocks.size
            _stocksLabel.value = "Trending Stocks" // You can pass this from resources
        } catch (e: Exception) {
            _toastMessage.value = "Error loading stocks: ${e.message}"
        }
    }

    fun filterStocks(query: String) {
        currentQuery = query
        val stocks = _allStocks.value ?: return

        val filtered = if (query.isEmpty()) {
            _stocksLabel.value = "Trending Stocks"
            stocks
        } else {
            _stocksLabel.value = "Search Results"
            val lowercaseQuery = query.lowercase()
            stocks.filter { stock ->
                stock.symbol.lowercase().contains(lowercaseQuery) ||
                        stock.name.lowercase().contains(lowercaseQuery)
            }
        }

        _filteredStocks.value = filtered
        _stockCount.value = filtered.size
    }

    private fun updateFavoriteCount() {
        _favoriteCount.value = favoritesManager.getFavoriteCount()
    }

    fun clearAllFavorites() {
        if (favoritesManager.hasFavorites()) {
            favoritesManager.clearAllFavorites()
            updateFavoriteCount()
            _toastMessage.value = "All favorites cleared"
        } else {
            _toastMessage.value = "No favorites to clear"
        }
    }

    fun onFavoriteChanged() {
        updateFavoriteCount()
    }

    fun refreshData() {
        updateFavoriteCount()
        filterStocks(currentQuery)
    }

    fun getStockCountText(): String {
        val count = _stockCount.value ?: 0
        return if (count == 1) "1 stock" else "$count stocks"
    }

    fun getFavoriteMenuTitle(): String {
        val count = _favoriteCount.value ?: 0
        return if (count > 0) "Favorites ($count)" else "Favorites"
    }
}