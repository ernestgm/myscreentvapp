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

package com.geniusdevelop.playmyscreens.app.repositories

import com.geniusdevelop.playmyscreens.app.api.models.toImage
import com.google.jetstream.data.util.AssetsReader
import com.geniusdevelop.playmyscreens.app.util.StringConstants

class MovieDataSource (
    assetsReader: AssetsReader
) {

    private val top250MovieDataReader = CachedDataReader {
        readMovieData(assetsReader, StringConstants.Assets.Top250Movies)
    }

    private var movieWithLongThumbnailDataReader: MovieDataReader = CachedDataReader {
        top250MovieDataReader.read().map {
            it.toImage()
        }
    }


    suspend fun getTop10Images() =
        movieWithLongThumbnailDataReader.read().subList(20, 23)
}
