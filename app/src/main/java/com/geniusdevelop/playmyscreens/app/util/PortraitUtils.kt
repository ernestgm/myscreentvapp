package com.geniusdevelop.playmyscreens.app.util

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object PortraitUtils {
    @Composable
    fun getBorderPadding(): Dp {
        val configuration = LocalConfiguration.current
        return (configuration.screenWidthDp * 0.24).dp
    }
}