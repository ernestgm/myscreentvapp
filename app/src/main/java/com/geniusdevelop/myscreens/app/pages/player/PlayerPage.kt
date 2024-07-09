package com.geniusdevelop.myscreens.app.pages.player


import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.geniusdevelop.myscreens.app.api.response.Images
import com.geniusdevelop.myscreens.app.session.SessionManager
import com.geniusdevelop.myscreens.app.util.Configuration
import com.geniusdevelop.myscreens.app.util.Padding
import com.geniusdevelop.myscreens.app.viewmodels.HomeScreenUiState
import com.geniusdevelop.myscreens.app.viewmodels.PlayerUiState
import com.geniusdevelop.myscreens.app.viewmodels.PlayerViewModel
import com.google.jetstream.presentation.common.Error
import com.google.jetstream.presentation.common.Loading
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerPage(
    playerPageViewModel: PlayerViewModel = viewModel(),
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    var images: Array<Images> by remember { mutableStateOf(emptyArray()) }
    var timerActive by remember { mutableStateOf(false) }
    val screenUpdatedAt by sessionManager.screenUpdateAt.collectAsState(initial = "")
    val coroutineScope = rememberCoroutineScope()
    val code by sessionManager.deviceCode.collectAsState(initial = "")

    val uiState by playerPageViewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = true) {
        coroutineScope.launch {
            playerPageViewModel.getContents(code.toString())

            val timer = startTimer(Configuration.Player.UpdateTimeRequest.toLong()) {
                if (timerActive) {
                    Log.d("SERVER_RESPONSE", "Update")
                    playerPageViewModel.checkForUpdate(code.toString(), screenUpdatedAt.toString())
                }
            }
        }
    }

    when (val s = uiState) {
        is PlayerUiState.Ready -> {
            images = s.images
            coroutineScope.launch {
                Log.d("SERVER_RESPONSE", "Update $screenUpdatedAt")
                sessionManager.saveScreenUpdatedAt(s.updatedAt)
                timerActive = true
            }
        }
        is PlayerUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }
        is PlayerUiState.Error -> {
            Error(text = s.msg, modifier = Modifier.fillMaxSize())
        }
        is PlayerUiState.Update -> {
            Log.d("SERVER_RESPONSE", "Se actualizo")
            images = s.images
            coroutineScope.launch {
                sessionManager.saveScreenUpdatedAt(s.updatedAt)
                timerActive = true
            }
        }
        is PlayerUiState.ReadyToUpdate -> {
            coroutineScope.launch {
                Log.d("SERVER_RESPONSE", "Update")
                timerActive = false
                playerPageViewModel.updatePlayer(code.toString())
            }
        }
        is PlayerUiState.UpdateError -> {
            Log.d("SERVER_RESPONSE", s.msg)
            timerActive = true
        }
        else -> {}
    }


    PlayerCarousel(
        images = images,
        padding = Padding(0.dp, 0.dp, 0.dp, 0.dp)
    )
}

fun startTimer(intervalMillis: Long, task: () -> Unit): Timer {
    val timer = Timer()
    timer.schedule(object : TimerTask() {
        override fun run() {
            task()
        }
    }, 0, intervalMillis)
    return timer
}

fun stopTimer(timer: Timer) {
    timer.cancel()
}


