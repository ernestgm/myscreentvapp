package com.geniusdevelop.myscreens.app.pages.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.geniusdevelop.myscreens.app.api.models.MovieList
import com.geniusdevelop.myscreens.app.util.Padding
import com.google.jetstream.presentation.screens.home.Top10MoviesList

val ParentPadding = PaddingValues(vertical = 20.dp, horizontal = 50.dp)

@Composable
fun rememberChildPadding(direction: LayoutDirection = LocalLayoutDirection.current): Padding {
    return remember {
        Padding(
            start = ParentPadding.calculateStartPadding(direction) + 8.dp,
            top = ParentPadding.calculateTopPadding(),
            end = ParentPadding.calculateEndPadding(direction) + 8.dp,
            bottom = ParentPadding.calculateBottomPadding()
        )
    }
}

@Composable
fun HomePage(
    //onMovieClick: (movie: Movie) -> Unit,
    //goToVideoPlayer: (movie: Movie) -> Unit,
    //onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean = true,
    homeScreeViewModel: HomeScreeViewModel = viewModel(),
) {


    val uiState by homeScreeViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is HomeScreenUiState.Ready -> {
            Catalog(
                top10Movies = s.top10MovieList,
                modifier = Modifier
                    .fillMaxSize()
                    .focusRequester(FocusRequester()),
            )
        }

        is HomeScreenUiState.Loading -> {}
        is HomeScreenUiState.Error -> {}
    }


}

@Composable
private fun Catalog(
    top10Movies: MovieList,
    //onMovieClick: (movie: Movie) -> Unit,
    //onScroll: (isTopBarVisible: Boolean) -> Unit,
    //goToVideoPlayer: (movie: Movie) -> Unit,
    modifier: Modifier = Modifier,
    isTopBarVisible: Boolean = true,
) {
    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()

    var immersiveListHasFocus by remember { mutableStateOf(false) }

    LaunchedEffect(isTopBarVisible) {
        focusManager.moveFocus(FocusDirection.Down)
    }

    LazyColumn(
        state = lazyListState,
        contentPadding = PaddingValues(bottom = 108.dp),
        // Setting overscan margin to bottom to ensure the last row's visibility
        modifier = modifier,
    ) {

        item(contentType = "Top10MoviesList") {
            Top10MoviesList(
                movieList = top10Movies,
                //onMovieClick = onMovieClick,
                modifier = Modifier.onFocusChanged {
                    immersiveListHasFocus = it.hasFocus
                },
                onMovieClick = {}
            )
        }
    }
}


