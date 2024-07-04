package com.geniusdevelop.myscreens.app.api.request

import kotlinx.serialization.Serializable

@Serializable
data class SetIdRequest (
    val code: String,
    val device_id: String,
    val user_id: String
)