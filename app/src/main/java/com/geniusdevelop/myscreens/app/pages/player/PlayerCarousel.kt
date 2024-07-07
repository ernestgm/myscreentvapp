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

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Carousel
import androidx.tv.material3.CarouselDefaults
import androidx.tv.material3.CarouselState
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.ShapeDefaults
import androidx.tv.material3.Text
import com.geniusdevelop.myscreens.R
import com.geniusdevelop.myscreens.app.api.response.Images
import com.geniusdevelop.myscreens.app.api.response.Product
import com.geniusdevelop.myscreens.app.util.Configuration
import com.geniusdevelop.myscreens.app.util.Padding
import com.geniusdevelop.myscreens.app.util.StringConstants


@OptIn(ExperimentalTvMaterial3Api::class)
val CarouselSaver = Saver<CarouselState, Int>(
    save = { it.activeItemIndex },
    restore = { CarouselState(it) }
)

@OptIn(
    ExperimentalTvMaterial3Api::class
)
@Composable
fun PlayerCarousel(
    images: Array<Images>,
    padding: Padding,
    modifier: Modifier = Modifier
) {
    val carouselState = rememberSaveable(saver = CarouselSaver) { CarouselState(0) }
    var isCarouselFocused by remember { mutableStateOf(false) }

    Carousel(
        modifier = modifier
            .padding(start = padding.start, end = padding.start, top = padding.top)
            //.clip(ShapeDefaults.Large)
            .onFocusChanged {
                // Because the carousel itself never gets the focus
                isCarouselFocused = it.hasFocus
            }
            .semantics {
                contentDescription =
                    StringConstants.Composable.ContentDescription.MoviesCarousel
            },
        itemCount = images.size,
        carouselState = carouselState,
        carouselIndicator = {
            CarouselIndicator(
                itemCount = images.size,
                activeItemIndex = carouselState.activeItemIndex
            )
        },
        contentTransformStartToEnd = fadeIn(tween(durationMillis = Configuration.Carousel.TimerTransation))
            .togetherWith(fadeOut(tween(durationMillis = Configuration.Carousel.TimerTransation))),
        contentTransformEndToStart = fadeIn(tween(durationMillis = Configuration.Carousel.TimerTransation))
            .togetherWith(fadeOut(tween(durationMillis = Configuration.Carousel.TimerTransation))),
        content = { index ->
            val image = images[index]
            if (image.isStatic()) {
                CarouselItemBackground(image = image, modifier = Modifier.fillMaxSize())
            } else {
                CarouselItemProducts(
                    image = image,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun BoxScope.CarouselIndicator(
    itemCount: Int,
    activeItemIndex: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(0.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            .graphicsLayer {
                clip = true
                shape = ShapeDefaults.ExtraSmall
            }
            .align(Alignment.BottomEnd)
    ) {
        CarouselDefaults.IndicatorRow(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp),
            itemCount = itemCount,
            activeItemIndex = activeItemIndex,
        )
    }
}

@Composable
fun CarouselItemProducts(
    image: Images,
    modifier: Modifier = Modifier
) {
    // Cargar el fondo de pantalla

    // Crear el contenedor con el fondo de pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Establecer un color de fondo mientras se carga la imagen
    ) {
        image.getImageBitmap()?.asImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = image.description.toString(),
                modifier = modifier.drawWithContent {
                    drawContent()
                    drawRect(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
                },
                contentScale = ContentScale.Crop
            )
        }

        // Crear la tabla de imágenes sobre el fondo
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (!image.products.isNullOrEmpty()) {
                val products = image.products

                var columns = products.size / 3
                var index = 0

                while (columns > 0) {
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        while (index < products.size) {
                            ImageBox(product = products[index])
                            Spacer(modifier = Modifier.height(16.dp))
                            index += 1
                            if (index == products.size / 2) {
                                break
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    columns--
                }

            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ImageBox(
    product: Product
) {
    Row (
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        product.getImageBitmap()?.asImageBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.5f),
                        shape = ShapeDefaults.Medium,
                    )
                    .clip(ShapeDefaults.Medium)
                    .padding(3.dp)
                    .size(130.dp)
                    .background(Color.Gray) // Fondo mientras se carga la imagen
            )
        }


        Column (
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Bottom
        ){
            Text(
                text = product.name.toString(),
                style = MaterialTheme.typography.displaySmall.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = Offset(x = 2f, y = 4f),
                        blurRadius = 2f
                    )
                ),
                color = Color.White,
                maxLines = 1
            )
            Text(
                text = "$" + product.prices?.last()?.value.toString(),
                style = MaterialTheme.typography.displayMedium.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.5f),
                        offset = Offset(x = 2f, y = 4f),
                        blurRadius = 2f
                    )
                ),
                color = Color.White,
                maxLines = 1
            )
        }
    }

}

@Composable
private fun CarouselItemBackground(image: Images, modifier: Modifier = Modifier) {
    image.getImageBitmap()?.asImageBitmap()?.let {
        Image(
            bitmap = it,
            contentDescription = image.description.toString(),
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}
