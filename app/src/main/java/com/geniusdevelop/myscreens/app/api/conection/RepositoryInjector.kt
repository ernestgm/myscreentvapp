package com.geniusdevelop.myscreens.app.api.conection

import android.content.Context
import com.geniusdevelop.myscreens.BuildConfig

internal object RepositoryInjector {
    fun initialize(context: Context, token: String?) {
        Factory.newApiClient(context, token)
    }

    fun getRepositoryContent(): IRepositoryContent {
        return ApiManager.getInstance()
    }

    fun getRepositoryUser(): IRepositoryUser {
        return ApiManager.getInstance()
    }

    private object Factory {
        fun newApiClient(context: Context, token: String?): IRepositoryContent {

            val client = Client.builer()
                .addBaseUrl(BuildConfig.BASE_URL)
                .addToken(token)
                .init()

            return ApiManager.initialize(context, client)
        }
    }
}