package com.geniusdevelop.myscreens.app.viewmodels


import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.myscreens.app.api.conection.Repository
import com.geniusdevelop.myscreens.app.session.SessionManager
import com.geniusdevelop.myscreens.app.session.dataStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeScreeViewModel() : ViewModel() {

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
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data class Error(val msg: String = "") : HomeScreenUiState
    data class Ready(
        val deviceID: String
    ) : HomeScreenUiState
}
