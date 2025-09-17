package com.websarva.wings.android.rideshare.shared.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RideOffer(
    val id: String,
    val driverId: String,
    val departure: String,
    val destination: String,
    val departureTime: Long,
    val availableSeats: Int,
    val description: String? = null,
    val status: RideStatus,
    val createdAt: Long
)

@Serializable
data class RideRequest(
    val id: String,
    val rideOfferId: String,
    val passengerId: String,
    val driverId: String,
    val message: String? = null,
    val status: RequestStatus,
    val createdAt: Long
)

enum class RideStatus {
    AVAILABLE,
    FULL,
    COMPLETED,
    CANCELLED
}

enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}

