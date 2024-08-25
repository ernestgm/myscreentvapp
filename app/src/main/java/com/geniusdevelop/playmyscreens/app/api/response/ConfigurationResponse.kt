package com.geniusdevelop.playmyscreens.app.api.response

import kotlinx.serialization.Serializable

@Serializable
data class ConfigResponse (
    val id : Int? = null,
    val slug: String = "",
    val acf : ConfigFields? = null,
)

@Serializable
data class ConfigFields (
    var screen_server_api_endpoint: String = "",
    var centrifugue_base_url: String = "",
    var centrifuge_pass_secret: String = ""
)