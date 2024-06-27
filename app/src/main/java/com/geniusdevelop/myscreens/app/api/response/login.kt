package com.geniusdevelop.myscreens.app.api.response

import com.geniusdevelop.myscreens.app.api.models.User
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse (
    val success : LoginSuccess? = null
)

@Serializable
data class LoginSuccess (
    val user : User? = null,
    val token : String? = null
)