package com.geniusdevelop.myscreens.app

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.geniusdevelop.myscreens.R
import com.geniusdevelop.myscreens.app.session.SessionManager
import com.geniusdevelop.myscreens.app.viewmodels.LoginUiState
import com.geniusdevelop.myscreens.app.viewmodels.LoginViewModel
import com.geniusdevelop.myscreens.ui.theme.navigation.LocalNavController
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppBar(
    logoutClick: () -> Unit,
    loginPageViewModel: LoginViewModel = viewModel()
) {
    val navHostController = LocalNavController.current
    val entry by navHostController.currentBackStackEntryAsState()
    val context = LocalContext.current

    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)
    val username by sessionManager.name.collectAsState(initial = "")
    val coroutineScope = rememberCoroutineScope()
    val uiState by loginPageViewModel.uiState.collectAsStateWithLifecycle()
    var showLoading by remember { mutableStateOf(false) }

    val title = stringResource(R.string.tv_compose)
    val description = "Welcome to the screens manager system"
    val isMainIconMagnified = true

    when (val s = uiState) {
        is LoginUiState.Error -> {
            showLoading = false
            Toast.makeText(context, s.msg, Toast.LENGTH_LONG).show()
        }
        is LoginUiState.Ready -> {
            showLoading = false
            coroutineScope.launch {
                sessionManager.clearSession()
                logoutClick()
            }
        }
        is LoginUiState.Loading -> {
            showLoading = true
        }
        else -> {}
    }

    Row(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxWidth()
            .padding(start = 54.dp, top = 40.dp, end = 38.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeadlineContent(
            title = title,
            description = description,
            isMainIconMagnified = isMainIconMagnified,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isLoggedIn) {
                username?.let {
                    Text(
                        modifier = Modifier.padding(all = 20.dp),
                        text = "Bienvenido $it")
                }
            }
            Actions(
                disabledLogout = !showLoading,
                logoutClick = {
                    loginPageViewModel.logout()
                }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun HeadlineContent(
    title: String,
    description: String? = null,
    isMainIconMagnified: Boolean,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .size(if (isMainIconMagnified) 55.dp else 50.dp)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer.copy(0.4f),
                    shape = CircleShape
                ), contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(if (isMainIconMagnified) 50.dp else 40.dp),
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }

        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.headlineMedium,
                overflow = TextOverflow.Ellipsis,
            )
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun Actions(
    disabledLogout: Boolean,
    logoutClick: () -> Unit,
) {
    val actions = listOf(
        Action(
            enabled = disabledLogout,
            icon = Icons.Default.ExitToApp,
            text = "Logout",
            onClick = logoutClick
        )
    )

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        actions.forEach {
            Button(
                enabled = it.enabled,
                onClick = it.onClick
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    imageVector = it.icon,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = it.text,
                    style = MaterialTheme.typography.labelLarge.copy(textMotion = TextMotion.Animated)
                )
            }
        }
    }
}

private data class Action(
    val enabled: Boolean,
    val icon: ImageVector,
    val text: String,
    val onClick: () -> Unit,
)
