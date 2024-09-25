package com.geniusdevelop.playmyscreens.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.geniusdevelop.playmyscreens.BuildConfig
import com.geniusdevelop.playmyscreens.MainActivity
import com.geniusdevelop.playmyscreens.R
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.util.AppLog
import com.geniusdevelop.playmyscreens.app.util.DeviceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

class BackgroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private lateinit var deviceUtils: DeviceUtils

    companion object {
        const val CHANNEL_ID = "ForegroundServiceMyScreen"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start the service in the foreground with a notification
        startForeground(1, createNotification())

        // Your background task logic here
        doBackgroundWork()

        return START_REDELIVER_INTENT
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Background Service Running")
            .setContentText("The app is running in the background")
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun doBackgroundWork() {
        deviceUtils = DeviceUtils(baseContext)
        AppLog.initialize(baseContext, deviceUtils.getDeviceId())
        AppLog.manager.logToFile("", "La app se ha detenido.")

        serviceScope.launch {
            if (isInternetAvailable()) {
                // Network is available, perform tasks
                // e.g., fetch data from server
                println("Network is available")
                uploadFileToFTP()
                delay(30000) // Wait for 10 minutes
                // After the delay, open the app
                openApp()
            } else {
                // No network, handle offline scenario
                println("No network connection")
                waitForConnection()
            }
        }
    }

    private suspend fun uploadFileToFTP() {
        Repository.initializeApiConfig(baseContext)
        val apiServerConfig = Repository.configurations.getConfiguration(BuildConfig.ENV)
        if (apiServerConfig != null) {
            val logFile = File(baseContext.filesDir, "app_logs_${deviceUtils.getDeviceId()}_.log")
            if (logFile.exists()) {
                println("El archivo existe: ${logFile.absolutePath}")
                val client = OkHttpClient()
                val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", logFile.name, RequestBody.create("text/plain".toMediaTypeOrNull(), logFile))
                    .build()

                val request = Request.Builder()
                    .url("${apiServerConfig.screen_server_api_endpoint}/upload")
                    .post(requestBody)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace() // Manejo del error
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            println("Archivo subido exitosamente")
                        } else {
                            println("Error en la subida: ${response.code}")
                        }
                    }
                })
            } else {
                println("El archivo no existe: ${logFile.absolutePath}")
            }
        }
    }

    private fun waitForConnection() {
        serviceScope.launch {
            while (true) {
                delay(30000)
                if (isInternetAvailable()) {
                    openApp()
                } else {
                    println("No network connection")
                }
            }
        }
    }

    fun isAppInstalled(packageManager: PackageManager, packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun openApp() {
        println("ENTRO Open App")
        serviceScope.launch {
            if (isInternetAvailable()) {
                println("Package: $packageName")
                if (isAppInstalled(packageManager, packageName)) {
                    val launchIntent = packageManager.getLeanbackLaunchIntentForPackage(packageName)
                    println("Intent: ${launchIntent}")
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(launchIntent)
                    }
                }
            } else {
                waitForConnection()
            }
        }
    }

    private suspend fun isInternetAvailable(): Boolean {
        return (isNetworkAvailable() && hasInternetAccess())
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            // For devices below Android M (API level 23)
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo?.isConnected == true
        }
    }

    private suspend fun hasInternetAccess(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://www.google.com") // Known reliable server
                    .build()
                val response = client.newCall(request).execute()
                println("Response: ${response.isSuccessful}")
                response.isSuccessful
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        // We are not using binding in this case
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Clean up the coroutine when the service is destroyed
    }
}
