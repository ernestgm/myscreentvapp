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

package com.geniusdevelop.playmyscreens.app.pages.player

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.geniusdevelop.playmyscreens.app.api.response.Images
import com.geniusdevelop.playmyscreens.app.util.BitmapUtil
import kotlinx.coroutines.delay
import java.time.format.TextStyle

@Composable
fun PlayerCarousel(
    images: Array<Images>,
    updateCurrentIndex: Boolean = false,
    updating: Boolean = false,
    onClick: () -> Unit
) {
    val durations = images.map {
        ((it.duration?.toLong() ?: 1) * 1000)
    }

    val descriptions = images.map {
        it.description
    }

    val imagesBitmaps = images.map {
        it.getImageBitmap()?.asImageBitmap()
    }

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = updateCurrentIndex) {
        if (images.isNotEmpty()) {
            if (updateCurrentIndex) {
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

    if (images.isNotEmpty()) {
        if (currentIndex in images.indices && !updating) {
            CarouselItemBackground(
                image = imagesBitmaps[currentIndex],
                description = descriptions[currentIndex],
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onClick()
                    }
            )
        } else {
            CarouselItemBackground(
                image = imagesBitmaps[0],
                description = descriptions[currentIndex],
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onClick()
                    }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun CarouselItemBackground(image: ImageBitmap?, description: String?, modifier: Modifier = Modifier) {
    val gradient = if (!description.isNullOrBlank()) {
         modifier.drawWithContent {
            drawContent()
            drawRect(
                Brush.verticalGradient(
                    startY = 300f,
                    endY = 600f,
                    colors = listOf(
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.5f)
                    )
                )
            )
        }
    } else {
        modifier
    }


        Box(
            modifier = modifier
        ) {
            image?.let {
                var contentScale = ContentScale.Fit
                if (it.width > it.height && BitmapUtil.isAspectRatio16x9(it.width, it.height)) {
                    contentScale = ContentScale.FillBounds
                } else {
                    Image(
                        bitmap = it,
                        contentDescription = "",
                        modifier = modifier.alpha(0.3F),
                        contentScale = ContentScale.Crop
                    )
                }
                Image(
                    bitmap = it,
                    contentDescription = "",
                    modifier = gradient,
                    contentScale = contentScale
                )
            }

            if (!description.isNullOrBlank()) {
                Row( modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(15.dp)
                )  {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(
                                red = 1f,
                                blue = 1f,
                                green = 1f,
                                alpha = 0.65f
                            ),
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.5f),
                                offset = Offset(x = 2f, y = 4f),
                                blurRadius = 2f
                            ),
                            fontSize = 32.sp
                        )
                    )
                }
            }
        }
}
