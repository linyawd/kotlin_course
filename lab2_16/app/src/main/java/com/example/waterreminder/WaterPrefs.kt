package com.example.waterreminder

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object WaterPrefs {
    private const val PREFS_NAME = "water_reminder_prefs"
    private const val KEY_COUNT = "reminder_count"

    private val _countFlow = MutableStateFlow(0)
    val countFlow: StateFlow<Int> = _countFlow.asStateFlow()

    fun init(context: Context) {
        _countFlow.value = getCount(context)
    }

    fun getCount(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_COUNT, 0)
    }

    fun incrementCount(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val updated = prefs.getInt(KEY_COUNT, 0) + 1
        prefs.edit().putInt(KEY_COUNT, updated).apply()
        _countFlow.value = updated
        return updated
    }
}