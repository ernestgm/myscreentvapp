package com.geniusdevelop.myscreens.app.api.conection

import android.content.Context
import com.geniusdevelop.myscreens.app.api.models.ImageList
import com.geniusdevelop.myscreens.app.api.models.User
import com.geniusdevelop.myscreens.app.api.request.LoginRequest
import com.geniusdevelop.myscreens.app.api.request.SetIdRequest
import com.geniusdevelop.myscreens.app.api.response.CheckScreenUpdateResponse
import com.geniusdevelop.myscreens.app.api.response.GetCodeResponse
import com.geniusdevelop.myscreens.app.api.response.GetImagesResponse
import com.geniusdevelop.myscreens.app.api.response.LoginResponse
import com.geniusdevelop.myscreens.app.api.response.LogoutResponse
import com.geniusdevelop.myscreens.app.api.response.SetDeviceIDResponse
import com.geniusdevelop.myscreens.app.api.response.SetIdResponse
import com.geniusdevelop.myscreens.app.repositories.MovieDataSource
import com.geniusdevelop.myscreens.app.util.DeviceUtils
import com.google.jetstream.data.util.AssetsReader
import io.github.centrifugal.centrifuge.Client
import io.github.centrifugal.centrifuge.Subscription
import io.github.centrifugal.centrifuge.SubscriptionEventListener
import io.github.centrifugal.centrifuge.SubscriptionOptions
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class WSManager internal constructor(
    private val context: Context,
    private val client: WSClient
) {

    fun newSubscription(channel: String, subscriptionEventListener: SubscriptionEventListener): Subscription {
        return client.ctClient.newSubscription(channel, SubscriptionOptions(), subscriptionEventListener)
    }

    fun connect() {
        client.ctClient.connect()
    }

    fun disconnect() {
        client.ctClient.disconnect()
    }

    fun removeSubscription(sub: Subscription) {
        client.ctClient.removeSubscription(sub)
    }

    fun close(i: Long): Boolean {
        return client.ctClient.close(i)
    }


    companion object {
        private lateinit var instance: WSManager
        private const val CLIENT_NOT_INITIALIZED = "ApiManager not initialized"

        fun initialize(
            context: Context,
            client: WSClient
        ): WSManager {
            synchronized(WSManager::class.java) {
                instance = WSManager(context, client)
            }
            return instance
        }

        fun getInstance(): WSManager {
            if (!Companion::instance.isInitialized) {
                throw Exception(CLIENT_NOT_INITIALIZED)
            } else {
                return instance
            }
        }
    }


}