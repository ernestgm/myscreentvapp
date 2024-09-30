package com.geniusdevelop.playmyscreens.app.api.conection

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
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.nio.charset.StandardCharsets.UTF_8

class WSClient private constructor(
    val ctClient: Client
) {

    companion object {
        // Builder para configurar el cliente
        fun builder(onError: (msg: String) -> Unit): Builder {
            return Builder() { msg ->
                onError(msg)
            }
        }
    }

    class Builder(onError: (msg: String) -> Unit) {
        private lateinit var baseUrl: String
        private lateinit var secret: String
        private lateinit var userId: String
        private val opts = Options()

        private fun generateToken(userId: String, secret: String): String {
            return Jwts.builder()
                .claim("sub",userId)
                .signWith(SignatureAlgorithm.HS256, secret.toByteArray())
                .compact()
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
                onError(event.error.toString())
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
            opts.timeout = 60000

            val client = Client(baseUrl, opts, listener)
            return WSClient(client)
        }
    }

}

