package com.geniusdevelop.playmyscreens.app.pages.player


import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonColors
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import com.geniusdevelop.playmyscreens.app.api.response.Images
import com.geniusdevelop.playmyscreens.app.session.SessionManager
import com.geniusdevelop.playmyscreens.app.viewmodels.PlayerUiState
import com.geniusdevelop.playmyscreens.app.viewmodels.PlayerViewModel
import com.geniusdevelop.playmyscreens.ui.theme.common.Error
import com.geniusdevelop.playmyscreens.ui.theme.component.CustomButton
import com.google.jetstream.presentation.common.Loading
import kotlinx.coroutines.launch


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerPage(
    goToHomePage: () -> Unit,
    goToLogout: () -> Unit,
    playerPageViewModel: PlayerViewModel = viewModel(),
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var images: Array<Images> by remember { mutableStateOf(emptyArray()) }
    var showButtonPause by remember { mutableStateOf(false) }
    var updatingImagesData by remember { mutableStateOf(false) }
    var updateCurrentIndex by remember { mutableStateOf(false) }
    var initialSizeImages by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val code by sessionManager.deviceCode.collectAsState(initial = "")
    val userId by sessionManager.userId.collectAsState(initial = "")
    val uiState by playerPageViewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity

    BackHandler {
        coroutineScope.launch {
            activity?.finish()
        }
    }

    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            playerPageViewModel.initSubscriptions(code.toString())
            playerPageViewModel.getContents(code.toString())
        }
    }

    when (val s = uiState) {
        is PlayerUiState.Ready -> {
            images = s.images
            initialSizeImages = images.size
        }

        is PlayerUiState.Loading -> {
            Loading(text = "Loading Screens", modifier = Modifier.fillMaxSize())
        }

        is PlayerUiState.Error -> {
            Error(text = s.msg, modifier = Modifier.fillMaxSize())
        }

        is PlayerUiState.Update -> {
            images = s.images
            updatingImagesData = false
            if (initialSizeImages == 1) {
                updateCurrentIndex = true
                initialSizeImages = images.size
            }
            if (images.size == 1) {
                initialSizeImages = images.size
            }
        }

        is PlayerUiState.ReadyToUpdate -> {
            coroutineScope.launch {
                updatingImagesData = true
                updateCurrentIndex = false
                playerPageViewModel.updatePlayer(code.toString())
            }
        }

        is PlayerUiState.UpdateError -> {
            updateCurrentIndex = false
            updatingImagesData = false
        }

        is PlayerUiState.GotoHome -> {
            goToHomePage()
        }

        is PlayerUiState.GotoLogout -> {
            goToLogout()
        }

        else -> {}
    }

    if (images.isNotEmpty()) {
        if (showButtonPause) {
            Column(
                modifier = Modifier
                    .background(color = Color.White)
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    CustomButton(
                        text = "Close App"
                    ) {
                        activity?.finish()
                    }
                    CustomButton(
                        text = "Back"
                    ) {
                        showButtonPause = false
                    }
                }
            }
        }
        PlayerCarousel(
            images = images,
            updateCurrentIndex = updateCurrentIndex,
            updating = updatingImagesData
        ) {
            showButtonPause = true
        }
    } else {
        Loading(text = "Waiting images for this screen", modifier = Modifier.fillMaxSize())
    }
}


