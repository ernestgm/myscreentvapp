package com.geniusdevelop.playmyscreens.app.api.conection

import android.content.Context
import io.github.centrifugal.centrifuge.ClientState
import io.github.centrifugal.centrifuge.Subscription
import io.github.centrifugal.centrifuge.SubscriptionEventListener
import io.github.centrifugal.centrifuge.SubscriptionOptions

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