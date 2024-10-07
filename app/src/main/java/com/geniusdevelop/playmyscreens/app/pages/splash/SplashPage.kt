package com.geniusdevelop.playmyscreens.app.pages.splash

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.geniusdevelop.playmyscreens.R
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.session.SessionManager
import com.geniusdevelop.playmyscreens.app.util.AppLifecycleObserver
import com.geniusdevelop.playmyscreens.app.util.DeviceUtils
import com.geniusdevelop.playmyscreens.app.viewmodels.SplashScreenUiState
import com.geniusdevelop.playmyscreens.app.viewmodels.SplashViewModel
import com.geniusdevelop.playmyscreens.ui.theme.common.ErrorWithButton
import com.geniusdevelop.playmyscreens.ui.theme.navigation.LocalNavController
import com.geniusdevelop.playmyscreens.ui.theme.navigation.NavGraph
import com.google.jetstream.presentation.common.Loading
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SplashScreen(
    splashScreeViewModel: SplashViewModel = viewModel()
) {

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)
    val token by sessionManager.token.collectAsState(initial = "")
    val coroutineScope = rememberCoroutineScope()
    val navHostController = LocalNavController.current

    val uiState by splashScreeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            splashScreeViewModel.getConfigurationByEnv()
        }
    }

    when (val s = uiState) {
        is SplashScreenUiState.Ready -> {
            Repository.initialize(context, s.config, token)
            Repository.initializeWs(context, s.config) { msg ->
                Log.d("ERROR", msg)
            }
            val deviceUtils = DeviceUtils(context)
            ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver(deviceUtils.getDeviceId()))
            splashScreeViewModel.setAppOnline()
            val startDestination = if (isLoggedIn) {
                NavGraph.Home.routeName
            } else {
                NavGraph.Login.routeName
            }
            navHostController.navigate(startDestination) {
                popUpTo(NavGraph.Splash.routeName) { inclusive = true }
            }
        }
        is SplashScreenUiState.Loading -> {}
        is SplashScreenUiState.Error -> {
            ErrorWithButton(text = s.msg, modifier = Modifier.fillMaxSize()) {
                coroutineScope.launch {
                    splashScreeViewModel.getConfigurationByEnv()
                }
            }
        }
        else -> {}
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
                text = "Welcome to PlayAds",
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.fillMaxWidth().height(30.dp))
            Loading(text = "", modifier = Modifier
                .fillMaxWidth()
                .height(60.dp))
        }
    }
}