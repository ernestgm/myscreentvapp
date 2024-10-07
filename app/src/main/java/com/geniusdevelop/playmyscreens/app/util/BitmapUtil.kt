package com.geniusdevelop.playmyscreens.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

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

    fun generateQRCode(text: String): Bitmap {
        val width = 500 // Width of the QR code
        val height = 500 // Height of the QR code
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }

        return bitmap
    }
}