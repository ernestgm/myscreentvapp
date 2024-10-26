package com.geniusdevelop.playmyscreens.app.pages.home

import android.os.Bundle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.geniusdevelop.playmyscreens.app.session.SessionManager
import com.geniusdevelop.playmyscreens.app.util.DeviceUtils
import com.geniusdevelop.playmyscreens.app.util.Padding
import com.geniusdevelop.playmyscreens.app.viewmodels.HomeScreeViewModel
import com.geniusdevelop.playmyscreens.app.viewmodels.HomeScreenUiState
import com.geniusdevelop.playmyscreens.ui.theme.common.Error
import com.geniusdevelop.playmyscreens.ui.theme.common.ErrorWithButton
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.jetstream.presentation.common.Loading
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    goToPlayerPage: () -> Unit,
    goToLogoutPage: () -> Unit,
    homeScreeViewModel: HomeScreeViewModel = viewModel()
) {
    val context = LocalContext.current
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Home")
    Firebase.analytics.logEvent("home_view", bundle)
    val sessionManager = remember { SessionManager(context) }
    var showLoading by remember { mutableStateOf(false) }
    val userId by sessionManager.userId.collectAsState(initial = "")
    val coroutineScope = rememberCoroutineScope()
    val code by sessionManager.deviceCode.collectAsState(initial = "")

    val uiState by homeScreeViewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        // Effect is triggered when HomeScreen is displayed
        coroutineScope.launch {
            homeScreeViewModel.setDeviceCode(userId.toString())
        }

        onDispose {
            homeScreeViewModel.removeAllSubscriptions()
        }
    }

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {
            showLoading = false
            coroutineScope.launch {
                sessionManager.saveDeviceCode(s.deviceCode)
                homeScreeViewModel.initSubscribeDevice(code.toString())
                homeScreeViewModel.checkExistScreenForDevice(code.toString())
            }
        }
        is HomeScreenUiState.ExistScreen -> {
            if (s.exist) {
                goToPlayerPage()
            }
        }
        is HomeScreenUiState.Loading -> {
            Loading(text = "Creating device code", modifier = Modifier.fillMaxSize())
        }
        is HomeScreenUiState.Error -> {
            ErrorWithButton(text = "${s.msg} ID: (${DeviceUtils(context).getDeviceId()})", modifier = Modifier.fillMaxSize()) {
                coroutineScope.launch {
                    homeScreeViewModel.setDeviceCode(userId.toString())
                }
            }
        }
        is HomeScreenUiState.DisabledScreen -> {
            Error(text = "Screen has been Disabled. Contact with support. Device Code: ${code}", modifier = Modifier.fillMaxSize())
        }
        is HomeScreenUiState.LogoutUser -> {
            goToLogoutPage()
        }
        else -> {}
    }


    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp, 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally

            ) {
            Loading(text = "Waiting for the screen data. Device Code: ${code}", modifier = Modifier.fillMaxSize())
        }
    }
}


