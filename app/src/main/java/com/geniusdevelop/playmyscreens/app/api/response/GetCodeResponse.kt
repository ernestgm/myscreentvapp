package com.geniusdevelop.playmyscreens.app.api.response

import com.google.protobuf.BoolValueOrBuilder
import kotlinx.serialization.Serializable

@Serializable
data class GetCodeResponse (
    val success : String? = null,
    val data: Device? = null
)

@Serializable
data class Device (
    val id: String? = null,
    val code: String? = null,
    val device_id: String? = null,
    val portrait: Int? = null,
    val slide: Int? = null,
) {
    fun isPortrait(): Boolean {
        return portrait == 1
    }

    fun isSlide(): Boolean {
        return slide == 1
    }
}