package com.geniusdevelop.playmyscreens.app.api.conection

import android.content.Context
import android.util.Log
import com.geniusdevelop.playmyscreens.app.api.response.ConfigFields


object Repository {
    val api: IRepositoryContent
        get() = RepositoryInjector.getRepositoryContent()

    val user: IRepositoryUser
        get() = RepositoryInjector.getRepositoryUser()

    val wsManager: WSManager
        get() = RepositoryInjector.getWSRepository()

    val configurations: IRepositoryConfiguration
        get() = RepositoryInjector.getConfigRepository()

    fun initialize(context: Context, configFields: ConfigFields, token: String? = null) {
        RepositoryInjector.initialize(context, token, configFields)
    }

    fun refreshApiToken(token: String? = null) {
        RepositoryInjector.refreshTokenApiClient(token)
    }

    fun initializeApiConfig(context: Context) {
        RepositoryInjector.initializeConfigRepository(context)
    }

    fun initializeWs(context: Context, configFields: ConfigFields, onError: (msg: String) -> Unit) {
        RepositoryInjector.initializeWs(context, configFields) { msg ->
            onError(msg)
        }
    }
}