package com.geniusdevelop.myscreens.app.viewmodels


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.myscreens.app.api.conection.Repository
import com.geniusdevelop.myscreens.app.api.models.ImageList
import com.geniusdevelop.myscreens.app.api.response.Images
import com.geniusdevelop.myscreens.app.util.Configuration
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlayerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<PlayerUiState?>(null)
    val uiState: StateFlow<PlayerUiState?> = _uiState


    fun getContents (deviceCode: String) {
        _uiState.value = PlayerUiState.Loading
        viewModelScope.launch {
            try {
                val result = Repository.api.getImagesByScreenCode(deviceCode)
                if (result.success.toBoolean()) {
                    _uiState.value = result.data?.let { PlayerUiState.Ready(it) }
                }
            } catch (e: Exception) {
                _uiState.value = PlayerUiState.Error(e.message.toString())
            }
        }
    }

    fun updatePlayer(deviceCode: String) {
        viewModelScope.launch {
            while (true) {
                delay(Configuration.Player.UpdateTimeRequest.toLong())
                try {
                    val result = Repository.api.getImagesByScreenCode(deviceCode)
                    if (result.success.toBoolean()) {
                        _uiState.value = result.data?.let { PlayerUiState.Update(it) }
                    }
                } catch (e: Exception) {
                    //_uiState.value = PlayerUiState.Error(e.message.toString())
                    Log.d("SERVER_RESPONSE", e.message.toString())
                }
            }
        }
    }
}

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Error(val msg: String = "") : PlayerUiState
    data class Ready(
        val images: Array<Images>
    ) : PlayerUiState
    data class Update(
        val images: Array<Images>
    ) : PlayerUiState
}
