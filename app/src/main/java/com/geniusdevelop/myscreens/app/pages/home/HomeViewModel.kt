package com.geniusdevelop.myscreens.app.pages.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.geniusdevelop.myscreens.app.api.conection.Repository
import com.geniusdevelop.myscreens.app.api.models.MovieList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeScreeViewModel : ViewModel() {

    val uiState: StateFlow<HomeScreenUiState> = combine(
        Repository.api.getTop10Movies()
    ) { top10MovieList ->
        HomeScreenUiState.Ready(
            top10MovieList.first()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenUiState.Loading
    )
}

sealed interface HomeScreenUiState {
    data object Loading : HomeScreenUiState
    data object Error : HomeScreenUiState
    data class Ready(
        val top10MovieList: MovieList
    ) : HomeScreenUiState
}
