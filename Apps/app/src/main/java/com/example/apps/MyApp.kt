package com.example.apps

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                val state = (activity as? AppCompatActivity)?.lifecycle?.currentState
                Timber.d("GlobalCallback: onActivityCreated -> ${activity.javaClass.simpleName}, currentState: $state")
            }

            override fun onActivityStarted(activity: Activity) {
                val state = (activity as? AppCompatActivity)?.lifecycle?.currentState
                Timber.d("GlobalCallback: onActivityStarted -> ${activity.javaClass.simpleName}, currentState: $state")
            }

            override fun onActivityResumed(activity: Activity) {
                val state = (activity as? AppCompatActivity)?.lifecycle?.currentState
                Timber.d("GlobalCallback: onActivityResumed -> ${activity.javaClass.simpleName}, currentState: $state")
            }

            override fun onActivityPaused(activity: Activity) {
                val state = (activity as? AppCompatActivity)?.lifecycle?.currentState
                Timber.d("GlobalCallback: onActivityPaused -> ${activity.javaClass.simpleName}, currentState: $state")
            }

            override fun onActivityStopped(activity: Activity) {
                val state = (activity as? AppCompatActivity)?.lifecycle?.currentState
                Timber.d("GlobalCallback: onActivityStopped -> ${activity.javaClass.simpleName}, currentState: $state")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                val state = (activity as? AppCompatActivity)?.lifecycle?.currentState
                Timber.d("GlobalCallback: onActivitySaveInstanceState -> ${activity.javaClass.simpleName}, currentState: $state")
            }

            override fun onActivityDestroyed(activity: Activity) {
                val state = (activity as? AppCompatActivity)?.lifecycle?.currentState
                Timber.d("GlobalCallback: onActivityDestroyed -> ${activity.javaClass.simpleName}, currentState: $state")
            }
        })
    }
}