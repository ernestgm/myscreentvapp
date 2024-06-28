package com.geniusdevelop.myscreens.app.pages

import android.app.Application
import android.content.res.Configuration
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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import com.geniusdevelop.myscreens.R
import com.geniusdevelop.myscreens.app.session.SessionManager
import com.geniusdevelop.myscreens.app.viewmodels.LoginUiState
import com.geniusdevelop.myscreens.app.viewmodels.LoginViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LoginPage(
    goToHomePage: () -> Unit,
    loginPageViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val uiState by loginPageViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is LoginUiState.Error -> {
            showLoading = false
            Toast.makeText(context, s.msg, Toast.LENGTH_LONG).show()
        }
        is LoginUiState.Ready -> {
            showLoading = false
            coroutineScope.launch {
                sessionManager.saveSession(
                    true,
                    s.success?.user?.name.toString(),
                    s.success?.token.toString()
                )
            }
            goToHomePage()
        }
        is LoginUiState.Loading -> {
            showLoading = true
        }
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
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(30.dp, 0.dp),
                verticalArrangement = Arrangement.Top,
            ) {
                Text("Welcome Back")
                Spacer(
                    modifier = Modifier
                        .padding(10.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.illustration_login),
                    contentDescription = "",
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = "Artist image",
                    )
                    Column(
                        modifier = Modifier
                            .padding(12.dp),
                    ) {
                        Text("Sign in to EScreen")
                    }
                }
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .focusRequester(FocusRequester()),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    visualTransformation = PasswordVisualTransformation()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            loginPageViewModel.authenticate(email, password)
                        },
                        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                        enabled = !showLoading
                    ) {

                        val image = if (showLoading) { Icons.Default.Refresh } else { Icons.Default.ArrowForward }
                        val text = if (showLoading) { "Loading..." } else { "Login" }

                        Text(text = text)
                        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                        Icon(
                            imageVector = image,
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    }
                }

            }
        }
    }
}