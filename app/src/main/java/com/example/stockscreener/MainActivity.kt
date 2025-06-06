package com.example.stockscreener

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.stockscreener.model.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val stocks = loadStocksFromAssets()
        val recyclerView = findViewById<RecyclerView>(R.id.stockRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = StockAdapter(stocks)
    }


    private fun loadStocksFromAssets(): List<Stock> {
        val jsonString = assets.open("stocks.json").bufferedReader().use { it.readText() }
        val stockResponse = Gson().fromJson(jsonString, StockResponse::class.java)
        return stockResponse.stocks
    }

}
