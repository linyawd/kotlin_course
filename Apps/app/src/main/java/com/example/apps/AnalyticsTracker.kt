package com.example.apps

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class AnalyticsTracker : LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                Log.d("AnalyticsTracker","AnalyticsTracker: Користувач взаємодіє з екраном (ON_RESUME)")
            }
            Lifecycle.Event.ON_PAUSE -> {
                Log.d("AnalyticsTracker","AnalyticsTracker: Екран втратив фокус (ON_PAUSE)")
            }
            else -> {
                // для інших подій нічого не робимо
            }
        }
    }
}