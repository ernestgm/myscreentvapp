package com.geniusdevelop.playmyscreens.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.geniusdevelop.playmyscreens.MainActivity


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            // Inicia tu aplicación aquí
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        }
    }
}