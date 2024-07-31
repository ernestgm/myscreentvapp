package com.geniusdevelop.playmyscreens.app.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.api.response.Images
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

class PlayerViewModel : ViewModel() {

    private lateinit var imagesSubscription: Subscription
    private lateinit var screenSubscription: Subscription
    private lateinit var userSubscription: Subscription
    private val _uiState = MutableStateFlow<PlayerUiState?>(null)
    val uiState: StateFlow<PlayerUiState?> = _uiState


    fun getContents (deviceCode: String) {
        _uiState.value = PlayerUiState.Loading
        viewModelScope.launch {
            try {
                val result = Repository.api.getImagesByScreenCode(deviceCode)
                if (result.success.toBoolean()) {
                    _uiState.value = result.data?.let {
                        PlayerUiState.Ready(it, result.screen_updated_at.toString())
                    }
                }
            } catch (e: Exception) {
                _uiState.value = PlayerUiState.Error(e.message.toString())
            }
        }
    }

    fun updatePlayer(deviceCode: String) {
        viewModelScope.launch {
            try {
                val result = Repository.api.getImagesByScreenCode(deviceCode)
                if (result.success != null && result.success.toBoolean()) {
                    _uiState.value = result.data?.let { PlayerUiState.Update(it, result.screen_updated_at.toString()) }
                }
            } catch (e: Exception) {
                _uiState.value = PlayerUiState.UpdateError(e.message.toString())
            }
        }
    }

    fun initSubscriptions(deviceCode: String) {
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
                    "player_screen_$deviceCode" -> {
                        when (data.message) {
                            "check_screen_update" -> {
                                Repository.wsManager.removeSubscription(imagesSubscription)
                                Repository.wsManager.removeSubscription(screenSubscription)
                                Repository.wsManager.removeSubscription(userSubscription)
                                _uiState.value = PlayerUiState.GotoHome
                            }
                        }
                    }
                    "player_images_$deviceCode" -> {
                        when (data.message) {
                            "check_images_update" -> {
                                _uiState.value = PlayerUiState.ReadyToUpdate
                            }
                        }
                    }
                    "user_$deviceCode" -> {
                        when (data.message) {
                            "logout" -> {
                                Repository.wsManager.removeSubscription(screenSubscription)
                                Repository.wsManager.removeSubscription(userSubscription)
                                Repository.wsManager.removeSubscription(imagesSubscription)
                                _uiState.value = PlayerUiState.GotoLogout
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
            imagesSubscription = Repository.wsManager.newSubscription("player_images_$deviceCode", subListener)
            screenSubscription = Repository.wsManager.newSubscription("player_screen_$deviceCode", subListener)
            userSubscription = Repository.wsManager.newSubscription("user_$deviceCode", subListener)
        } catch (e: DuplicateSubscriptionException) {
            e.printStackTrace()
            return
        }


        viewModelScope.launch {
            imagesSubscription.subscribe()
            screenSubscription.subscribe()
            userSubscription.subscribe()
        }
    }
}

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data object GotoHome : PlayerUiState
    data object GotoLogout : PlayerUiState
    data class Error(val msg: String = "") : PlayerUiState
    data class Ready(
        val images: Array<Images>,
        val updatedAt: String
    ) : PlayerUiState

    data object ReadyToUpdate : PlayerUiState
    data class Update(
        val images: Array<Images>,
        val updatedAt: String
    ) : PlayerUiState
    data class UpdateError(val msg: String = "") : PlayerUiState
}
