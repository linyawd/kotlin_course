package com.example.apps

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AudioPlayer : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("AudioPlayer", "AudioPlayer: Відтворення фонового аудіо відновлено")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("AudioPlayer", "AudioPlayer: Відтворення фонового аудіо поставлено на паузу")
    }
}