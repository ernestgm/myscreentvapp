package com.geniusdevelop.playmyscreens

import android.content.Intent
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.geniusdevelop.playmyscreens.app.App
import com.geniusdevelop.playmyscreens.app.service.BackgroundService

class MainActivity : ComponentActivity() {
    private lateinit var serviceIntent: Intent

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        disableOptimizationBattery()
        enableActiveScreen()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            checkPermissionOverlay()
        }

        setContent {
            App()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun checkPermissionOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            val REQUEST_OVERLAY_PERMISSION = 1001
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun disableOptimizationBattery() {
        val intent = Intent()
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        startActivity(intent)
    }

    private fun disableActiveScreen() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun enableActiveScreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        disableActiveScreen()
        startServiceBackground()
        super.onDestroy()
    }

    override fun onRestart() {
        enableActiveScreen()
        stopServiceBackground()
        super.onRestart()
    }

    override fun onResume() {
        enableActiveScreen()
        stopServiceBackground()
        super.onResume()
    }

    override fun onPause() {
        disableActiveScreen()
        startServiceBackground()
        super.onPause()
    }

    private fun startServiceBackground() {
        if (!::serviceIntent.isInitialized) {
            serviceIntent = Intent(this, BackgroundService::class.java)
        }

        startService(serviceIntent)
    }

    private fun stopServiceBackground() {
        if (::serviceIntent.isInitialized) {
            stopService(serviceIntent)
        }
    }
}