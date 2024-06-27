package com.geniusdevelop.myscreens.app.api.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest (
    val email: String,
    val password: String
)