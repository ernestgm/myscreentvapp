package com.geniusdevelop.myscreens.app.api.conection

import com.geniusdevelop.myscreens.app.api.response.SetDeviceIDResponse

interface IRepositoryContent {
    suspend fun setDeviceID(userId: String): SetDeviceIDResponse
}
