package com.example.privatbankcurrency

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PrivatBankApi {

    @GET("exchange_rates?json")
    suspend fun getExchangeRates(
        @Query("date") date: String
    ): Response<ExchangeArchiveResponse>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.privatbank.ua/p24api/"

    val api: PrivatBankApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PrivatBankApi::class.java)
    }
}