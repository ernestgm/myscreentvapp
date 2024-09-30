package com.geniusdevelop.playmyscreens.app

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.ui.theme.BlueSeedColor
import com.geniusdevelop.playmyscreens.ui.theme.Mode
import com.geniusdevelop.playmyscreens.ui.theme.colorutils.Scheme.Companion.light
import com.geniusdevelop.playmyscreens.ui.theme.colorutils.toColorScheme
import com.geniusdevelop.playmyscreens.ui.theme.navigation.NavigationGraph

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun App() {
    val themeMode by remember { mutableStateOf(Mode.Light) }
    val seedColor by remember { mutableStateOf(BlueSeedColor) }
    val layoutDirection by remember { mutableStateOf(LayoutDirection.Ltr) }
    val fontScale by remember { mutableFloatStateOf(1.0f) }

    val argbColor = seedColor.color.toArgb()
    val colorScheme = light(argbColor)
    val context = LocalContext.current
    Repository.initializeApiConfig(context)

    AppProviders(
        seedColor = seedColor,
        themeMode = themeMode,
        layoutDirection = layoutDirection,
        fontScale = fontScale,
    ) {
        MaterialTheme(colorScheme = colorScheme.toColorScheme()) {
            Surface(
                Modifier.fillMaxSize(),
                shape = RectangleShape
            ) {
                Column(Modifier.fillMaxSize()) {
                    NavigationGraph()
                }
            }
        }
    }
}
