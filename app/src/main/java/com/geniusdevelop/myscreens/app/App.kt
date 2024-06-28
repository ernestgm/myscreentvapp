package com.geniusdevelop.myscreens.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.geniusdevelop.myscreens.app.api.conection.Repository
import com.geniusdevelop.myscreens.app.session.SessionManager
import com.geniusdevelop.myscreens.ui.theme.BlueSeedColor
import com.geniusdevelop.myscreens.ui.theme.Mode
import com.geniusdevelop.myscreens.ui.theme.colorutils.Scheme.Companion.dark
import com.geniusdevelop.myscreens.ui.theme.colorutils.toColorScheme
import com.geniusdevelop.myscreens.ui.theme.navigation.NavigationGraph

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun App() {
    val themeMode by remember { mutableStateOf(Mode.Dark) }
    var seedColor by remember { mutableStateOf(BlueSeedColor) }
    var layoutDirection by remember { mutableStateOf(LayoutDirection.Ltr) }
    var fontScale by remember { mutableFloatStateOf(1.0f) }

    var isThemeSelectorExpanded by remember { mutableStateOf(false) }

    val argbColor = seedColor.color.toArgb()
    val colorScheme = dark(argbColor)

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val token by sessionManager.token.collectAsState(initial = "")
    Repository.initialize(token)

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
