/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.geniusdevelop.playmyscreens.app.util

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date

object AppLog  {
    val manager: LogManager
        get() = LogManager.getInstance()

    fun initialize(context: Context, deviceId: String) {
        LogInjector.initialize(context, deviceId)
    }
}

class LogManager internal constructor(
    private val context: Context,
    private val deviceId: String
){

    fun logToFile(deviceCode: String,logMessage: String) {
        val logFile = File(context.filesDir, "app_logs_${deviceId}_${deviceCode}.log")
        try {
            val fileWriter = FileWriter(logFile, true)  // 'true' para modo append
            val date = Date(System.currentTimeMillis())
            fileWriter.append("${date}: $logMessage\n")
            fileWriter.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private lateinit var instance: LogManager
        private const val LOG_NOT_INITIALIZED = "Log not initialized"

        fun initialize(
            context: Context,
            deviceId: String
        ): LogManager {
            synchronized(LogManager::class.java) {
                instance = LogManager(context, deviceId)
            }
            return instance
        }

        fun getInstance(): LogManager {
            if (!Companion::instance.isInitialized) {
                throw Exception(LOG_NOT_INITIALIZED)
            } else {
                return instance
            }
        }
    }
}

internal object LogInjector {
    fun initialize(context: Context, deviceId: String) {
        Factory.new(context, deviceId)
    }

    private object Factory {
        fun new(
            context: Context,
            deviceId: String
        ): LogManager {
            return LogManager.initialize(context, deviceId)
        }
    }
}


