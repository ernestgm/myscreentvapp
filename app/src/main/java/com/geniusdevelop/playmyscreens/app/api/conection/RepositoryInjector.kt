package com.geniusdevelop.playmyscreens.app.api.conection

import android.content.Context
import com.geniusdevelop.playmyscreens.BuildConfig
import com.geniusdevelop.playmyscreens.app.util.DeviceUtils

internal object RepositoryInjector {
    fun initialize(context: Context, token: String?, onError: (msg: String) -> Unit) {
        Factory.newApiClient(context, token)
        Factory.newWSClient(context) { msg ->
            onError(msg)
        }
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

    private object Factory {
        fun newApiClient(context: Context, token: String?): IRepositoryContent {

            val client = Client.builder()
                .addBaseUrl(BuildConfig.BASE_URL)
                .addToken(token)
                .init()

            return ApiManager.initialize(context, client)
        }

        fun newWSClient(context: Context, onError: (msg: String) -> Unit): WSManager {
            val deviceID = DeviceUtils(context).getDeviceId()
            val client  = WSClient.builder { msg ->
                    onError(msg)
                }
                .addBaseUrl(BuildConfig.WS_BASE_URL)
                .addSecret(BuildConfig.WS_SECRET)
                .addUserId(deviceID)
                .init()

            client.ctClient.connect()

            return WSManager.initialize(context, client)
        }
    }
}