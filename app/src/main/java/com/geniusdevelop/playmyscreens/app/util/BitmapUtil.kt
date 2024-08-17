package com.geniusdevelop.playmyscreens.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

object BitmapUtil {
    fun getImageByBase64(image: String): Bitmap? {
        val pureBase64Encode = image.substring(image.indexOf(",")  + 1)
        val decodedString = Base64.decode(pureBase64Encode, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun isAspectRatio16x9(width: Int, height: Int): Boolean {
        val aspectRatio = width.toDouble() / height.toDouble()
        val targetRatio = 16.0 / 9.0

        // Compara la relación de aspecto con 16:9 con un margen de tolerancia
        val tolerance = 0.01
        return kotlin.math.abs(aspectRatio - targetRatio) < tolerance
    }
}