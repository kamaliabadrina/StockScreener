package com.example.stockscreener.model

data class StockResponse(
    val stocks: List<Stock>
)

data class Stock(
    val id: Int,
    val symbol: String,
    val name: String,
    val logo_url: String,
    val stock_price: StockPrice
)

data class StockPrice(
    val current_price: CurrentPrice,
    val price_change: Double,
    val percentage_change: Double
)

data class CurrentPrice(
    val amount: String,
    val currency: String
)
