package com.geniusdevelop.myscreens.app.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.myscreens.app.api.models.User
import com.geniusdevelop.myscreens.ui.theme.navigation.NavGraph.Home
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState?>(null)
    val uiState: StateFlow<LoginUiState?> = _uiState

    fun authenticate(email: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                val response = com.geniusdevelop.myscreens.app.api.login(email, password)
                if (response.success != null) {
                    _uiState.value = LoginUiState.Ready(response.success.user)
                } else {
                    Log.d("LoginResponse", "Unauthorize")
                }
            } catch (e: Exception) {
                val a = e.message
                Log.d("LoginRequest", a.toString())
            }
        }
    }
}

sealed interface LoginUiState {
    data object Loading : LoginUiState
    data object Error : LoginUiState
    data class Ready(
        val user: User?,
    ) : LoginUiState
}