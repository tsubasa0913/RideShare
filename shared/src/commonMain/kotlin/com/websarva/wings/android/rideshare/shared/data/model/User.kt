package com.websarva.wings.android.rideshare.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val grade: String = "",
    val department: String = "",
    val profileImageUrl: String? = null,
    val bio: String? = null
)

