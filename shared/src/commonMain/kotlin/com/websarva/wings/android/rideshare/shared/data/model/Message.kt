package com.websarva.wings.android.rideshare.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String = "",
    val text: String = "",
    val senderId: String = "",
    val timestamp: Long = 0L
)
