package com.geniusdevelop.playmyscreens.app.api.conection

import com.geniusdevelop.playmyscreens.app.api.models.ImageList
import com.geniusdevelop.playmyscreens.app.api.response.CheckScreenUpdateResponse
import com.geniusdevelop.playmyscreens.app.api.response.GetImagesResponse
import com.geniusdevelop.playmyscreens.app.api.response.SetDeviceIDResponse

interface IRepositoryContent {
    suspend fun setDeviceID(userId: String): SetDeviceIDResponse
    suspend fun getContents(deviceCode: String): ImageList
    suspend fun getImagesByScreenCode(deviceCode: String): GetImagesResponse
    suspend fun checkScreenUpdated(screenId: String, updatedAt: String): CheckScreenUpdateResponse
    suspend fun getDataScreenByDeviceCode(code: String): CheckScreenUpdateResponse
}
