package com.geniusdevelop.myscreens.app.api.conection

import android.content.Context
import com.geniusdevelop.myscreens.BuildConfig
import com.geniusdevelop.myscreens.app.util.DeviceUtils

internal object RepositoryInjector {
    fun initialize(context: Context, token: String?) {
        Factory.newApiClient(context, token)
        Factory.newWSClient(context)
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

        fun newWSClient(context: Context): WSManager {
            val deviceID = DeviceUtils(context).getDeviceId()
            val client  = WSClient.builder()
                .addBaseUrl(BuildConfig.WS_BASE_URL)
                .addSecret(BuildConfig.WS_SECRET)
                .addUserId(deviceID)
                .init()

            client.ctClient.connect()

            return WSManager.initialize(context, client)
        }
    }
}