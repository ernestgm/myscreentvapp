package com.geniusdevelop.playmyscreens.app.api.models

import kotlinx.serialization.Serializable


@Serializable
data class User (
    val userId: String? = null,
    val id: Int? = null,
    val name : String? = null,
    val lastname : String? = null,
    val email: String? = null
)