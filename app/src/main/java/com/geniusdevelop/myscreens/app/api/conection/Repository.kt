package com.geniusdevelop.myscreens.app.api.conection

import android.content.Context
import android.util.Log


object Repository {
    val api: IRepositoryContent
        get() = RepositoryInjector.getRepositoryContent()

    val user: IRepositoryUser
        get() = RepositoryInjector.getRepositoryUser()

    fun initialize(token: String? = null) {
        Log.d("TOKEN", token.toString())
        RepositoryInjector.initialize(token)
    }
}