package com.geniusdevelop.myscreens.app.api.response

import kotlinx.serialization.Serializable

@Serializable
data class GetCodeResponse (
    val success : String? = null,
    val data: Device? = null
)

@Serializable
data class Device (
    val code: String? = null,
    val device_id: String? = null
)