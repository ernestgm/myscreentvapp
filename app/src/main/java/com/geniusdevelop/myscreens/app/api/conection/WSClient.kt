package com.geniusdevelop.myscreens.app.api.conection

import android.util.Log
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.github.centrifugal.centrifuge.Client
import io.github.centrifugal.centrifuge.ConnectedEvent
import io.github.centrifugal.centrifuge.ConnectingEvent
import io.github.centrifugal.centrifuge.ConnectionTokenEvent
import io.github.centrifugal.centrifuge.ConnectionTokenGetter
import io.github.centrifugal.centrifuge.DisconnectedEvent
import io.github.centrifugal.centrifuge.ErrorEvent
import io.github.centrifugal.centrifuge.EventListener
import io.github.centrifugal.centrifuge.MessageEvent
import io.github.centrifugal.centrifuge.Options
import io.github.centrifugal.centrifuge.ServerJoinEvent
import io.github.centrifugal.centrifuge.ServerLeaveEvent
import io.github.centrifugal.centrifuge.ServerPublicationEvent
import io.github.centrifugal.centrifuge.ServerSubscribedEvent
import io.github.centrifugal.centrifuge.ServerSubscribingEvent
import io.github.centrifugal.centrifuge.ServerUnsubscribedEvent
import io.github.centrifugal.centrifuge.TokenCallback
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets.UTF_8
import java.util.Date

class WSClient private constructor(
    val ctClient: Client
) {

    companion object {
        // Builder para configurar el cliente
        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder() {
        private lateinit var baseUrl: String
        private lateinit var secret: String
        private lateinit var userId: String
        private val opts = Options()

        private fun generateToken(userId: String, secret: String): String {
            val algorithm = Algorithm.HMAC256(secret)
            return JWT.create()
                .withClaim("sub", userId)
                .sign(algorithm)
        }

        fun addBaseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        fun addSecret(secret: String): Builder {
            this.secret = secret
            return this
        }

        fun addUserId(userId: String): Builder {
            this.userId = userId
            return this
        }


        private val listener: EventListener = object : EventListener() {
            override fun onConnected(client: Client?, event: ConnectedEvent) {
                System.out.printf("connected with client id %s%n", event.client)
            }

            override fun onConnecting(client: Client?, event: ConnectingEvent) {
                System.out.printf("connecting: %s%n", event.reason)
            }

            override fun onDisconnected(client: Client?, event: DisconnectedEvent) {
                System.out.printf("disconnected %d %s%n", event.code, event.reason)
            }

            override fun onError(client: Client?, event: ErrorEvent) {
                System.out.printf("connection error: %s%n", event.error.toString())
            }

            override fun onMessage(client: Client?, event: MessageEvent) {
                val data = String(event.data, UTF_8)
                println("message received: $data")
            }

            override fun onSubscribed(client: Client?, event: ServerSubscribedEvent) {
                println("server side subscribed: " + event.channel + ", recovered " + event.recovered)
            }

            override fun onSubscribing(client: Client?, event: ServerSubscribingEvent) {
                println("server side subscribing: " + event.channel)
            }

            override fun onUnsubscribed(client: Client?, event: ServerUnsubscribedEvent) {
                println("server side unsubscribed: " + event.channel)
            }

            override fun onPublication(client: Client?, event: ServerPublicationEvent) {
                val data = String(event.data, UTF_8)
                println("server side publication: " + event.channel + ": " + data)
            }

            override fun onJoin(client: Client?, event: ServerJoinEvent) {
                println("server side join: " + event.channel + " from client " + event.info.client)
            }

            override fun onLeave(client: Client?, event: ServerLeaveEvent) {
                println("server side leave: " + event.channel + " from client " + event.info.client)
            }
        }

        private val refreshToken = object : ConnectionTokenGetter() {
            override fun getConnectionToken(event: ConnectionTokenEvent, callback: TokenCallback) {
                val token = generateToken(userId, secret)
                callback.Done(null, token)
            }
        }


        fun init(): WSClient {
            opts.token = generateToken(userId, secret)
            opts.tokenGetter = refreshToken


            val client = Client(baseUrl, opts, listener)

            return WSClient(client)
        }
    }

}

