package com.geniusdevelop.playmyscreens.app.api.conection

import android.content.Context
import androidx.compose.ui.text.toLowerCase
import com.geniusdevelop.playmyscreens.BuildConfig
import com.geniusdevelop.playmyscreens.app.api.response.ConfigFields
import com.geniusdevelop.playmyscreens.app.api.response.ConfigResponse

class ApiConfigurationManager internal constructor(
    private val context: Context,
    private val client: Client,
    private var configFields: ConfigFields? = null
) : IRepositoryConfiguration {

    override suspend fun getConfiguration(env: String): ConfigFields? {
        var config: ConfigFields? = null

        if (env.isEmpty()) {
            config = ConfigFields()
            config.screen_server_api_endpoint = BuildConfig.BASE_URL
            config.centrifugue_base_url = BuildConfig.WS_BASE_URL
            config.centrifuge_pass_secret = BuildConfig.WS_SECRET
            config.activate_url = BuildConfig.ACTIVATE_URL
        } else {
            val result:Array<ConfigResponse> = client.get("/screen_api_server_configurations")
            result.forEach { response ->
                if (response.slug.lowercase() == env.lowercase()) {
                    config = response.acf
                }
            }
        }

        configFields = config
        return config
    }

    override fun getConfigFields(): ConfigFields? {
        return configFields
    }

    companion object {
        private lateinit var instance: ApiConfigurationManager
        private const val CLIENT_NOT_INITIALIZED = "ApiManager not initialized"

        fun initialize(
            context: Context,
            client: Client
        ): ApiConfigurationManager {
            synchronized(ApiConfigurationManager::class.java) {
                instance = ApiConfigurationManager(context, client)
            }
            return instance
        }

        fun getInstance(): ApiConfigurationManager {
            if (!Companion::instance.isInitialized) {
                throw Exception(CLIENT_NOT_INITIALIZED)
            } else {
                return instance
            }
        }
    }
}