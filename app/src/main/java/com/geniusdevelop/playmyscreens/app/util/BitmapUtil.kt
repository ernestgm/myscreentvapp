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
}