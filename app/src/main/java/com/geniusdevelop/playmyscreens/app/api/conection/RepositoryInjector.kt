package com.geniusdevelop.playmyscreens.app.api.conection

import android.content.Context
import com.geniusdevelop.playmyscreens.BuildConfig
import com.geniusdevelop.playmyscreens.app.api.response.ConfigFields
import com.geniusdevelop.playmyscreens.app.util.DeviceUtils
import com.geniusdevelop.playmyscreens.app.util.StringConstants

internal object RepositoryInjector {
    fun initialize(context: Context, token: String?, config: ConfigFields) {
        Factory.newApiClient(context, token, config)
    }

    fun refreshTokenApiClient(token: String?) {
        Factory.refreshTokenApiClient(token, ApiManager.getInstance())
    }

    fun initializeWs(context: Context, config: ConfigFields, onFinish: () -> Unit , onError: (msg: String) -> Unit) {
        Factory.newWSClient(
            context,
            config,
            onFinish = { onFinish() },
            onError = { msg ->
                onError(msg)
            }
        )
    }

    fun initializeConfigRepository(context: Context) {
        Factory.newConfigClient(context)
    }

    fun getRepositoryContent(): IRepositoryContent {
        return ApiManager.getInstance()
    }

    fun getRepositoryUser(): IRepositoryUser {
        return ApiManager.getInstance()
    }

    fun getWSRepository(): WSManager{
        return WSManager.getInstance()
    }

    fun getConfigRepository(): IRepositoryConfiguration {
        return ApiConfigurationManager.getInstance()
    }

    private object Factory {
        fun newApiClient(context: Context, token: String?, config: ConfigFields): IRepositoryContent {
            val client = Client.builder()
                .addBaseUrl(config.screen_server_api_endpoint)
                .useBearerToken(!token.isNullOrEmpty())
                .addToken(token.toString())
                .init()

            return ApiManager.initialize(context, client)
        }

        fun refreshTokenApiClient(token: String?, apiManager: ApiManager) {
            apiManager.refreshClientToken(token)
        }

        fun newWSClient(context: Context, config: ConfigFields, onFinish: () -> Unit, onError: (msg: String) -> Unit): WSManager {
            val deviceID = DeviceUtils(context).getDeviceId()
            val client  = WSClient.builder(
                    onFinish = { onFinish() },
                    onError = { msg ->
                        onError(msg)
                    }
                )
                .addBaseUrl(config.centrifugue_base_url)
                .addSecret(config.centrifuge_pass_secret)
                .addUserId(deviceID)
                .init()

            client.ctClient.connect()

            return WSManager.initialize(context, client)
        }

        fun newConfigClient(context: Context): IRepositoryConfiguration {
            val client = Client.builder()
                .addBaseUrl(StringConstants.ApiConfiguration.BASE_URL)
                .useBasicAuthCredentials(true)
                .addUser(StringConstants.ApiConfiguration.USER)
                .addPassword(StringConstants.ApiConfiguration.PASSWORD)
                .init()

            return ApiConfigurationManager.initialize(context, client)
        }
    }
}