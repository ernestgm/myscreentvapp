package com.geniusdevelop.playmyscreens.ui.theme.component

import android.graphics.drawable.Drawable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import com.geniusdevelop.playmyscreens.R

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CustomButton(
    text: String = "",
    icon: Int? = null,
    modifier: Modifier = Modifier,
    onCLick: () -> Unit
) {
    Button(
        onClick = { onCLick() },
        modifier = modifier.clickable {
            onCLick()
        },
        colors = ButtonDefaults.colors(
            focusedContainerColor = Color.LightGray
        )
    ) {
        if (icon != null) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "",
                modifier = Modifier
                    .width(50.dp)
                    .height(50.dp)
                    .padding(5.dp)
                    .alpha(0.5f)
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(textMotion = TextMotion.Animated)
        )
    }
}