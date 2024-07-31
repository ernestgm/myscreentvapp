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
    val enabled: String? = null
)