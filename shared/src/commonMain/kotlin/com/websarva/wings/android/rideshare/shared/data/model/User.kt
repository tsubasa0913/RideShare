package com.websarva.wings.android.rideshare.shared.data.model

import kotlinx.serialization.Serializable

// AuthResponseで使うために、Userクラスにも@Serializableが必須
@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val grade: String,
    val department: String,
    val profileImage: String? = null,
    val bio: String? = null,
    val createdAt: Long
)