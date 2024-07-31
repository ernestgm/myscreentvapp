package com.geniusdevelop.playmyscreens.app.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.api.response.WSMessage
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


class HomeScreeViewModel() : ViewModel() {

    private lateinit var screenSubscription: Subscription
    private lateinit var userSubscription: Subscription
    private val _uiState = MutableStateFlow<HomeScreenUiState?>(null)
    val uiState: StateFlow<HomeScreenUiState?> = _uiState


    fun setDeviceCode (userID: String) {
        _uiState.value = HomeScreenUiState.Loading
        viewModelScope.launch {
            try {
                val result = Repository.api.setDeviceID(userID)
                if (result.success) {
                    _uiState.value = HomeScreenUiState.Ready(result.code)
                } else if (result.error){
                    _uiState.value = HomeScreenUiState.Error(result.message)
                }
            } catch (e: Exception) {
                _uiState.value = HomeScreenUiState.Error(e.message.toString())
            }
        }
    }

    fun initSubscribeDevice(deviceCode: String) {
        System.out.println("init subscribed to")
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
                    "user_$deviceCode" -> {
                        when (data.message) {
                            "logout" -> {
                                Repository.wsManager.removeSubscription(screenSubscription)
                                Repository.wsManager.removeSubscription(userSubscription)
                                _uiState.value = HomeScreenUiState.LogoutUser
                            }
                        }
                    }
                    "home_screen_$deviceCode" -> {
                        when (data.message) {
                            "check_screen_update" -> {
                                checkExistScreenForDevice(deviceCode)
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
            screenSubscription = Repository.wsManager.newSubscription("home_screen_$deviceCode", subListener)
            userSubscription = Repository.wsManager.newSubscription("user_$deviceCode", subListener)
        } catch (e: DuplicateSubscriptionException) {
            println("duplicado ${e.message}")
            e.printStackTrace()
            return
        }


        viewModelScope.launch {
            screenSubscription.subscribe()
            userSubscription.subscribe()
        }
    }

    fun checkExistScreenForDevice(code: String) {
        viewModelScope.launch {
            try {
                val result = Repository.api.checkExistScreenByCode(code)
                if (!result.success.toBoolean()) {
                    _uiState.value = HomeScreenUiState.ExistScreen(result.success.toBoolean())
                } else {
                    if (result.enabled != null && result.enabled.toBoolean()) {
                        Repository.wsManager.removeSubscription(screenSubscription)
                        Repository.wsManager.removeSubscription(userSubscription)
                        _uiState.value = HomeScreenUiState.ExistScreen(result.success.toBoolean())
                    } else {
                        _uiState.value = HomeScreenUiState.DisabledScreen
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HomeScreenUiState.Error(e.message.toString())
            }
        }
    }
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data class Error(val msg: String = "") : HomeScreenUiState
    data class Ready(
        val deviceCode: String
    ) : HomeScreenUiState
    data class ExistScreen(val exist: Boolean): HomeScreenUiState
    data object DisabledScreen: HomeScreenUiState
    data object LogoutUser: HomeScreenUiState
}


