package com.geniusdevelop.playmyscreens.app.api.request

import kotlinx.serialization.Serializable

@Serializable
data class SetIdRequest (
    val name: String,
    val code: String,
    val device_id: String,
    val user_id: String,
    val app_version: String,
    val android_version: String,
    val overlay_permission: String
)

@Serializable
data class SetUpdateDeviceRequest (
    val app_version: String,
    val android_version: String,
    val overlay_permission: String
)