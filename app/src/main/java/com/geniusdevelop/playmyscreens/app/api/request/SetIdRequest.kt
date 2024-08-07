package com.geniusdevelop.playmyscreens.app.api.request

import kotlinx.serialization.Serializable

@Serializable
data class SetIdRequest (
    val name: String,
    val code: String,
    val device_id: String,
    val user_id: String
)