package com.geniusdevelop.playmyscreens.app.pages.logout

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
import com.geniusdevelop.playmyscreens.app.session.SessionManager
import com.geniusdevelop.playmyscreens.app.viewmodels.LoginUiState
import com.geniusdevelop.playmyscreens.app.viewmodels.LoginViewModel
import com.google.jetstream.presentation.common.Loading
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LogoutPage(
    goToLogin: () -> Unit,
    loginPageViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
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
        }
        is LoginUiState.Ready -> {
            coroutineScope.launch {
                sessionManager.clearSession()
                goToLogin()
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
            Loading(text = "Coming out", modifier = Modifier.fillMaxSize())
        }
    }
}