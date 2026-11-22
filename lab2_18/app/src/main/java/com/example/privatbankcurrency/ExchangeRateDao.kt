package com.example.privatbankcurrency

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRateDao {

    @Query("SELECT * FROM exchange_rates WHERE date = :date ORDER BY currency ASC")
    fun observeRates(date: String): Flow<List<ExchangeRateEntity>>

    @Upsert
    suspend fun upsertRates(rates: List<ExchangeRateEntity>)
}