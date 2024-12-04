package com.geniusdevelop.playmyscreens.app.pages.login

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import com.geniusdevelop.playmyscreens.R
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.session.SessionManager
import com.geniusdevelop.playmyscreens.app.util.BitmapUtil
import com.geniusdevelop.playmyscreens.app.util.DeviceUtils
import com.geniusdevelop.playmyscreens.app.viewmodels.LoginUiState
import com.geniusdevelop.playmyscreens.app.viewmodels.LoginViewModel
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LoginPage(
    goToHomePage: () -> Unit,
    loginPageViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Login")
    Firebase.analytics.logEvent("login_view", bundle)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var showLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val configFields = Repository.configurations.getConfigFields()

    val uiState by loginPageViewModel.uiState.collectAsStateWithLifecycle()
    val deviceUtils = DeviceUtils(context)

    when (val s = uiState) {
        is LoginUiState.Error -> {
            showLoading = false
            Toast.makeText(context, s.msg, Toast.LENGTH_LONG).show()
        }
        is LoginUiState.Ready -> {
            showLoading = false
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
        is LoginUiState.LoginByCode -> {
            loginPageViewModel.loginByCode(code, deviceUtils.getDeviceId())
        }
        is LoginUiState.CodeReady -> {
            code = s.code.toString()
        }
        is LoginUiState.Loading -> {
            showLoading = true
        }
        else -> {}
    }

    DisposableEffect(Unit) {
        // Effect is triggered when HomeScreen is displayed
        coroutineScope.launch {
            loginPageViewModel.initSubscribe(deviceUtils.getDeviceId())
            loginPageViewModel.generateLoginCode(deviceUtils.getDeviceId())
        }

        onDispose {
            loginPageViewModel.removeAllSubscriptions()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center
            ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .padding(30.dp, 0.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Scan the QR or write the url in your browser for login.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
                )
                Spacer(
                    modifier = Modifier
                        .padding(5.dp)
                )
                if (configFields != null) {
                    Text(
                        text = configFields.activate_url,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
                    )
                }
                Spacer(
                    modifier = Modifier
                        .padding(5.dp)
                )
                if (configFields != null) {
                    Image(
                        bitmap = BitmapUtil.generateQRCode(configFields.activate_url, 500, 500).asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier.width(250.dp).height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(
                    modifier = Modifier
                        .padding(10.dp)
                )
                Text(
                    text = code,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 1f)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(1f).padding(60.dp, 0.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier.width(100.dp),
                        painter = painterResource(id = R.drawable.ic_logo_only_img),
                        contentDescription = "Artist image",
                    )
                    Column(
                        modifier = Modifier
                            .padding(12.dp),
                    ) {
                        Text("Sign in to PlayAds")
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
                        unfocusedTextColor = Color.Black,
                        focusedTextColor = Color.Black,
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
                        onDone = {
                            loginPageViewModel.authenticate(email, password)
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        unfocusedTextColor = Color.Black,
                        focusedTextColor = Color.Black,
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
                        enabled = !showLoading,
                        modifier = Modifier.clickable {
                            loginPageViewModel.authenticate(email, password)
                        },
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