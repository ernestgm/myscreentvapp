package com.geniusdevelop.playmyscreens.app.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object LayoutUtils {
    @Composable
    fun getAlignByPosition(position: String, portrait: Boolean): PositionControl {
        val positionControl = PositionControl
        positionControl.reset()

        when (position) {
            "tl" -> {
                positionControl.align = Alignment.TopStart
                if (portrait) {
                    positionControl.sPadding = PortraitUtils.getBorderPadding()
                }
            }
            "tc" -> {
                positionControl.align = Alignment.TopCenter
            }
            "tr" -> {
                positionControl.align = Alignment.TopEnd
                if (portrait) {
                    positionControl.ePadding = PortraitUtils.getBorderPadding()
                }
            }
            "cl" -> {
                positionControl.align = Alignment.CenterStart
                if (portrait) {
                    positionControl.sPadding = PortraitUtils.getBorderPadding()
                }
            }
            "cc" -> {
                positionControl.align = Alignment.Center
            }
            "cr" -> {
                positionControl.align = Alignment.CenterEnd
                if (portrait) {
                    positionControl.ePadding = PortraitUtils.getBorderPadding()
                }
            }
            "bl" -> {
                positionControl.align = Alignment.BottomStart
                if (portrait) {
                    positionControl.sPadding = PortraitUtils.getBorderPadding()
                }
            }
            "bc" -> {
                positionControl.align = Alignment.BottomCenter
            }
            "br" -> {
                positionControl.align = Alignment.BottomEnd
                if (portrait) {
                    positionControl.ePadding = PortraitUtils.getBorderPadding()
                }
            }
            else -> {
                positionControl.align = Alignment.BottomEnd
                if (portrait) {
                    positionControl.ePadding = PortraitUtils.getBorderPadding()
                }
            }
        }

        return positionControl
    }
}

object PositionControl {
    var align: Alignment = Alignment.BottomEnd
    var sPadding: Dp = 5.dp
    var tPadding: Dp = 5.dp
    var ePadding: Dp = 5.dp
    var bPadding: Dp = 5.dp

    fun reset() {
        align = Alignment.BottomEnd
        sPadding = 5.dp
        tPadding = 5.dp
        ePadding = 5.dp
        bPadding = 5.dp
    }
}