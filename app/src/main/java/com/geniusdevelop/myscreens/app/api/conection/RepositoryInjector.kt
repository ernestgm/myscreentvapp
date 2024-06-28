package com.geniusdevelop.myscreens.app.api.conection

import com.geniusdevelop.myscreens.BuildConfig

internal object RepositoryInjector {
    fun initialize(token: String?) {
        Factory.newApiClient(token)
    }

    fun getRepositoryContent(): IRepositoryContent {
        return ApiManager.getInstance()
    }

    fun getRepositoryUser(): IRepositoryUser {
        return ApiManager.getInstance()
    }

    private object Factory {
        fun newApiClient(token: String?): IRepositoryContent {

            val client = Client.builer()
                .addBaseUrl(BuildConfig.BASE_URL)
                .addToken(token)
                .init()

            return ApiManager.initialize(client)
        }
    }
}