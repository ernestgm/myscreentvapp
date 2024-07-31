package com.geniusdevelop.playmyscreens.app.api.response

import kotlinx.serialization.Serializable

@Serializable
data class SetIdResponse (
    val success : String? = null,
    val error : String? = null,
    val message : String? = null,
    val data: ErrorValidator? = null
)

data class SetDeviceIDResponse (
    val success : Boolean = false,
    val code: String = "",
    val error : Boolean = false,
    val message : String = "",
)

@Serializable
data class ErrorValidator (
    val code: Array<String> = emptyArray(),
    val device_id: Array<String> = emptyArray(),
)