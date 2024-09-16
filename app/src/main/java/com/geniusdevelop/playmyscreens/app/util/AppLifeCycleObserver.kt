package com.geniusdevelop.playmyscreens.app.util

import android.content.Intent
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.service.BackgroundService

class AppLifecycleObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEnterForeground() {
        Repository.wsManager.connect()
        Log.d("AppLifecycleObserver", "La aplicación está en primer plano")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEnterBackground() {
        Repository.wsManager.disconnect()
        Log.d("AppLifecycleObserver", "La aplicación está en segundo plano")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        Repository.wsManager.disconnect()
        Log.d("AppLifecycleObserver", "La aplicación se detuvo")
    }
}