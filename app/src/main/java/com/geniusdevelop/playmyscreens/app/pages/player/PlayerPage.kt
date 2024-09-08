package com.geniusdevelop.playmyscreens.app.pages.player


import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.geniusdevelop.playmyscreens.BuildConfig
import com.geniusdevelop.playmyscreens.app.api.response.Images
import com.geniusdevelop.playmyscreens.app.components.Marquee
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

    var maxHeightImagesBox by remember { mutableStateOf(1f) }
    var maxHeightMarqueeBox by remember { mutableStateOf(0f) }

    var marqueeMessage by remember { mutableStateOf("") }
    var marqueeBgColor by remember { mutableStateOf("#000000") }
    var marqueeTextColor by remember { mutableStateOf("#FFFFFF") }

    BackHandler {
        coroutineScope.launch {
            activity?.finish()
        }
    }

    DisposableEffect(Unit) {
        // Effect is triggered when HomeScreen is displayed
        coroutineScope.launch {
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
            maxHeightImagesBox = 0.90f
            maxHeightMarqueeBox = 0.10f

            marqueeMessage = ""
            marqueeBgColor = s.marquee.bg_color.toString()
            marqueeTextColor = s.marquee.text_color.toString()

            val ads = s.marquee.ads?.filter { ad -> ad.isEnable() }

            if (!ads.isNullOrEmpty()) {
                ads.forEachIndexed{ idx, ad ->
                    if (ad.isEnable()) {
                        marqueeMessage = "$marqueeMessage ${ad.message}"
                        if (idx != ads.size - 1) {
                            marqueeMessage = "$marqueeMessage ***** "
                        }
                    }
                }
            } else {
                maxHeightImagesBox = 1f
                maxHeightMarqueeBox = 0f
            }

            if (!s.isUpdate) {
                playerPageViewModel.getContents(code.toString())
            }
        }

        is PlayerUiState.HideMarquee -> {
            maxHeightImagesBox = 1f
            maxHeightMarqueeBox = 0f
            if (!s.isUpdate) {
                playerPageViewModel.getContents(code.toString())
            }
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

        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            // Create references for the composables to constrain
            val (topBox, bottomBox) = createRefs()
            Box(
                modifier = Modifier
                    .constrainAs(topBox) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(bottomBox.top)
                    }
                    .fillMaxHeight(maxHeightImagesBox)
                    .fillMaxWidth()
                    .zIndex(2f)
            ) {
                PlayerCarousel(
                    images = images,
                    updateCurrentIndex = updateCurrentIndex,
                    updating = updatingImagesData
                ) {
                    showButtonPause = true
                }
            }
            Box(
                modifier = Modifier
                    .constrainAs(bottomBox) {
                        top.linkTo(topBox.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .fillMaxWidth(1f)
                    .fillMaxHeight(maxHeightMarqueeBox)
                    .zIndex(1f)
            ) {
                Marquee(
                    text = marqueeMessage,
                    textColor = marqueeTextColor,
                    bgColor = marqueeBgColor,
                )
            }
        }
    } else {
        Loading(text = "Waiting images for this screen", modifier = Modifier.fillMaxSize())
    }
}


