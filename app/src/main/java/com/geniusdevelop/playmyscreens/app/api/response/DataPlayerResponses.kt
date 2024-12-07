package com.geniusdevelop.playmyscreens.app.api.response

import android.graphics.Bitmap
import com.geniusdevelop.playmyscreens.app.util.BitmapUtil
import kotlinx.serialization.Serializable

@Serializable
data class Images (
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val description_position: String? = null,
    val description_size: String? = null,
    val image: String? = null,
    val video: String? = null,
    val screen_id: String? = null,
    val qr_info: String? = null,
    val qr_position: String? = null,
    val duration: Int? = null,
) {
    fun getImageBitmap(): Bitmap? {
        return BitmapUtil.getImageByBase64(image.toString())
    }

    fun isVideo(): Boolean {
        return image.isNullOrEmpty()
    }
}

@Serializable
data class CheckScreenUpdateResponse(
    val success: String? = null,
    val screen: Screen? = null,
    val device: Device? = null,
)

@Serializable
data class CheckMarqueeUpdateResponse(
    val success: String? = null,
    val marquee: Marquee? = null
)

@Serializable
data class CheckQrUpdateResponse(
    val success: String? = null,
    val qr: QR? = null
)

@Serializable
data class Screen (
    val id: Int? = null,
    val name: String? = null,
    val description_position: String? = null,
    val description_size: String? = null,
    val enabled: Int? = null,
    val images: Array<Images>? = null
) {
    fun isEnable(): Boolean {
        return enabled == 1
    }
}

@Serializable
data class Marquee (
    val id: Int? = null,
    val name: String? = null,
    val bg_color: String? = null,
    val text_color: String? = null,
    val ads: Array<Ad>? = null
)

@Serializable
data class QR (
    val id: Int? = null,
    val name: String? = null,
    val message: String? = null,
    val info: String? = null,
    val position: String? = null
)

@Serializable
data class Ad (
    val id: String? = null,
    val message: String? = null,
    val enabled: Int? = null,
) {
    fun isEnable(): Boolean {
        return enabled == 1
    }
}