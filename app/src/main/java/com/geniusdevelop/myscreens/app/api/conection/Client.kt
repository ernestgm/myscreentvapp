package com.geniusdevelop.myscreens.app.api.conection

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class Client private constructor(
    val httpClient: HttpClient,
    val baseUrl: String
) {

    suspend inline fun <reified T> post(url: String, request: Any?): T {
        return httpClient.post {
            url(baseUrl+url)
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    suspend inline fun <reified T> get(url: String): T {
        return httpClient.get() {
            url(baseUrl+url)
            contentType(ContentType.Application.Json)
        }.body()
    }

    companion object {
        // Builder para configurar el cliente
        fun builder(): Builder {
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

            val httpClient = HttpClient(CIO) {
                engine {
                    requestTimeout = 0 // 0 to disable, or a millisecond value to fit your needs
                }

                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    })
                }

                if (token != null && token != "") {
                    Log.d("TOKEN", "Install bearer")
                    install(Auth) {
                        bearer {
                            loadTokens {
                                BearerTokens(
                                    accessToken = token.toString(),
                                    refreshToken = ""
                                )
                            }
                        }
                    }
                }
            }

            return Client(
                httpClient,
                baseUrl
            )
        }
    }

}

