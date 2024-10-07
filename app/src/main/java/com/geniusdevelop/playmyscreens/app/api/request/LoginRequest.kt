package com.geniusdevelop.playmyscreens.app.api.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest (
    val email: String,
    val password: String
)

@Serializable
data class LoginByCodeRequest (
    val code: String,
    val deviceId: String
)

@Serializable
data class GenerateCodeRequest (
    val deviceId: String
)