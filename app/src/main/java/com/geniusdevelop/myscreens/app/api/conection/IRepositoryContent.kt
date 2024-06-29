package com.geniusdevelop.myscreens.app.api.conection

import android.content.Context
import com.geniusdevelop.myscreens.app.api.models.MovieList
import kotlinx.coroutines.flow.Flow

interface IRepositoryContent {
    suspend fun closeApiSession()
    fun getTop10Movies(): Flow<MovieList>
}
