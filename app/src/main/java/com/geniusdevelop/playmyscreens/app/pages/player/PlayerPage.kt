package com.geniusdevelop.playmyscreens.app.pages.player


import android.app.Activity
import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.geniusdevelop.playmyscreens.BuildConfig
import com.geniusdevelop.playmyscreens.app.api.response.Images
import com.geniusdevelop.playmyscreens.app.session.SessionManager
import com.geniusdevelop.playmyscreens.app.viewmodels.PlayerUiState
import com.geniusdevelop.playmyscreens.app.viewmodels.PlayerViewModel
import com.geniusdevelop.playmyscreens.ui.theme.common.Error
import com.geniusdevelop.playmyscreens.ui.theme.component.CustomButton
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.jetstream.presentation.common.Loading
import kotlinx.coroutines.launch


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PlayerPage(
    goToSplashPage: () -> Unit,
    refreshPlayer: () -> Unit,
    goToLogout: () -> Unit,
    playerPageViewModel: PlayerViewModel = viewModel(),
) {
    val context = LocalContext.current
    val bundle = Bundle()
    bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "Player")
    Firebase.analytics.logEvent("player_view", bundle)
    val sessionManager = remember { SessionManager(context) }
    var images: Array<Images> by remember { mutableStateOf(emptyArray()) }
    var showButtonPause by remember { mutableStateOf(false) }
    var updatingImagesData by remember { mutableStateOf(false) }
    var updateCurrentIndex by remember { mutableStateOf(false) }
    var initialSizeImages by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val code by sessionManager.deviceCode.collectAsState(initial = "")
    val uiState by playerPageViewModel.uiState.collectAsStateWithLifecycle()
    val activity = LocalContext.current as? Activity

    var showMarquees by remember { mutableStateOf(false) }

    var marqueeMessage by remember { mutableStateOf("") }
    var marqueeBgColor by remember { mutableStateOf("#000000") }
    var marqueeTextColor by remember { mutableStateOf("#FFFFFF") }

    var isPortrait by remember { mutableStateOf(true) }

    BackHandler {
        coroutineScope.launch {
            activity?.finish()
        }
    }

    DisposableEffect(Unit) {
        // Effect is triggered when HomeScreen is displayed
        coroutineScope.launch {
            playerPageViewModel.getContents(code.toString())
            playerPageViewModel.getMarquee(code.toString())
        }

        onDispose {
            playerPageViewModel.removeAllSubscriptions()
        }
    }

    fun updateScreens() {
        coroutineScope.launch {
            updatingImagesData = true
            updateCurrentIndex = false
            playerPageViewModel.updatePlayer(code.toString())
        }
    }

    fun reloadCarrousel() {
        updatingImagesData = false
        if (initialSizeImages == 1) {
            updateCurrentIndex = true
            initialSizeImages = images.size
        }
        if (images.size == 1) {
            initialSizeImages = images.size
        }
    }

    when (val s = uiState) {
        is PlayerUiState.Ready -> {
            isPortrait = s.isPortrait
            images = s.images
            initialSizeImages = images.size
            playerPageViewModel.initSubscriptions(code.toString())
        }

        is PlayerUiState.Loading -> {
            Loading(text = "Loading Screens", modifier = Modifier.fillMaxSize())
        }

        is PlayerUiState.Error -> {
            Error(text = s.msg, modifier = Modifier.fillMaxSize())
        }

        is PlayerUiState.Update -> {
            images = s.images
            reloadCarrousel()
        }

        is PlayerUiState.ReadyToUpdate -> {
            updateScreens()
        }

        is PlayerUiState.UpdateMarquee -> {
            coroutineScope.launch {
                playerPageViewModel.getMarquee(code.toString(), true)
            }
        }

        is PlayerUiState.UpdateError -> {
            updateCurrentIndex = false
            updatingImagesData = false
        }

        is PlayerUiState.ShowMarquee -> {
            marqueeMessage = ""
            marqueeBgColor = s.marquee.bg_color.toString()
            marqueeTextColor = s.marquee.text_color.toString()

            val ads = s.marquee.ads?.filter { ad -> ad.isEnable() }

            if (!ads.isNullOrEmpty()) {
                showMarquees = true
                ads.forEachIndexed{ idx, ad ->
                    if (ad.isEnable()) {
                        marqueeMessage = "$marqueeMessage ${ad.message}"
                        if (idx != ads.size - 1) {
                            marqueeMessage = "$marqueeMessage ***** "
                        }
                    }
                }

                if (marqueeMessage.length < 100) {
                    marqueeMessage = marqueeMessage.padStart(30).padEnd(100)
                }
            } else {
                showMarquees = false
            }
        }

        is PlayerUiState.HideMarquee -> {
            showMarquees = false
        }

        is PlayerUiState.RefreshPlayer -> {
            refreshPlayer()
        }

        is PlayerUiState.ReloadApp -> {
            goToSplashPage()
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
                    if (BuildConfig.ENV != "Prod") {
                        Text(text = "DESA")
                    }

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

        PlayerLayout(
            images = images,
            updateCurrentIndex = updateCurrentIndex,
            updating = updatingImagesData,
            marqueeBgColor = marqueeBgColor,
            marqueeTextColor = marqueeTextColor,
            marqueeMessage = marqueeMessage,
            isPortrait = isPortrait,
            showMarquees = showMarquees
        ) {
            showButtonPause = true
        }
    } else {
        Loading(text = "Waiting images for this screen", modifier = Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun PlayerLayout(
    images: Array<Images>,
    updateCurrentIndex: Boolean = false,
    updating: Boolean = false,
    showMarquees: Boolean = false,
    marqueeBgColor: String = "#000",
    marqueeTextColor: String = "#FFF",
    marqueeMessage: String = "",
    isPortrait: Boolean = false,
    onClick: () -> Unit
){
    val configuration = LocalConfiguration.current
    val screenWidthPx = configuration.screenWidthDp.dp

    val modifier = if (isPortrait) {
        Modifier.requiredHeight(screenWidthPx).graphicsLayer { rotationZ = 90F }
    } else {
        Modifier.fillMaxSize()
    }

    Box(
        modifier = modifier
            //.graphicsLayer { rotationZ = 90F } // Rotating the entire layout
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.requiredHeight(screenWidthPx)
        ) {
            // Top Box takes up the remaining space (using weight)
            Box(
                modifier = Modifier
                    .weight(1f) // Take up all the remaining space
                    .fillMaxWidth()
            ) {
                Player(
                    images = images,
                    updateCurrentIndex = updateCurrentIndex,
                    updating = updating,
                    portrait = isPortrait
                ) {
                    onClick()
                }
            }

            if (showMarquees) {
                // Bottom Box with a fixed height
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp) // Fixed height for the bottom box
                        .background(Color(android.graphics.Color.parseColor(marqueeBgColor)))
                ) {
                    Text(
                        modifier = Modifier.basicMarquee(
                            delayMillis = 500,
                            iterations = Int.MAX_VALUE,
                            spacing = MarqueeSpacing.fractionOfContainer(1f / 6f),
                            velocity = 60.dp
                        ).padding(5.dp),
                        text = marqueeMessage.uppercase(),
                        color = Color(android.graphics.Color.parseColor(marqueeTextColor)),
                        fontWeight = FontWeight.Bold,
                        fontSize = 50.sp,
                    )
                }
            }
        }
    }
}