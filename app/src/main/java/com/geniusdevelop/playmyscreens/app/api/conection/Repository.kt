package com.geniusdevelop.playmyscreens.app.api.conection

import android.content.Context
import android.util.Log


object Repository {
    val api: IRepositoryContent
        get() = RepositoryInjector.getRepositoryContent()

    val user: IRepositoryUser
        get() = RepositoryInjector.getRepositoryUser()

    val wsManager: WSManager
        get() = RepositoryInjector.getWSRepository()

    fun initialize(context: Context, token: String? = null) {
        RepositoryInjector.initialize(context, token)
    }

    fun initializeWs(context: Context, onError: (msg: String) -> Unit) {
        RepositoryInjector.initializeWs(context) { msg ->
            onError(msg)
        }
    }
}