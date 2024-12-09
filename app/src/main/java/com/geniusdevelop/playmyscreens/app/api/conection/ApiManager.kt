package com.geniusdevelop.playmyscreens.app.api.conection

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.geniusdevelop.playmyscreens.app.api.request.GenerateCodeRequest
import com.geniusdevelop.playmyscreens.app.api.request.LoginByCodeRequest
import com.geniusdevelop.playmyscreens.app.api.request.LoginRequest
import com.geniusdevelop.playmyscreens.app.api.request.SetIdRequest
import com.geniusdevelop.playmyscreens.app.api.request.SetUpdateDeviceRequest
import com.geniusdevelop.playmyscreens.app.api.response.CheckMarqueeUpdateResponse
import com.geniusdevelop.playmyscreens.app.api.response.CheckQrUpdateResponse
import com.geniusdevelop.playmyscreens.app.api.response.CheckScreenUpdateResponse
import com.geniusdevelop.playmyscreens.app.api.response.GenerateLoginCodeResponse
import com.geniusdevelop.playmyscreens.app.api.response.GetCodeResponse
import com.geniusdevelop.playmyscreens.app.api.response.LoginResponse
import com.geniusdevelop.playmyscreens.app.api.response.LogoutResponse
import com.geniusdevelop.playmyscreens.app.api.response.SetDeviceIDResponse
import com.geniusdevelop.playmyscreens.app.api.response.SetIdResponse
import com.geniusdevelop.playmyscreens.app.exceptions.ApiManagerUninitializedException
import com.geniusdevelop.playmyscreens.app.util.DeviceUtils

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

    @RequiresApi(Build.VERSION_CODES.M)
    override suspend fun setDeviceID(userId: String): SetDeviceIDResponse {
        val deviceUtils = DeviceUtils(context)
        val deviceID = deviceUtils.getDeviceId()
        var code = ""
        code = getDeviceCode(deviceID, userId)

        if (code == "") {
            code = deviceUtils.generateSixDigitRandom().toString()
            val response = client.post<SetIdResponse>(
                "/device",
                SetIdRequest(
                    deviceID,
                    code,
                    deviceID,
                    userId,
                    deviceUtils.getAppVersion(),
                    deviceUtils.getAndroidVersion(),
                    deviceUtils.hasOverlayPermission()
                ))

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

    override suspend fun getDataScreenByDeviceCode(code: String): CheckScreenUpdateResponse {
        return client.get<CheckScreenUpdateResponse>("/devices/getScreen?code=$code")
    }

    override suspend fun getMarqueeByDeviceCode(code: String): CheckMarqueeUpdateResponse {
        return client.get<CheckMarqueeUpdateResponse>("/devices/getMarquee?code=$code")
    }

    override suspend fun getQrByDeviceCode(code: String): CheckQrUpdateResponse {
        return client.get<CheckQrUpdateResponse>("/devices/getQr?code=$code")
    }

    fun refreshClientToken(token: String?) {
        this.client = client.builder.useBearerToken(!token.isNullOrEmpty()).addToken(token.toString()).init()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getDeviceCode(deviceID: String, userId: String): String {
        var code = ""
        val response = client.get<GetCodeResponse>("/devices/byId?device_id=$deviceID&user_id=$userId")

        if (response.data != null) {
            updateDataDevice(response.data.id.toString())
            code = response.data.code.toString()
        }

        return code
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun updateDataDevice(id: String) {
        val deviceUtils = DeviceUtils(context)
        client.put<SetIdResponse>(
            "/device/update/${id}",
            SetUpdateDeviceRequest(
                deviceUtils.getAppVersion(),
                deviceUtils.getAndroidVersion(),
                deviceUtils.hasOverlayPermission()
            ))
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