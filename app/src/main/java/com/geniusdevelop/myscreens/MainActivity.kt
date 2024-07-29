package com.geniusdevelop.myscreens

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.geniusdevelop.myscreens.app.App

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableActiveScreen()
        setContent {
            App()
        }
    }

    private fun disableActiveScreen() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun enableActiveScreen() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onDestroy() {
        disableActiveScreen()
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
}