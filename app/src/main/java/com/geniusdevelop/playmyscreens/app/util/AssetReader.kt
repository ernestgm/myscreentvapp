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
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.io.IOException

class AssetsReader (
    private val context: Context,
) {
    fun getJsonDataFromAsset(fileName: String, context: Context = this.context): Result<String> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            Result.success(jsonString)
        } catch (e: IOException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.failure(e)
        }
    }
}
