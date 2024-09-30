package com.geniusdevelop.playmyscreens

import android.app.ActivityManager
import android.content.ComponentCallbacks2
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
import com.geniusdevelop.playmyscreens.app.util.DeviceUtils
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.performance
import com.google.firebase.perf.trace
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket


class MainActivity : ComponentActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var deviceUtils: DeviceUtils
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        deviceUtils = DeviceUtils(baseContext)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        if (!isBatteryOptimizationIgnored(baseContext)) {
            println("Not isBatteryOptimizationIgnored")
            requestDisableBatteryOptimizationForApp(this)
        }

        enableActiveScreen()
        startWDService()
        openPortForWatchDog()

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

    private fun isBatteryOptimizationIgnored(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(packageName)
        } else {
            // Battery optimization doesn't exist on versions below Marshmallow
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestDisableBatteryOptimizationForApp(context: Context) {
        println(packageName)
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        intent.data = Uri.parse("package:${packageName}")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private fun disableActiveScreen() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun enableActiveScreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        disableActiveScreen()
        Firebase.performance.newTrace("app_destroy").trace {
            // Update scenario.
            putAttribute("deviceId", deviceUtils.getDeviceId())
        }
        super.onDestroy()
    }

    override fun onRestart() {
        enableActiveScreen()
        super.onRestart()
    }

    override fun onResume() {
        enableActiveScreen()
        super.onResume()
    }

    override fun onPause() {
        disableActiveScreen()
        super.onPause()
    }

    private fun isWatchDogRunning(): Boolean {
        return try {
            Socket("localhost", 9998).use { true } // Try to open a connection to the port
        } catch (e: IOException) {
            false // If it fails, the port is not open
        }
    }

    private fun startWDService() {
        serviceScope.launch {
            val wdPackageName = "com.geniusdevelop.watchdog.${BuildConfig.BUILD_TYPE}"
            if (deviceUtils.isAppInstalled(packageManager, wdPackageName)) {
                if (!isWatchDogRunning()) {
                    val intent = Intent("com.geniusdevelop.watchdog.START_FOREGROUND_SERVICE")
                    intent.setPackage(wdPackageName)  // The package name of App A
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        baseContext.startForegroundService(intent)
                    } else {
                        baseContext.startService(intent)
                    }
                }
            } else {
                println("WatchDog Not installed")
            }
        }
    }

    private fun openPortForWatchDog() {
        serviceScope.launch {
            val serverSocket = ServerSocket(9999) // Example port 12345
            try {
                while (true) {
                    val socket = serverSocket.accept() // Wait for a client connection
                    // Handle socket connection if needed
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                serverSocket.close()
            }
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

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            Firebase.performance.newTrace("app_memory_kill").trace {
                // Update scenario.
                putAttribute("deviceId", deviceUtils.getDeviceId())
            }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Firebase.performance.newTrace("app_system_low_memory").trace {
            // Update scenario.
            putAttribute("deviceId", deviceUtils.getDeviceId())
        }
    }
}