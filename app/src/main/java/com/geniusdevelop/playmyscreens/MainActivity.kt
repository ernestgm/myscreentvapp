package com.geniusdevelop.playmyscreens

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import com.geniusdevelop.playmyscreens.app.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

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