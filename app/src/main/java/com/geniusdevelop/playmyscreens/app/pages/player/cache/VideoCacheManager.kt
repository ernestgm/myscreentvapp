package com.geniusdevelop.playmyscreens.app.pages.player.cache

import android.content.Context
import com.geniusdevelop.playmyscreens.app.api.response.Images
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class VideoCacheManager(context: Context) {

    private val cacheDir = File(context.cacheDir, "video_cache").apply { mkdirs() }

    fun getVideo(url: String): File {
        return File(cacheDir, url.hashCode().toString())
    }

    fun cacheVideo(url: String, onProgress: (Float) -> Unit): File {
        val file = File(cacheDir, url.hashCode().toString())

        if (!file.exists()) {
            CoroutineScope(Dispatchers.IO).launch {
                // Download video
                val connection = URL(url).openConnection()
                connection.connect()
                val totalSize = connection.contentLength
                val input = connection.getInputStream()
                val output = FileOutputStream(file)

                val buffer = ByteArray(1024)
                var bytesRead: Int
                var downloadedSize = 0

                while (input.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    downloadedSize += bytesRead
                    onProgress(downloadedSize.toFloat() / totalSize)
                }

                output.close()
                input.close()
            }
        }

        return file
    }

    fun cleanUp(images: List<Images>) {
        cacheDir.listFiles()?.forEach { file ->
            if (images.none { it.video.hashCode().toString() == file.name }) {
                file.delete()
            }
        }
    }
}
