package com.websarva.wings.android.rideshare.shared.data.ride

import com.websarva.wings.android.rideshare.shared.data.model.RideOffer
import com.websarva.wings.android.rideshare.shared.data.model.RideRequest
import com.websarva.wings.android.rideshare.shared.data.model.RideStatus
import com.websarva.wings.android.rideshare.shared.data.model.RequestStatus
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.datetime.Clock //追加
import kotlinx.serialization.Serializable

// 乗車提供を投稿する際にサーバーに送信するデータ
// このデータクラスはViewModelからデータを受け取るために引き続き使用します
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
                // ▼▼▼ 2. ここを修正 ▼▼▼
                createdAt = Clock.System.now().toEpochMilliseconds() // 現在時刻をマルチプラットフォームで取得
            )

            collection.add(newRide)

            Result.success(Unit)
        } catch (e: Exception) {
            println("Firestore Post Ride Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * すべての乗車提供情報をCloud Firestoreから取得する
     */
    suspend fun getAllRides(): Result<List<RideOffer>> {
        return try {
            val snapshot = Firebase.firestore.collection("rides").get()
            val rides = snapshot.documents.map { document ->
                // FirestoreのドキュメントをRideOfferオブジェクトに変換
                val ride = document.data<RideOffer>()
                // Firestoreが自動生成したIDをオブジェクトにセット
                ride.copy(id = document.id)
            }
            Result.success(rides)
        } catch (e: Exception) {
            println("Firestore Get All Rides Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * 新しい乗車リクエストをCloud Firestoreに保存する
     */
    suspend fun sendRideRequest(rideOfferId: String, passengerId: String): Result<Unit> {
        return try {
            val collection = Firebase.firestore.collection("requests")

            val newRequest = RideRequest(
                id = "", // Firestoreが自動採番
                rideOfferId = rideOfferId,
                passengerId = passengerId,
                message = "よろしくお願いします！", // (任意)メッセージ
                status = RequestStatus.PENDING, // 最初は「承認待ち」状態
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
     * 指定されたドライバーIDの乗車提供に対するリクエストをすべて取得する
     */
    suspend fun getReceivedRequests(driverId: String): Result<List<RideRequest>> {
        return try {
            // 1. ドライバーが投稿した乗車情報をすべて取得
            val ridesSnapshot = Firebase.firestore.collection("rides")
                .where("driverId", "==", driverId).get()
            val rideIds = ridesSnapshot.documents.map { it.id }

            if (rideIds.isEmpty()) {
                return Result.success(emptyList()) // 乗車情報がなければリクエストもない
            }

            // 2. 取得した乗車情報IDに紐づくリクエストをすべて取得
            val requestsSnapshot = Firebase.firestore.collection("requests")
                .where("rideOfferId", "in", rideIds).get()

            val requests = requestsSnapshot.documents.map { document ->
                val request = document.data<RideRequest>()
                request.copy(id = document.id)
            }
            Result.success(requests)
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
}