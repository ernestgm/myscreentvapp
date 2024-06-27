package com.geniusdevelop.myscreens.app.api.conection

import com.geniusdevelop.myscreens.app.api.request.LoginRequest
import com.geniusdevelop.myscreens.app.api.response.LoginResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class Client private constructor(
    val httpClient: HttpClient,
    val baseUrl: String,
    private val token: String? = null
) {

    suspend inline fun <reified T> post(url: String, request: Any?): T {
        return httpClient.post {
            url(baseUrl+url)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    companion object {
        // Builder para configurar el cliente
        fun builer(): Builder {
            return Builder()
        }
    }

    class Builder() {
        private lateinit var baseUrl: String
        private var token: String? = null

        fun addBaseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        fun addToken(token: String?): Builder {
            this.token = token
            return this
        }

        fun init(): Client {

            val httpClient = HttpClient(Android) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    })
                }
            }

            return Client(
                httpClient,
                baseUrl,
                token
            )
        }
    }

}

