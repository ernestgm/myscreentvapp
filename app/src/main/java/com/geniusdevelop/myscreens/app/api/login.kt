package com.geniusdevelop.myscreens.app.api


import com.geniusdevelop.myscreens.app.api.request.LoginRequest
import com.geniusdevelop.myscreens.app.api.response.LoginResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation

import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.request.*

import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

val httpClient = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
        })
    }
}

suspend fun login(email: String, password: String): LoginResponse {
    return httpClient.post {
        url("http://10.0.2.2/laravel/screen-server/public/api/v1/login")
        contentType(ContentType.Application.Json)
        setBody(LoginRequest(email, password))
    }.body()
}