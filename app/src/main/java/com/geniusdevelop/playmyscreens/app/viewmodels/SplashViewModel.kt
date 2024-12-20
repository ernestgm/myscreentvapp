package com.geniusdevelop.playmyscreens.app.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.playmyscreens.BuildConfig
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.api.response.ConfigFields
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
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets.UTF_8


class SplashViewModel : ViewModel() {
    private lateinit var onlineStatusSubscription: Subscription
    private val _uiState = MutableStateFlow<HomeScreenUiState?>(null)
    val uiState: StateFlow<HomeScreenUiState?> = _uiState

    fun setAppOnline() {
        println("init subscribed Status Online")
        val subListener: SubscriptionEventListener = object : SubscriptionEventListener() {
            override fun onSubscribed(sub: Subscription, event: SubscribedEvent) {
                println(("subscribed to " + sub.channel) + ", recovered " + event.recovered)
            }

            override fun onSubscribing(sub: Subscription?, event: SubscribingEvent) {
                System.out.printf("subscribing: %s%n", event.reason)
            }

            override fun onUnsubscribed(sub: Subscription, event: UnsubscribedEvent) {
                println(("unsubscribed " + sub.channel) + ", reason: " + event.reason)
            }

            override fun onError(sub: Subscription, event: SubscriptionErrorEvent) {
                println(("subscription error " + sub.channel) + " " + event.error.toString())
            }

            override fun onPublication(sub: Subscription, event: PublicationEvent) {
                // val data = Json.decodeFromString<WSMessage>(String(event.data, UTF_8))
                println(("message from " + sub.channel) + " " + String(event.data, UTF_8))
            }

            override fun onJoin(sub: Subscription, event: JoinEvent) {
                println("client " + event.info.client + " joined channel " + sub.channel)
            }

            override fun onLeave(sub: Subscription, event: LeaveEvent) {
                println("client " + event.info.client + " left channel " + sub.channel)
            }
        }

        try {
            if (!::onlineStatusSubscription.isInitialized) {
                println("init subscribe Splash Status")
                onlineStatusSubscription = Repository.wsManager.newSubscription("status:appOnline", subListener)
            }
        } catch (e: DuplicateSubscriptionException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            FirebaseCrashlytics.getInstance().log("Splash Status duplicate")
            e.printStackTrace()
        }


        viewModelScope.launch {
            if (::onlineStatusSubscription.isInitialized) {
                onlineStatusSubscription.subscribe()
            }
        }
    }

    fun getConfigurationByEnv () {
        _uiState.value = HomeScreenUiState.Loading
        viewModelScope.launch {
            try {
                val config = Repository.configurations.getConfiguration(BuildConfig.ENV)
                if (config == null) {
                    _uiState.value = SplashScreenUiState.Error("Load Settings Failed")
                } else {
                    config.let {
                        _uiState.value = SplashScreenUiState.Ready(it)
                    }
                }
            } catch (e: Exception) {
                showException(e)
            }
        }
    }

    private fun showException(e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        when(e) {
            is UnresolvedAddressException -> {
                _uiState.value = SplashScreenUiState.Error("Network Error: Check your internet connection.")
            }
            is NoTransformationFoundException -> {
                _uiState.value = SplashScreenUiState.Error("Network Error: Load Configuration Failed.")
            }
            is HttpRequestTimeoutException -> {
                FirebaseCrashlytics.getInstance().log("HttpRequestTimeoutException")
                _uiState.value = SplashScreenUiState.ReloadPage
            }
            is ConnectTimeoutException -> {
                FirebaseCrashlytics.getInstance().log("ConnectTimeoutException")
                _uiState.value = SplashScreenUiState.ReloadPage
            }
            else -> {
                _uiState.value = SplashScreenUiState.Error("Error: " + e.message.toString())
            }
        }
    }
}

sealed interface SplashScreenUiState {
    data object ReloadPage : HomeScreenUiState
    data object Loading : HomeScreenUiState
    data class Error(val msg: String = "") : HomeScreenUiState
    data class Ready(
        val config: ConfigFields
    ) : HomeScreenUiState
}


