package com.geniusdevelop.playmyscreens.app.api.response

import android.graphics.Bitmap
import com.geniusdevelop.playmyscreens.app.util.BitmapUtil
import kotlinx.serialization.Serializable

@Serializable
data class GetImagesResponse (
    val success : String? = null,
    val screen_updated_at: String? = null,
    val data: Array<Images>? = null
)

@Serializable
data class Images (
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val screen_id: String? = null,
    val is_static: String? = null,
    val duration: Int? = null,
    val products: Array<Product>? = null
) {
    fun getImageBitmap(): Bitmap? {
        return BitmapUtil.getImageByBase64(image.toString())
    }

    fun isStatic() : Boolean {
        return is_static.toString() == "1"
    }
}

@Serializable
data class Product (
    val id: String? = null,
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    val prices: Array<Price>? = null
) {
    fun getImageBitmap(): Bitmap? {
        return BitmapUtil.getImageByBase64(image.toString())
    }
}

@Serializable
data class Price (
    val id: String? = null,
    val value: String? = null
)

@Serializable
data class CheckScreenUpdateResponse(
    val success: String? = null,
    val screen: Screen? = null
)

@Serializable
data class CheckMarqueeUpdateResponse(
    val success: String? = null,
    val marquee: Marquee? = null
)

@Serializable
data class Screen (
    val id: Int? = null,
    val name: String? = null,
    val enabled: Int? = null,
    val portrait: Int? = null,
    val images: Array<Images>? = null
) {
    fun isEnable(): Boolean {
        return enabled == 1
    }

    fun isPortrait(): Boolean {
        return portrait == 1
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
data class Ad (
    val id: String? = null,
    val message: String? = null,
    val enabled: Int? = null,
) {
    fun isEnable(): Boolean {
        return enabled == 1
    }
}