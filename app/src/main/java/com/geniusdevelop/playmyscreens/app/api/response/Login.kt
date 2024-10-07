package com.geniusdevelop.playmyscreens.app.api.response

import com.geniusdevelop.playmyscreens.app.api.models.User
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse (
    val success : LoginSuccess? = null,
    val error : String? = null,
    val message : String? = null
)

@Serializable
data class GenerateLoginCodeResponse (
    val code : String? = null,
)

@Serializable
data class LogoutResponse (
    val success : String? = null,
    val message : String? = null
)


@Serializable
data class LoginSuccess (
    val user : User? = null,
    val token : String? = null
)