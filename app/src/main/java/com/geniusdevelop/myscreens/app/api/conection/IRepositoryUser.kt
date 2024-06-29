package com.geniusdevelop.myscreens.app.api.conection

import android.content.Context
import com.geniusdevelop.myscreens.app.api.models.MovieList
import com.geniusdevelop.myscreens.app.api.models.User
import com.geniusdevelop.myscreens.app.api.response.LoginResponse
import com.geniusdevelop.myscreens.app.api.response.LogoutResponse
import kotlinx.coroutines.flow.Flow

interface IRepositoryUser {
    suspend fun authenticate(email: String, password: String): LoginResponse
    suspend fun logout():LogoutResponse
    suspend fun getUserSessions(): User
    suspend fun getSessionToken(): String
}