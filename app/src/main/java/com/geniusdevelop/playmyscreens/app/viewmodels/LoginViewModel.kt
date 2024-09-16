package com.geniusdevelop.playmyscreens.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.api.response.LoginSuccess
import com.geniusdevelop.playmyscreens.app.util.AppLog
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
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
                if ( e is UnresolvedAddressException ) {
                    AppLog.manager.logToFile("", "Login: Network Error:" + e.message.toString())
                    _uiState.value = LoginUiState.Error("Network Error: Check your internet connection.")
                } else {
                    AppLog.manager.logToFile("", "Login: Error: " + e.message.toString())
                    _uiState.value = LoginUiState.Error("Error: " + e.message.toString())
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                Repository.user.logout()
                _uiState.value = LoginUiState.Ready()
            } catch (e: Exception) {
                if ( e is UnresolvedAddressException ) {
                    AppLog.manager.logToFile("", "Login: Network Error:" + e.message.toString())
                    _uiState.value = LoginUiState.Error("Network Error: Check your internet connection.")
                } else {
                    AppLog.manager.logToFile("", "Login: Error:" + e.message.toString())
                    _uiState.value = LoginUiState.Error("Error: " + e.message.toString())
                }
            }
        }
    }
}

sealed interface LoginUiState {
    data object Loading : LoginUiState
    data class Error(val msg: String = "") : LoginUiState
    data class Ready(val success: LoginSuccess? = null) : LoginUiState
}