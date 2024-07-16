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

package com.geniusdevelop.myscreens.app.pages.player

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.geniusdevelop.myscreens.app.api.response.Images
import com.google.jetstream.presentation.common.Loading
import kotlinx.coroutines.delay


@Composable
fun PlayerCarousel(
    images: Array<Images>,
    updateCurrentIndex: Boolean = false,
    updating: Boolean = false,
) {
    val durations = images.map {
        ((it.duration?.toLong() ?: 1) * 1000)
    }

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = updateCurrentIndex) {
        if (images.isNotEmpty()) {
            if (updateCurrentIndex) {
                Log.d("CAROUSEL", "Index update")
                if (images.size > 1) {
                    currentIndex = (currentIndex + 1) % images.size
                }
            }
        }
    }

    LaunchedEffect(currentIndex) {
        if (durations.isNotEmpty()) {
            if (currentIndex in durations.indices) {
                delay(durations[currentIndex])
                Log.d("CAROUSEL", "Index $currentIndex")
                currentIndex = (currentIndex + 1) % images.size
            }
        }
    }

    Crossfade(targetState = currentIndex, label = "") { index ->
        if (images.isNotEmpty()) {
            if (index in images.indices && !updating) {
                CarouselItemBackground(image = images[index], modifier = Modifier.fillMaxSize())
            } else {
                CarouselItemBackground(image = images[0], modifier = Modifier.fillMaxSize())
            }
        }
    }

}

@Composable
private fun CarouselItemBackground(image: Images, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        image.getImageBitmap()?.asImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = image.description.toString(),
                modifier = modifier.alpha(0.3F),
                contentScale = ContentScale.Crop
            )
            Image(
                bitmap = it,
                contentDescription = image.description.toString(),
                modifier = modifier,
                contentScale = ContentScale.Fit
            )
        }
    }

}
