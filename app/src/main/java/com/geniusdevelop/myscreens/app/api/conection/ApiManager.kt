package com.geniusdevelop.myscreens.app.api.conection

import android.content.Context
import com.geniusdevelop.myscreens.app.api.models.User
import com.geniusdevelop.myscreens.app.api.request.LoginRequest
import com.geniusdevelop.myscreens.app.api.request.SetIdRequest
import com.geniusdevelop.myscreens.app.api.response.GetCodeResponse
import com.geniusdevelop.myscreens.app.api.response.LoginResponse
import com.geniusdevelop.myscreens.app.api.response.LogoutResponse
import com.geniusdevelop.myscreens.app.api.response.SetDeviceIDResponse
import com.geniusdevelop.myscreens.app.api.response.SetIdResponse
import com.geniusdevelop.myscreens.app.util.DeviceUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow

class ApiManager internal constructor(
    private val context: Context,
    private val client: Client
) : IRepositoryUser, IRepositoryContent {

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
            val response = client.post<SetIdResponse>("/device", SetIdRequest(code, deviceID, userId))

            if (response.success.toString() == "success") {
                return SetDeviceIDResponse(success = true, code = code)
            } else {
                if (!response.data?.code.isNullOrEmpty()) {
                    setDeviceID(userId)
                } else {
                    return SetDeviceIDResponse(error = true, message = "Este dispositivo ya tiene un Codigo Asignado!!!")
                }
            }
        }

        return SetDeviceIDResponse(success = true, code = code)
    }

    private suspend fun getDeviceCode(deviceID: String, userId: String): String {
        var code = ""
        val response = client.get<GetCodeResponse>("/device?device_id=$deviceID&user_id=$userId")
        code = if (response.data != null) {
            response.data.code.toString()
        } else {
            ""
        }

        return code
    }

    override suspend fun getUserSessions(): User {
        TODO("Not yet implemented")
    }

    override suspend fun getSessionToken(): String {
        TODO("Not yet implemented")
    }

    companion object {
        private lateinit var instance: ApiManager
        private const val CLIENT_NOT_INITIALIZED = "ApiManager not initialized"

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
                throw Exception(CLIENT_NOT_INITIALIZED)
            } else {
                return instance
            }
        }
    }


}