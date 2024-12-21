package com.geniusdevelop.playmyscreens.app.pages.logout

import android.os.Bundle
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.session.SessionManager
import com.geniusdevelop.playmyscreens.app.util.DeviceUtils
import com.geniusdevelop.playmyscreens.app.viewmodels.LoginUiState
import com.geniusdevelop.playmyscreens.app.viewmodels.LoginViewModel
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.jetstream.presentation.common.Loading
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SwitchAccountPage(
    goToLogin: () -> Unit,
    goToHomePage: () -> Unit,
    loginPageViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Logout")
    Firebase.analytics.logEvent("logout_view", bundle)
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val uiState by loginPageViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            loginPageViewModel.logout()
        }
    }

    when (val s = uiState) {
        is LoginUiState.Error -> {
            Toast.makeText(context, s.msg, Toast.LENGTH_LONG).show()
            goToLogin()
        }
        is LoginUiState.Ready -> {
            coroutineScope.launch {
                sessionManager.clearSession()
                val deviceUtils = DeviceUtils(context)
                loginPageViewModel.switchAccount(deviceUtils.getDeviceId())
            }
        }
        is LoginUiState.SwitchAccount -> {
            coroutineScope.launch {
                Repository.refreshApiToken(s.success?.token.toString())
                sessionManager.saveSession(
                    true,
                    s.success?.user?.name.toString(),
                    s.success?.user?.id.toString(),
                    s.success?.token.toString()
                )
                goToHomePage()
            }
        }
        is LoginUiState.Loading -> {}
        else -> {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(30.dp, 0.dp),

            ) {
            Loading(text = "Switch Account", modifier = Modifier.fillMaxSize())
        }
    }
}