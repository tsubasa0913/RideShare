package com.websarva.wings.android.rideshare.shared.data.ride

import com.websarva.wings.android.rideshare.shared.data.model.RideOffer
import com.websarva.wings.android.rideshare.shared.data.model.RideRequest
import com.websarva.wings.android.rideshare.shared.data.model.RequestStatus
import com.websarva.wings.android.rideshare.shared.data.model.RideStatus
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

// 乗車提供を投稿する際にサーバーに送信するデータ
@Serializable
data class PostRideRequest(
    val driverId: String,
    val departure: String,
    val destination: String,
    val departureTime: Long,
    val availableSeats: Int,
    val description: String?
)

class RideRepository {
    suspend fun postRide(request: PostRideRequest): Result<Unit> {
        return try {
            val collection = Firebase.firestore.collection("rides")

            val newRide = RideOffer(
                id = "",
                driverId = request.driverId,
                departure = request.departure,
                destination = request.destination,
                departureTime = request.departureTime,
                availableSeats = request.availableSeats,
                description = request.description,
                status = RideStatus.AVAILABLE,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )

            collection.add(newRide)

            Result.success(Unit)
        } catch (e: Exception) {
            println("Firestore Post Ride Error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getAllRides(): Result<List<RideOffer>> {
        return try {
            val snapshot = Firebase.firestore.collection("rides").get()
            val rides = snapshot.documents.map { document ->
                val ride = document.data<RideOffer>()
                ride.copy(id = document.id)
            }
            Result.success(rides)
        } catch (e: Exception) {
            println("Firestore Get All Rides Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 新しい乗車リクエストをCloud Firestoreに保存する (driverIdも保存するよう変更)
     */
    suspend fun sendRideRequest(rideOfferId: String, passengerId: String, driverId: String): Result<Unit> {
        return try {
            val collection = Firebase.firestore.collection("requests")
            val newRequest = RideRequest(
                id = "",
                rideOfferId = rideOfferId,
                passengerId = passengerId,
                driverId = driverId, // driverIdを保存
                message = "よろしくお願いします！",
                status = RequestStatus.PENDING,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
            collection.add(newRequest)
            Result.success(Unit)
        } catch (e: Exception) {
            println("Firestore Send Ride Request Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 【修正版】指定されたドライバーIDのリクエストを取得する
     */
    suspend fun getReceivedRequests(driverId: String): Result<List<RideRequest>> {
        return try {
            // where句が機能しない問題の回避策として、全件取得後に手動でフィルタリングします。
            val allRequestsSnapshot = Firebase.firestore.collection("requests").get()

            val matchingRequests = allRequestsSnapshot.documents
                .map { document ->
                    // まずドキュメントをRideRequestオブジェクトに変換し、IDをセットします
                    val request = document.data<RideRequest>()
                    request.copy(id = document.id)
                }
                .filter { request ->
                    // その後、driverIdが一致するものだけを絞り込みます
                    request.driverId == driverId
                }

            Result.success(matchingRequests)

        } catch (e: Exception) {
            println("Firestore Get Received Requests Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 指定されたリクエストIDのステータスを更新する
     */
    suspend fun updateRequestStatus(requestId: String, newStatus: RequestStatus): Result<Unit> {
        return try {
            Firebase.firestore.collection("requests").document(requestId)
                .update("status" to newStatus)
            Result.success(Unit)
        } catch (e: Exception) {
            println("Firestore Update Request Status Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * ▼▼▼ 新しく追加した関数 ▼▼▼
     * 指定された乗客IDの送信済みリクエストをすべて取得する
     */
    suspend fun getSentRequests(passengerId: String): Result<List<RideRequest>> {
        return try {
            // where句が機能しない問題に備え、こちらも手動フィルタリングで実装します
            val allRequestsSnapshot = Firebase.firestore.collection("requests").get()

            val matchingRequests = allRequestsSnapshot.documents
                .map { document ->
                    val request = document.data<RideRequest>()
                    request.copy(id = document.id)
                }
                .filter { request ->
                    request.passengerId == passengerId
                }

            Result.success(matchingRequests)

        } catch (e: Exception) {
            println("Firestore Get Sent Requests Error: ${e.message}")
            Result.failure(e)
        }
    }
}

