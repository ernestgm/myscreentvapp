package com.geniusdevelop.playmyscreens.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

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

    fun isAspectRatio9x16(width: Int, height: Int): Boolean {
        val aspectRatio = width.toDouble() / height.toDouble()
        val targetRatio = 9.0 / 16.0

        // Compara la relación de aspecto con 16:9 con un margen de tolerancia
        val tolerance = 0.01
        return kotlin.math.abs(aspectRatio - targetRatio) < tolerance
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateQRCode(text: String, size: Int, margin: Int = 0): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val hints = mapOf(
            EncodeHintType.MARGIN to margin // Control the margin size
        )
        val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size, size, hints)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        //val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGBA_F16)
        for (x in 0 until size - margin) {
            for (y in 0 until size - margin) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }
}