package com.geniusdevelop.myscreens.app.api.conection

import com.geniusdevelop.myscreens.app.api.models.User
import com.geniusdevelop.myscreens.app.api.response.LoginResponse
import com.geniusdevelop.myscreens.app.api.response.LogoutResponse

interface IRepositoryUser {
    suspend fun authenticate(email: String, password: String): LoginResponse
    suspend fun logout():LogoutResponse
}