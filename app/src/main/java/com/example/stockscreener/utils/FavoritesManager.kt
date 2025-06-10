package com.example.stockscreener.utils

import android.annotation.SuppressLint
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

    private fun getFavoriteStockIds(): Set<Int> {
        val favoritesString = sharedPreferences.getString(FAVORITES_KEY, "") ?: ""
        return if (favoritesString.isEmpty()) {
            emptySet()
        } else {
            favoritesString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        }
    }

    @SuppressLint("UseKtx")
    private fun saveFavorites(favorites: Set<Int>) {
        val favoritesString = favorites.joinToString(",")
        sharedPreferences.edit()
            .putString(FAVORITES_KEY, favoritesString)
            .apply()
    }

    fun getFavoriteCount(): Int {
        return getFavoriteStockIds().size
    }

    fun getFavoriteStocks(allStocks: List<Stock>): List<Stock> {
        val favoriteIds = getFavoriteStockIds()
        return allStocks.filter { stock -> favoriteIds.contains(stock.id) }
    }

    fun hasFavorites(): Boolean {
        return getFavoriteStockIds().isNotEmpty()
    }

    @SuppressLint("UseKtx")
    fun clearAllFavorites() {
        sharedPreferences.edit()
            .remove(FAVORITES_KEY)
            .apply()
    }
}