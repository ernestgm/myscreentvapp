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

package com.geniusdevelop.myscreens.app.repositories

import com.geniusdevelop.myscreens.app.api.models.Image
import com.geniusdevelop.myscreens.app.api.response.ImagesResponseItem
import com.google.jetstream.data.util.AssetsReader
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

internal class CachedDataReader<T>(private val reader: suspend () -> List<T>) {
    private val mutex = Mutex()
    private lateinit var cache: List<T>

    suspend fun read(): List<T> {
        mutex.withLock {
            if (!::cache.isInitialized) {
                cache = reader()
            }
        }
        return cache
    }
}

internal typealias MovieDataReader = CachedDataReader<Image>

internal suspend fun readMovieData(
    assetsReader: AssetsReader,
    resourceId: String,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): List<ImagesResponseItem> = withContext(dispatcher) {
    assetsReader.getJsonDataFromAsset(resourceId).map {
        Json.decodeFromString<List<ImagesResponseItem>>(it)
    }.getOrDefault(emptyList())
}
