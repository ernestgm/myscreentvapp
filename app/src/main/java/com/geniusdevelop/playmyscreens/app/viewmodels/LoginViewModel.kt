package com.geniusdevelop.playmyscreens.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.api.response.LoginSuccess
import com.geniusdevelop.playmyscreens.app.api.response.WSMessage
import com.google.firebase.crashlytics.FirebaseCrashlytics
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
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets.UTF_8

class LoginViewModel : ViewModel() {
    private lateinit var linkSubscription: Subscription
    private val _uiState = MutableStateFlow<LoginUiState?>(null)
    val uiState: StateFlow<LoginUiState?> = _uiState


    fun authenticate(email: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                val response = Repository.user.authenticate(email, password)
                if (response.success != null) {
                    _uiState.value = LoginUiState.Ready(response.success)
                } else {
                    val msg = response.error
                    _uiState.value = LoginUiState.Error(msg.toString())
                }
            } catch (e: Exception) {
                showException(e)
            }
        }
    }

    fun generateLoginCode(deviceId: String) {
        viewModelScope.launch {
            try {
                val response = Repository.user.generateLoginCode(deviceId)
                if (response.code != null) {
                    _uiState.value = LoginUiState.CodeReady(response.code)
                }
            } catch (e: Exception) {
                showException(e)
            }
        }
    }

    fun loginByCode(code: String, deviceId: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                val response = Repository.user.loginByCode(code, deviceId)
                if (response.success != null) {
                    _uiState.value = LoginUiState.Ready(response.success)
                } else {
                    val msg = response.error
                    _uiState.value = LoginUiState.Error(msg.toString())
                }
            } catch (e: Exception) {
                showException(e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                Repository.user.logout()
                _uiState.value = LoginUiState.Ready()
            } catch (e: Exception) {
                showException(e)
            }
        }
    }

    private fun showException(e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        when(e) {
            is UnresolvedAddressException -> {
                _uiState.value = LoginUiState.Error("Network Error: Check your internet connection.")
            }
            is NoTransformationFoundException -> {
                _uiState.value = LoginUiState.Error("Network Error: Load Data Failed.")
            }
            else -> {
                _uiState.value = LoginUiState.Error("Error: " + e.message.toString())
            }
        }
    }

    fun initSubscribe(deviceId: String) {
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
                System.out.println(("message from " + sub.channel) + " " + data.message)
                when (sub.channel) {
                    "link_device_$deviceId" -> {
                        when (data.message) {
                            "login_by_code" -> {
                                _uiState.value = LoginUiState.LoginByCode
                            }
                        }
                    }
                }

            }

            override fun onJoin(sub: Subscription, event: JoinEvent) {
                println("client " + event.info.client + " joined channel " + sub.channel)
            }

            override fun onLeave(sub: Subscription, event: LeaveEvent) {
                println("client " + event.info.client + " left channel " + sub.channel)
            }
        }


        try {
            if (!::linkSubscription.isInitialized) {
                println("init subscribe Login")
                linkSubscription = Repository.wsManager.newSubscription("link_device_$deviceId", subListener)
            }
        } catch (e: DuplicateSubscriptionException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            FirebaseCrashlytics.getInstance().log("Login Link Suscription Duplicate")
            e.printStackTrace()
        }

        viewModelScope.launch {
            if (::linkSubscription.isInitialized) {
                linkSubscription.subscribe()
            }
        }
    }

    fun removeAllSubscriptions() {
        if (::linkSubscription.isInitialized) {
            Repository.wsManager.removeSubscription(linkSubscription)
        }
    }
}

sealed interface LoginUiState {
    data object Loading : LoginUiState
    data object LoginByCode : LoginUiState
    data class Error(val msg: String = "") : LoginUiState
    data class Ready(val success: LoginSuccess? = null) : LoginUiState
    data class CodeReady(val code: String? = null) : LoginUiState
}