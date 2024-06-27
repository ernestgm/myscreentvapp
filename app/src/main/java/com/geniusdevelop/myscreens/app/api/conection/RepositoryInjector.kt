package com.geniusdevelop.myscreens.app.api.conection

import android.content.Context
import com.geniusdevelop.myscreens.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal object RepositoryInjector {
    fun initialize() {
        Factory.newApiClient()
    }

    fun getRepositoryContent(): IRepositoryContent {
        return ApiManager.getInstance()
    }

    fun getRepositoryUser(): IRepositoryUser {
        return ApiManager.getInstance()
    }

    private object Factory {
        fun newApiClient(): IRepositoryContent {

            val client = Client.builer()
                .addBaseUrl(BuildConfig.BASE_URL)
                .addToken(null)
                .init()

            return ApiManager.initialize(client)
        }
    }
}