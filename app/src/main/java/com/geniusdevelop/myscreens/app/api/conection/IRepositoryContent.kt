package com.geniusdevelop.myscreens.app.api.conection

import com.geniusdevelop.myscreens.app.api.models.ImageList
import com.geniusdevelop.myscreens.app.api.response.CheckScreenUpdateResponse
import com.geniusdevelop.myscreens.app.api.response.GetImagesResponse
import com.geniusdevelop.myscreens.app.api.response.SetDeviceIDResponse

interface IRepositoryContent {
    suspend fun setDeviceID(userId: String): SetDeviceIDResponse
    suspend fun getContents(deviceCode: String): ImageList
    suspend fun getImagesByScreenCode(deviceCode: String): GetImagesResponse
    suspend fun checkScreenUpdated(screenId: String, updatedAt: String): CheckScreenUpdateResponse
    suspend fun checkExistScreenByCode(code: String): CheckScreenUpdateResponse
}
