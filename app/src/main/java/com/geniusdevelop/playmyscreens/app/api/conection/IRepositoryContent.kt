package com.geniusdevelop.playmyscreens.app.api.conection

import com.geniusdevelop.playmyscreens.app.api.response.CheckMarqueeUpdateResponse
import com.geniusdevelop.playmyscreens.app.api.response.CheckQrUpdateResponse
import com.geniusdevelop.playmyscreens.app.api.response.CheckScreenUpdateResponse
import com.geniusdevelop.playmyscreens.app.api.response.SetDeviceIDResponse

interface IRepositoryContent {
    suspend fun setDeviceID(userId: String): SetDeviceIDResponse
    suspend fun getDataScreenByDeviceCode(code: String): CheckScreenUpdateResponse
    suspend fun getMarqueeByDeviceCode(code: String): CheckMarqueeUpdateResponse
    suspend fun getQrByDeviceCode(code: String): CheckQrUpdateResponse
}
