package com.geniusdevelop.playmyscreens.app.pages.player.video

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.REPEAT_MODE_OFF
import androidx.media3.common.Player.REPEAT_MODE_ONE
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

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
    val exoPlayer = remember { createExoPlayer(context, uri) }

    var isPlaying by remember { mutableStateOf(true) }

    LaunchedEffect(key1 = repeat) {
        exoPlayer.repeatMode = if (repeat) REPEAT_MODE_ONE else REPEAT_MODE_OFF
        exoPlayer.play()
    }

    DisposableEffect(Unit) {
        exoPlayer.prepare()
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    onFinish()
                }
            }
        }
        exoPlayer.addListener(listener)
        exoPlayer.playWhenReady = isPlaying
        onDispose {
            exoPlayer.release()
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
                    useController = slide
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    setShowRewindButton(false)
                    setShowFastForwardButton(false)

                    // Hide specific UI elements
                    findViewById<View>(androidx.media3.ui.R.id.exo_progress)?.visibility = View.GONE
                    findViewById<View>(androidx.media3.ui.R.id.exo_bottom_bar)?.visibility = View.GONE
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )
    }
}

private fun createExoPlayer(context: Context, uri: String): ExoPlayer {
    return ExoPlayer.Builder(context).build().apply {
        setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
    }
}
