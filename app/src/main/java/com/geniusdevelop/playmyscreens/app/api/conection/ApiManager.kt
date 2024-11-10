package com.geniusdevelop.playmyscreens.app.api.conection

import android.content.Context
import com.geniusdevelop.playmyscreens.app.api.request.GenerateCodeRequest
import com.geniusdevelop.playmyscreens.app.api.request.LoginByCodeRequest
import com.geniusdevelop.playmyscreens.app.api.request.LoginRequest
import com.geniusdevelop.playmyscreens.app.api.request.SetIdRequest
import com.geniusdevelop.playmyscreens.app.api.response.CheckMarqueeUpdateResponse
import com.geniusdevelop.playmyscreens.app.api.response.CheckScreenUpdateResponse
import com.geniusdevelop.playmyscreens.app.api.response.GenerateLoginCodeResponse
import com.geniusdevelop.playmyscreens.app.api.response.GetCodeResponse
import com.geniusdevelop.playmyscreens.app.api.response.GetImagesResponse
import com.geniusdevelop.playmyscreens.app.api.response.LoginResponse
import com.geniusdevelop.playmyscreens.app.api.response.LogoutResponse
import com.geniusdevelop.playmyscreens.app.api.response.SetDeviceIDResponse
import com.geniusdevelop.playmyscreens.app.api.response.SetIdResponse
import com.geniusdevelop.playmyscreens.app.exceptions.ApiManagerUninitializedException
import com.geniusdevelop.playmyscreens.app.util.DeviceUtils
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class ApiManager internal constructor(
    private val context: Context,
    var client: Client
) : IRepositoryUser, IRepositoryContent {
    override suspend fun generateLoginCode(deviceId: String): GenerateLoginCodeResponse {
        return client.post("/generate-login-code", GenerateCodeRequest(deviceId))
    }

    override suspend fun loginByCode(code: String, deviceId: String): LoginResponse {
        return client.post("/login-with-code", LoginByCodeRequest(code, deviceId))
    }

    override suspend fun authenticate(email: String, password: String): LoginResponse {
        return client.post("/login", LoginRequest(email, password))
    }

    override suspend fun logout(): LogoutResponse {
        return client.post("/logout", null)
    }

    override suspend fun setDeviceID(userId: String): SetDeviceIDResponse {
        val deviceUtils = DeviceUtils(context)
        val deviceID = deviceUtils.getDeviceId()
        var code = ""
        code = getDeviceCode(deviceID, userId)

        if (code == "") {
            code = deviceUtils.generateSixDigitRandom().toString()
            val response = client.post<SetIdResponse>("/device", SetIdRequest(deviceID, code, deviceID, userId))

            if (response.success.toString() == "success") {
                return SetDeviceIDResponse(success = true, code = code)
            } else {
                if (!response.data?.code.isNullOrEmpty()) {
                    setDeviceID(userId)
                } else if(!response.data?.device_id.isNullOrEmpty()) {
                    return SetDeviceIDResponse(error = true, message = response.data?.device_id?.first().toString())
                } else if(response.success.toString() == "limit_device") {
                    return SetDeviceIDResponse(error = true, message = "You have exceeded the device limit")
                } else {
                    return SetDeviceIDResponse(error = true, message = "Error creating the device code")
                }
            }
        }

        return SetDeviceIDResponse(success = true, code = code)
    }

    override suspend fun getImagesByScreenCode(deviceCode: String): GetImagesResponse {
        return client.get<GetImagesResponse>("/images/byScreen?code=$deviceCode")
    }

    override suspend fun checkScreenUpdated(
        screenCode: String,
        updatedAt: String
    ): CheckScreenUpdateResponse {
        val encodeUpdateAt = URLEncoder.encode(updatedAt, StandardCharsets.UTF_8.toString())
        return client.get<CheckScreenUpdateResponse>("/screens/checkUpdatedAt?udpated_time=$encodeUpdateAt&code=$screenCode")
    }

    override suspend fun getDataScreenByDeviceCode(code: String): CheckScreenUpdateResponse {
        return client.get<CheckScreenUpdateResponse>("/devices/getScreen?code=$code")
    }

    override suspend fun getMarqueeByDeviceCode(code: String): CheckMarqueeUpdateResponse {
        return client.get<CheckMarqueeUpdateResponse>("/devices/getMarquee?code=$code")
    }

    fun refreshClientToken(token: String?) {
        this.client = client.builder.useBearerToken(!token.isNullOrEmpty()).addToken(token.toString()).init()
    }

    private suspend fun getDeviceCode(deviceID: String, userId: String): String {
        var code = ""
        val response = client.get<GetCodeResponse>("/devices/byId?device_id=$deviceID&user_id=$userId")
        code = if (response.data != null) {
            response.data.code.toString()
        } else {
            ""
        }

        return code
    }

    companion object {
        private lateinit var instance: ApiManager

        fun initialize(
            context: Context,
            client: Client
        ): ApiManager {
            synchronized(ApiManager::class.java) {
                instance = ApiManager(context, client)
            }
            return instance
        }

        fun getInstance(): ApiManager {
            if (!Companion::instance.isInitialized) {
                throw ApiManagerUninitializedException()
            } else {
                return instance
            }
        }
    }


}