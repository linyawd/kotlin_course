package com.example.privatbankcurrency

data class ExchangeArchiveResponse(
    val date: String? = null,
    val bank: String? = null,
    val baseCurrency: Int? = null,
    val baseCurrencyLit: String? = null,
    val exchangeRate: List<ExchangeRateItem> = emptyList()
)

data class ExchangeRateItem(
    val baseCurrency: String? = null,
    val currency: String? = null,
    val saleRateNB: Double? = null,
    val purchaseRateNB: Double? = null,
    val saleRate: Double? = null,
    val purchaseRate: Double? = null
)