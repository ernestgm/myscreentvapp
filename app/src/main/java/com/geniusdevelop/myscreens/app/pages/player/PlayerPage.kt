package com.geniusdevelop.myscreens.app.pages.player


import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.geniusdevelop.myscreens.app.api.response.Images
import com.geniusdevelop.myscreens.app.session.SessionManager
import com.geniusdevelop.myscreens.app.util.Configuration
import com.geniusdevelop.myscreens.app.viewmodels.PlayerUiState
import com.geniusdevelop.myscreens.app.viewmodels.PlayerViewModel
import com.google.jetstream.presentation.common.Error
import com.google.jetstream.presentation.common.Loading
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Timer


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerPage(
    playerPageViewModel: PlayerViewModel = viewModel(),
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var images: Array<Images> by remember { mutableStateOf(emptyArray()) }
    var timerActive by remember { mutableStateOf(false) }
    var updatingImagesData by remember { mutableStateOf(false) }
    var updateCurrentIndex by remember { mutableStateOf(false) }
    var initialSizeImages by remember { mutableIntStateOf(0) }
    val screenUpdatedAt by sessionManager.screenUpdateAt.collectAsState(initial = "")
    val coroutineScope = rememberCoroutineScope()
    val code by sessionManager.deviceCode.collectAsState(initial = "")
    var timer: Timer? = null

    val uiState by playerPageViewModel.uiState.collectAsStateWithLifecycle()



    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            playerPageViewModel.getContents(code.toString())
        }
    }

    LaunchedEffect(key1 = timerActive) {
        val autoUpdateTimer = updateTotalTimer(images)
        delay(autoUpdateTimer)
        if (timerActive) {
            Log.d("SERVER_RESPONSE", "Update")
            timerActive = false
            playerPageViewModel.checkForUpdate(code.toString(), screenUpdatedAt.toString())
        }
    }

    when (val s = uiState) {
        is PlayerUiState.Ready -> {
            images = s.images
            initialSizeImages = images.size
            coroutineScope.launch {
                sessionManager.saveScreenUpdatedAt(s.updatedAt)
                timerActive = true
            }
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
                Log.d("SERVER_RESPONSE", "Images Initial Size $initialSizeImages")
                updateCurrentIndex = true
                initialSizeImages = images.size
            }
            if (images.size == 1) {
                initialSizeImages = images.size
                Log.d("SERVER_RESPONSE", "Images 1 $initialSizeImages")
            }

            coroutineScope.launch {
                sessionManager.saveScreenUpdatedAt(s.updatedAt)
                timerActive = true
            }
        }
        is PlayerUiState.ReadyToUpdate -> {
            coroutineScope.launch {
                timerActive = false
                updatingImagesData = true
                updateCurrentIndex = false
                playerPageViewModel.updatePlayer(code.toString())
            }
        }
        is PlayerUiState.UpdateError -> {
            timerActive = true
            updateCurrentIndex = false
            updatingImagesData = false
        }
        else -> {}
    }

    if (images.isNotEmpty()) {
        PlayerCarousel(
            images = images,
            updateCurrentIndex = updateCurrentIndex,
            updating = updatingImagesData
        )
    } else {
        Loading(text = "Waiting images for this screen", modifier = Modifier.fillMaxSize())
    }


}
fun updateTotalTimer(images: Array<Images>): Long {
    Log.d("SERVER_RESPONSE", "Update total timer")
    if (images.isNotEmpty()) {
        val totalTimeTransitions = images.sumOf {
            it.duration ?: 0
        }.toLong() * 1000

        if (totalTimeTransitions > Configuration.Player.UpdateTimeRequestDefault) {
            return totalTimeTransitions
        }
    }

    return Configuration.Player.UpdateTimeRequestDefault
}


