package com.geniusdevelop.myscreens.app.pages.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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
import com.geniusdevelop.myscreens.app.session.SessionManager
import com.geniusdevelop.myscreens.app.util.Padding
import com.geniusdevelop.myscreens.app.viewmodels.HomeScreeViewModel
import com.geniusdevelop.myscreens.app.viewmodels.HomeScreenUiState
import com.google.jetstream.presentation.common.Error
import com.google.jetstream.presentation.common.Loading
import kotlinx.coroutines.launch

val ParentPadding = PaddingValues(vertical = 20.dp, horizontal = 50.dp)

@Composable
fun rememberChildPadding(direction: LayoutDirection = LocalLayoutDirection.current): Padding {
    return remember {
        Padding(
            start = ParentPadding.calculateStartPadding(direction) + 8.dp,
            top = ParentPadding.calculateTopPadding(),
            end = ParentPadding.calculateEndPadding(direction) + 8.dp,
            bottom = ParentPadding.calculateBottomPadding()
        )
    }
}

@Composable
fun HomePage(
    goToPlayerPage: () -> Unit,
    goToLogoutPage: () -> Unit,
    homeScreeViewModel: HomeScreeViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var showLoading by remember { mutableStateOf(false) }
    val userId by sessionManager.userId.collectAsState(initial = "")
    val coroutineScope = rememberCoroutineScope()
    val code by sessionManager.deviceCode.collectAsState(initial = "")

    val uiState by homeScreeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            homeScreeViewModel.setDeviceCode(userId.toString())
        }
    }

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {
            showLoading = false
            coroutineScope.launch {
                sessionManager.saveDeviceCode(s.deviceCode)
                homeScreeViewModel.initSubscribeDevice(code.toString(), userId.toString())
                homeScreeViewModel.checkExistScreenForDevice(code.toString())
            }
        }
        is HomeScreenUiState.ExistScreen -> {
            System.out.println("ExistScreen")
            if (s.exist) {
                goToPlayerPage()
            }
        }
        is HomeScreenUiState.Loading -> {
            Loading(text = "Creating device code", modifier = Modifier.fillMaxSize())
        }
        is HomeScreenUiState.Error -> {
            Error(text = s.msg, modifier = Modifier.fillMaxSize())
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


