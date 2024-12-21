package com.geniusdevelop.playmyscreens.app.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.api.response.Images
import com.geniusdevelop.playmyscreens.app.api.response.Marquee
import com.geniusdevelop.playmyscreens.app.api.response.QR
import com.geniusdevelop.playmyscreens.app.api.response.WSMessage
import com.geniusdevelop.playmyscreens.app.exceptions.ApiManagerUninitializedException
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets.UTF_8

class PlayerViewModel : ViewModel() {

    private lateinit var imagesSubscription: Subscription
    private lateinit var screenSubscription: Subscription
    private lateinit var marqueeSubscription: Subscription
    private lateinit var qrSubscription: Subscription
    private lateinit var userSubscription: Subscription

    private val _uiState = MutableStateFlow<PlayerUiState?>(null)
    val uiState: StateFlow<PlayerUiState?> = _uiState

    private val _marqueeState = MutableStateFlow<PlayerMarqueeState?>(null)
    val marqueeState: StateFlow<PlayerMarqueeState?> = _marqueeState

    private val _qrState = MutableStateFlow<PlayerQRState?>(null)
    val qrState: StateFlow<PlayerQRState?> = _qrState


    fun getContents (deviceCode: String) {
        _uiState.value = PlayerUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = Repository.api.getDataScreenByDeviceCode(deviceCode)
                if (result.success.toBoolean()) {
                    val isPortrait = result.device?.isPortrait() != null && result.device.isPortrait()
                    val isSlide = result.device?.isSlide() != null && result.device.isSlide()
                    val gDescriptionPosition = result.screen?.description_position.toString()
                    val gDescriptionSize = result.screen?.description_size.toString()

                    _uiState.value = PlayerUiState.Ready(
                        isPortrait,
                        isSlide,
                        gDescriptionPosition,
                        gDescriptionSize,
                        result.getMedia()
                    )
                }
            } catch (e: Exception) {
                showException(e)
            }
        }
    }

    fun getMarquee(deviceCode: String,  isUpdate: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                println("get Marquee")
                val result = Repository.api.getMarqueeByDeviceCode(deviceCode)
                if (result.success != null && result.success.toBoolean()) {
                    _marqueeState.value = result.marquee?.let { PlayerMarqueeState.ShowMarquee(it, isUpdate) }
                } else {
                    _marqueeState.value = PlayerMarqueeState.HideMarquee
                }
            } catch (e: Exception) {
                showException(e)
            }
        }
    }

    fun getQr(deviceCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = Repository.api.getQrByDeviceCode(deviceCode)
                if (result.success != null && result.success.toBoolean()) {
                    _qrState.value = result.qr?.let { PlayerQRState.ShowQR(it) }
                } else {
                    _qrState.value = PlayerQRState.HideQR
                }
            } catch (e: Exception) {
                showException(e)
            }
        }
    }

    fun updatePlayer(deviceCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = Repository.api.getDataScreenByDeviceCode(deviceCode)
                if (result.success != null && result.success.toBoolean()) {
                    _uiState.value = PlayerUiState.Update(result.getMedia())
                }
            } catch (e: Exception) {
                showException(e)
            }
        }
    }

    fun initSubscriptions(deviceCode: String) {
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
                                _uiState.value = PlayerUiState.RefreshPlayer
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
                    "player_marquee_$deviceCode" -> {
                        when (data.message) {
                            "check_marquee_update" -> {
                                _marqueeState.value = PlayerMarqueeState.UpdateMarquee
                                viewModelScope.launch {
                                    delay(3000)
                                    _uiState.value = PlayerUiState.ReadyToUpdate
                                }
                            }
                        }
                    }
                    "player_qr_$deviceCode" -> {
                        when (data.message) {
                            "check_qr_update" -> {
                                _qrState.value = PlayerQRState.UpdateQR
                            }
                        }
                    }
                    "user_$deviceCode" -> {
                        when (data.message) {
                            "logout" -> {
                                _uiState.value = PlayerUiState.GotoLogout(false)
                            }
                            "switch_account" -> {
                                _uiState.value = PlayerUiState.GotoLogout(true)
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
            println("init subscribe userSubscription")
            if (!::userSubscription.isInitialized) {
                userSubscription = Repository.wsManager.newSubscription("user_$deviceCode", subListener)
            }
        } catch (e: DuplicateSubscriptionException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            FirebaseCrashlytics.getInstance().log("Duplicate userSubscription")
            e.printStackTrace()
        }

        try {
            println("init subscribe screenSubscription")
            if (!::screenSubscription.isInitialized) {
                screenSubscription = Repository.wsManager.newSubscription("player_screen_$deviceCode", subListener)
            }
        } catch (e: DuplicateSubscriptionException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            FirebaseCrashlytics.getInstance().log("Duplicate screenSubscription")
            e.printStackTrace()
        }

        try {
            if (!::imagesSubscription.isInitialized) {
                println("init subscribe imagesSubscription")
                imagesSubscription = Repository.wsManager.newSubscription("player_images_$deviceCode", subListener)
            }
        } catch (e: DuplicateSubscriptionException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            FirebaseCrashlytics.getInstance().log("Duplicate imagesSubscription")
            e.printStackTrace()
        }

        try {
            if (!::marqueeSubscription.isInitialized) {
                println("init subscribe marqueeSubscription")
                marqueeSubscription = Repository.wsManager.newSubscription("player_marquee_$deviceCode", subListener)
            }
        } catch (e: DuplicateSubscriptionException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            FirebaseCrashlytics.getInstance().log("Duplicate marqueeSubscription")
            e.printStackTrace()
        }

        try {
            if (!::qrSubscription.isInitialized) {
                println("init subscribe qrSubscription")
                qrSubscription = Repository.wsManager.newSubscription("player_qr_$deviceCode", subListener)
            }
        } catch (e: DuplicateSubscriptionException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            FirebaseCrashlytics.getInstance().log("Duplicate qrSubscription")
            e.printStackTrace()
        }

        viewModelScope.launch {
            if (::imagesSubscription.isInitialized) {
                imagesSubscription.subscribe()
            }
            if (::screenSubscription.isInitialized) {
                screenSubscription.subscribe()
            }
            if (::marqueeSubscription.isInitialized) {
                marqueeSubscription.subscribe()
            }
            if (::qrSubscription.isInitialized) {
                qrSubscription.subscribe()
            }
            if (::userSubscription.isInitialized) {
                userSubscription.subscribe()
            }
        }
    }

    fun removeAllSubscriptions() {
        if (::imagesSubscription.isInitialized) {
            Repository.wsManager.removeSubscription(imagesSubscription)
        }
        if (::screenSubscription.isInitialized) {
            Repository.wsManager.removeSubscription(screenSubscription)
        }
        if (::marqueeSubscription.isInitialized) {
            Repository.wsManager.removeSubscription(marqueeSubscription)
        }
        if (::qrSubscription.isInitialized) {
            Repository.wsManager.removeSubscription(qrSubscription)
        }
        if (::userSubscription.isInitialized) {
            Repository.wsManager.removeSubscription(userSubscription)
        }
    }

    private fun showException(e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
        when(e) {
            is UnresolvedAddressException -> {
                _uiState.value = PlayerUiState.Error("Network Error: Check your internet connection.")
            }
            is NoTransformationFoundException -> {
                _uiState.value = PlayerUiState.Error("Network Error: Load Data Failed.")
            }
            is ApiManagerUninitializedException -> {
                FirebaseCrashlytics.getInstance().log("ApiManager not initialized")
                _uiState.value = PlayerUiState.ReloadApp
            }
            is HttpRequestTimeoutException -> {
                FirebaseCrashlytics.getInstance().log("HttpRequestTimeoutException")
                _uiState.value = PlayerUiState.ReloadApp
            }
            is ConnectTimeoutException -> {
                FirebaseCrashlytics.getInstance().log("ConnectTimeoutException")
                _uiState.value = PlayerUiState.ReloadApp
            }
            else -> {
                _uiState.value = PlayerUiState.Error("Error: " + e.message.toString())
            }
        }
    }
}

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data object RefreshPlayer : PlayerUiState
    data object ReloadApp : PlayerUiState
    data class GotoLogout(
        val switchAccount: Boolean
    ) : PlayerUiState
    data class Error(val msg: String = "") : PlayerUiState
    data class Ready(
        val isPortrait: Boolean,
        val isSlide: Boolean,
        val globlaDescriptionPosition: String,
        val globlaDescriptionSize: String,
        val images: Array<Images>
    ) : PlayerUiState

    data object ReadyToUpdate : PlayerUiState
    data class Update(
        val images: Array<Images>
    ) : PlayerUiState
    data class UpdateError(val msg: String = "") : PlayerUiState
}

sealed interface PlayerQRState {
    data class ShowQR(val qr: QR) : PlayerQRState
    data object HideQR : PlayerQRState
    data object UpdateQR : PlayerQRState
}

sealed interface PlayerMarqueeState {
    data object UpdateMarquee : PlayerMarqueeState
    data object HideMarquee : PlayerMarqueeState
    data class ShowMarquee(
        val marquee: Marquee,
        val isUpdate: Boolean
    ) : PlayerMarqueeState
}
