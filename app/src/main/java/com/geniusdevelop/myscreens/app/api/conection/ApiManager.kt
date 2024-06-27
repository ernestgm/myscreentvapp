package com.geniusdevelop.myscreens.app.api.conection

import com.geniusdevelop.myscreens.app.api.models.User
import com.geniusdevelop.myscreens.app.api.request.LoginRequest
import com.geniusdevelop.myscreens.app.api.response.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

class ApiManager internal constructor(
    private val client: Client
) : IRepositoryUser, IRepositoryContent {

    override suspend fun authenticate(email: String, password: String): LoginResponse {
        return client.post("/login", LoginRequest(email, password))
    }

    override suspend fun getUserSessions(): User {
        TODO("Not yet implemented")
    }

    override suspend fun getSessionToken(): String {
        TODO("Not yet implemented")
    }

    override suspend fun closeApiSession() {
        TODO("Not yet implemented")
    }

    companion object {
        private lateinit var instance: ApiManager
        private const val CLIENT_NOT_INITIALIZED = "ApiManager not initialized"

        fun initialize(
            client: Client
        ): ApiManager {
            synchronized(ApiManager::class.java) {
                instance = ApiManager(client)
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