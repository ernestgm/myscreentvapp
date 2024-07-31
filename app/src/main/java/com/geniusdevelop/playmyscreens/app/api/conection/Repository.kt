package com.geniusdevelop.playmyscreens.app.api.conection

import android.content.Context


object Repository {
    val api: IRepositoryContent
        get() = RepositoryInjector.getRepositoryContent()

    val user: IRepositoryUser
        get() = RepositoryInjector.getRepositoryUser()

    val wsManager: WSManager
        get() = RepositoryInjector.getWSRepository()

    fun initialize(context: Context, token: String? = null, onError: (msg: String) -> Unit) {
        RepositoryInjector.initialize(context, token) { msg ->
            onError(msg)
        }
    }
}