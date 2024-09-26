package com.geniusdevelop.playmyscreens

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.geniusdevelop.playmyscreens.app.App
import com.geniusdevelop.playmyscreens.app.service.BackgroundService
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : ComponentActivity() {
    private lateinit var serviceIntent: Intent
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        if (!isBatteryOptimizationIgnored(baseContext)) {
            disableOptimizationBattery()
        }

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
    private fun isBatteryOptimizationIgnored(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun disableOptimizationBattery() {
        val intent = Intent()
        intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
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
        logMemoryUsage()
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

    private fun logMemoryUsage() {
        // Obtener la información de memoria
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        // Obtener memoria disponible en MB
        val availableMegs = memoryInfo.availMem / 1048576L

        // Crear un bundle para enviar datos
        val bundle = Bundle()
        bundle.putLong("available_memory_mb", availableMegs)

        // Registrar el evento en Firebase Analytics
        mFirebaseAnalytics!!.logEvent("memory_usage_event", bundle)
    }
}