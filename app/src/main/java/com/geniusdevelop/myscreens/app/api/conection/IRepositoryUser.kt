package com.geniusdevelop.myscreens.app.api.conection

import android.provider.ContactsContract.CommonDataKinds.Email
import com.geniusdevelop.myscreens.app.api.models.User
import com.geniusdevelop.myscreens.app.api.response.LoginResponse

interface IRepositoryUser {
    suspend fun authenticate(email: String, password: String): LoginResponse
    suspend fun getUserSessions(): User
    suspend fun getSessionToken(): String
}