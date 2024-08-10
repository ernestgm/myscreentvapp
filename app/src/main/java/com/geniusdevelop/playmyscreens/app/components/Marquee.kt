package com.geniusdevelop.playmyscreens.app.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import kotlinx.coroutines.delay

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun Marquee(text: String, textColor: String, speed: Int = 90, delayDuration: Long = 300L) {
    val scrollState = rememberScrollState(0)
    var textWidth by remember { mutableIntStateOf(0) }

    BoxWithConstraints {
        LaunchedEffect(Unit) {
            while (true) {
                scrollState.animateScrollTo(
                    textWidth,
                    animationSpec = tween(
                        durationMillis = (textWidth * 1000 / speed),
                        easing = LinearEasing
                    )
                )
                delay(delayDuration)
                scrollState.scrollTo(0)
            }
        }

        Row(
            modifier = Modifier
                .width(this.maxWidth * 2)
                .horizontalScroll(scrollState, reverseScrolling = false)
        ) {
            Spacer(modifier = Modifier.width(this@BoxWithConstraints.maxWidth))
            Text(
                text = text,
                fontSize = 28.sp,
                color = Color(android.graphics.Color.parseColor(textColor)),
                textAlign = TextAlign.Start,
                onTextLayout = { textLayoutResult ->
                    textWidth = textLayoutResult.size.width * 2
                },
                modifier = Modifier
                    .padding(horizontal = 0.dp, vertical = 10.dp)
                    .wrapContentWidth()
            )
            Spacer(modifier = Modifier.width(this@BoxWithConstraints.maxWidth))
        }
    }
}