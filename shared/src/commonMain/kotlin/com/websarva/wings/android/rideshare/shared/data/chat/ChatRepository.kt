package com.websarva.wings.android.rideshare.shared.data.chat

import com.websarva.wings.android.rideshare.shared.data.model.Message
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.Direction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository {

    /**
     * 新しいメッセージを送信する
     * @param rideRequestId チャットルームのID（乗車リクエストのIDを流用）
     * @param message 送信するメッセージオブジェクト
     */
    suspend fun sendMessage(rideRequestId: String, message: Message): Result<Unit> {
        return try {
            // "requests"コレクション -> 特定のIDのドキュメント -> "messages"サブコレクション にメッセージを追加
            Firebase.firestore
                .collection("requests").document(rideRequestId)
                .collection("messages").add(message)
            Result.success(Unit)
        } catch (e: Exception) {
            println("Firestore Send Message Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * チャットのメッセージをリアルタイムで購読する
     * @param rideRequestId チャットルームのID
     * @return メッセージリストのFlow
     */
    fun getMessages(rideRequestId: String): Flow<List<Message>> {
        return Firebase.firestore
            .collection("requests").document(rideRequestId)
            .collection("messages")
            .orderBy("timestamp", Direction.ASCENDING) // 時刻順に並び替え
            .snapshots // リアルタイムでデータの変更を監視
            .map { snapshot ->
                snapshot.documents.map { document ->
                    val message = document.data<Message>()
                    message.copy(id = document.id)
                }
            }
    }
}