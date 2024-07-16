package com.geniusdevelop.myscreens.app.pages.logout

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import com.geniusdevelop.myscreens.R
import com.geniusdevelop.myscreens.app.api.conection.Repository
import com.geniusdevelop.myscreens.app.session.SessionManager
import com.geniusdevelop.myscreens.app.viewmodels.LoginUiState
import com.geniusdevelop.myscreens.app.viewmodels.LoginViewModel
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