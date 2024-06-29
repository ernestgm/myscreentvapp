package com.geniusdevelop.myscreens.app.pages.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.geniusdevelop.myscreens.R
import com.geniusdevelop.myscreens.app.session.SessionManager
import com.geniusdevelop.myscreens.ui.theme.navigation.LocalNavController
import com.geniusdevelop.myscreens.ui.theme.navigation.NavGraph
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SplashScreen() {

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()
    val navHostController = LocalNavController.current

    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            delay(3000) // Tiempo en milisegundos para la pantalla de inicio
            val startDestination = if (isLoggedIn) {
                NavGraph.Home.routeName
            } else {
                NavGraph.Login.routeName
            }
            navHostController.navigate(startDestination) {
                popUpTo(NavGraph.Splash.routeName) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .padding(all = 20.dp),
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "")
            Text(
                text = "Welcome to EScreen",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}