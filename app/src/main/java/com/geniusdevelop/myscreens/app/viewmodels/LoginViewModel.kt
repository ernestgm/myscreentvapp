package com.geniusdevelop.myscreens.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.myscreens.app.api.conection.Repository
import com.geniusdevelop.myscreens.app.api.response.LoginSuccess
import com.geniusdevelop.myscreens.app.api.response.WSMessage
import io.github.centrifugal.centrifuge.DuplicateSubscriptionException
import io.github.centrifugal.centrifuge.JoinEvent
import io.github.centrifugal.centrifuge.LeaveEvent
import io.github.centrifugal.centrifuge.PublicationEvent
import io.github.centrifugal.centrifuge.SubscribedEvent
import io.github.centrifugal.centrifuge.SubscribingEvent
import io.github.centrifugal.centrifuge.Subscription
import io.github.centrifugal.centrifuge.SubscriptionErrorEvent
import io.github.centrifugal.centrifuge.SubscriptionEventListener
import io.github.centrifugal.centrifuge.UnsubscribedEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets.UTF_8

class LoginViewModel : ViewModel() {
    private lateinit var subscription: Subscription
    private val _uiState = MutableStateFlow<LoginUiState?>(null)
    val uiState: StateFlow<LoginUiState?> = _uiState


    fun authenticate(email: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                val response = Repository.user.authenticate(email, password)
                if (response.success != null) {
                    //Repository.initialize(response.success.token.toString())
                    _uiState.value = LoginUiState.Ready(response.success)
                } else {
                    val msg = response.error
                    _uiState.value = LoginUiState.Error(msg.toString())
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message.toString())
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                Repository.user.logout()
                subscription.unsubscribe()
                _uiState.value = LoginUiState.Ready()
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message.toString())
            }
        }
    }

    fun initUserSuscribe(userId: String) {

        val subListener: SubscriptionEventListener = object : SubscriptionEventListener() {
            override fun onSubscribed(sub: Subscription, event: SubscribedEvent) {
                System.out.println(("subscribed to " + sub.channel) + ", recovered " + event.recovered)
            }

            override fun onSubscribing(sub: Subscription?, event: SubscribingEvent) {
                System.out.printf("subscribing: %s%n", event.reason)
            }

            override fun onUnsubscribed(sub: Subscription, event: UnsubscribedEvent) {
                System.out.println(("unsubscribed " + sub.channel) + ", reason: " + event.reason)
            }

            override fun onError(sub: Subscription, event: SubscriptionErrorEvent) {
                System.out.println(("subscription error " + sub.channel) + " " + event.error.toString())
            }

            override fun onPublication(sub: Subscription, event: PublicationEvent) {
                val data = Json.decodeFromString<WSMessage>(String(event.data, UTF_8))
                if (data.message == "logout") {
                    logout()
                }

                System.out.println(("message from " + sub.channel) + " " + data)
            }

            override fun onJoin(sub: Subscription, event: JoinEvent) {
                println("client " + event.info.client + " joined channel " + sub.channel)
            }

            override fun onLeave(sub: Subscription, event: LeaveEvent) {
                println("client " + event.info.client + " left channel " + sub.channel)
            }
        }


        try {
            subscription = Repository.wsManager.newSubscription("user_$userId", subListener)
        } catch (e: DuplicateSubscriptionException) {
            e.printStackTrace()
            return
        }


        viewModelScope.launch {
            subscription.subscribe()
        }
    }
}

sealed interface LoginUiState {
    data object Loading : LoginUiState
    data class Error(val msg: String = "") : LoginUiState
    data class Ready(val success: LoginSuccess? = null) : LoginUiState
}