package com.geniusdevelop.playmyscreens.app.api.conection

import com.geniusdevelop.playmyscreens.app.api.response.ConfigFields

interface IRepositoryConfiguration {
    suspend fun getConfiguration(env: String): ConfigFields?
}
