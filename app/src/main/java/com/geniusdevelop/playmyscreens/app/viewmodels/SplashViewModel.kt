package com.geniusdevelop.playmyscreens.app.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.playmyscreens.BuildConfig
import com.geniusdevelop.playmyscreens.app.api.conection.Repository
import com.geniusdevelop.playmyscreens.app.api.response.ConfigFields
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class SplashViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow<HomeScreenUiState?>(null)
    val uiState: StateFlow<HomeScreenUiState?> = _uiState


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
                _uiState.value = SplashScreenUiState.Error(e.message.toString())
            }
        }
    }
}

sealed interface SplashScreenUiState {
    data object Loading : HomeScreenUiState
    data class Error(val msg: String = "") : HomeScreenUiState
    data class Ready(
        val config: ConfigFields
    ) : HomeScreenUiState
}


