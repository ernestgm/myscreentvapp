package com.geniusdevelop.playmyscreens.app.api.conection

import com.geniusdevelop.playmyscreens.app.api.response.LoginResponse
import com.geniusdevelop.playmyscreens.app.api.response.LogoutResponse

interface IRepositoryUser {
    suspend fun authenticate(email: String, password: String): LoginResponse
    suspend fun logout():LogoutResponse
}