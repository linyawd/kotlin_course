package com.example.apps

import android.content.Context

object UserTracker {
    private val listeners = mutableListOf<Context>()

    fun register(context: Context) {
        listeners.add(context)
    }

    fun unregister(context: Context) {
        listeners.remove(context)
    }

    fun getListenersCount(): Int = listeners.size
}