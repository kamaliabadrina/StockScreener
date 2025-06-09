package com.example.stockscreener.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.stockscreener.model.Stock

class FavoritesManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("stock_favorites", Context.MODE_PRIVATE)

    companion object {
        private const val FAVORITES_KEY = "favorite_stocks"
    }

    fun addToFavorites(stockId: Int) {
        val favorites = getFavoriteStockIds().toMutableSet()
        favorites.add(stockId)
        saveFavorites(favorites)
    }

    fun removeFromFavorites(stockId: Int) {
        val favorites = getFavoriteStockIds().toMutableSet()
        favorites.remove(stockId)
        saveFavorites(favorites)
    }

    fun isFavorite(stockId: Int): Boolean {
        return getFavoriteStockIds().contains(stockId)
    }

    fun getFavoriteStockIds(): Set<Int> {
        val favoritesString = sharedPreferences.getString(FAVORITES_KEY, "") ?: ""
        return if (favoritesString.isEmpty()) {
            emptySet()
        } else {
            favoritesString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        }
    }

    private fun saveFavorites(favorites: Set<Int>) {
        val favoritesString = favorites.joinToString(",")
        sharedPreferences.edit()
            .putString(FAVORITES_KEY, favoritesString)
            .apply()
    }

    fun getFavoriteCount(): Int {
        return getFavoriteStockIds().size
    }

    // NEW METHODS FOR GETTING FAVORITE STOCKS

    /**
     * Get favorite stocks from a list of all stocks
     * @param allStocks List of all available stocks
     * @return List of favorite stocks only
     */
    fun getFavoriteStocks(allStocks: List<Stock>): List<Stock> {
        val favoriteIds = getFavoriteStockIds()
        return allStocks.filter { stock -> favoriteIds.contains(stock.id) }
    }

    /**
     * Filter stocks to show only favorites
     * @param allStocks List of all available stocks
     * @return List of favorite stocks, or empty list if no favorites
     */
    fun filterFavoriteStocks(allStocks: List<Stock>): List<Stock> {
        return getFavoriteStocks(allStocks)
    }

    /**
     * Check if there are any favorites
     * @return true if user has favorite stocks, false otherwise
     */
    fun hasFavorites(): Boolean {
        return getFavoriteStockIds().isNotEmpty()
    }

    /**
     * Clear all favorites
     */
    fun clearAllFavorites() {
        sharedPreferences.edit()
            .remove(FAVORITES_KEY)
            .apply()
    }

    /**
     * Toggle favorite status of a stock
     * @param stockId ID of the stock to toggle
     * @return true if stock is now favorited, false if unfavorited
     */
    fun toggleFavorite(stockId: Int): Boolean {
        return if (isFavorite(stockId)) {
            removeFromFavorites(stockId)
            false
        } else {
            addToFavorites(stockId)
            true
        }
    }
}