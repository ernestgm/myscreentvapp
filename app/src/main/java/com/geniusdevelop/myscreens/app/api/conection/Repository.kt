package com.geniusdevelop.myscreens.app.api.conection

import android.content.Context


object Repository {
    val api: IRepositoryContent
        get() = RepositoryInjector.getRepositoryContent()

    val user: IRepositoryUser
        get() = RepositoryInjector.getRepositoryUser()

    fun initialize() {
        RepositoryInjector.initialize()
    }
}