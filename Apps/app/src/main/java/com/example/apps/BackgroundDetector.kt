package com.example.apps

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class BackgroundDetector : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("BackgroundDetector", "Застосунок перейшов у FOREGROUND (видимий на екрані)")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("BackgroundDetector", "Застосунок перейшов у BACKGROUND (згорнутий у фон)")
    }
}