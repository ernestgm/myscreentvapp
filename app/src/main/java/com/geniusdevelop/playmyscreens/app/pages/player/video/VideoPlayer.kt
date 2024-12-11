package com.geniusdevelop.playmyscreens.app.pages.player.video

import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.geniusdevelop.playmyscreens.app.pages.player.cache.VideoCacheManager
import com.geniusdevelop.playmyscreens.app.util.focusOnInitialVisibility

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    slide: Boolean = false,
    portrait: Boolean = false,
    repeat: Boolean = false,
    uri: String,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val videoCacheManager = remember { VideoCacheManager(context) }
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(ExoPlayer.Builder(context).build()) }
    var isCaching by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = repeat) {
        if (exoPlayer != null) {
            exoPlayer?.repeatMode = if (repeat) REPEAT_MODE_ONE else REPEAT_MODE_OFF
        }
    }

    LaunchedEffect(key1 = uri) {
        if (exoPlayer != null) {
            val cachedFile = videoCacheManager.getVideo(uri)
            if (cachedFile.exists()) {
                exoPlayer?.setMediaItem(MediaItem.fromUri(cachedFile.toUri()))
            } else {
                isCaching = true
                val cachedVideo = videoCacheManager.cacheVideo(uri) { progress ->
                    println("Download Progress: ${progress * 100}%")
                }
                exoPlayer?.setMediaItem(MediaItem.fromUri(cachedVideo.toUri()))
                isCaching = false
            }
            exoPlayer?.prepare()
            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        onFinish()
                    }
                }
            }
            exoPlayer?.addListener(listener)
            exoPlayer?.playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (exoPlayer != null) {
                exoPlayer?.playWhenReady = false
                exoPlayer?.release()
                exoPlayer = null
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = true
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    setShowRewindButton(false)
                    setShowFastForwardButton(false)

                    if (!slide) {
                        controllerShowTimeoutMs = 1
                        findViewById<View>(androidx.media3.ui.R.id.exo_play_pause)?.visibility = View.GONE
                    }
                    // Hide specific UI elements
                    findViewById<View>(androidx.media3.ui.R.id.exo_progress)?.visibility = View.GONE
                    findViewById<View>(androidx.media3.ui.R.id.exo_bottom_bar)?.visibility = View.GONE
                }
            },
            update = {
                it.player = exoPlayer
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )
    }
}
