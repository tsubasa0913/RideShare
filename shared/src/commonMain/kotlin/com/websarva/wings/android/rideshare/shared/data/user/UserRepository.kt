package com.websarva.wings.android.rideshare.shared.data.user

import com.websarva.wings.android.rideshare.shared.data.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where

class UserRepository {

    private val usersCollection = Firebase.firestore.collection("users")

    /**
     * 指定されたUIDのユーザープロフィールを取得する
     */
    suspend fun getUserProfile(uid: String): Result<User?> {
        return try {
            val document = usersCollection.document(uid).get()
            Result.success(document.data())
        } catch (e: Exception) {
            println("Get User Profile Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * ユーザープロフィールを保存または更新する
     */
    suspend fun saveUserProfile(user: User): Result<Unit> {
        return try {
            // ユーザーのUIDをドキュメントIDとしてプロフィールを保存
            usersCollection.document(user.uid).set(user)
            Result.success(Unit)
        } catch (e: Exception) {
            println("Save User Profile Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Googleログイン時に、プロフィールが未作成であれば初期プロフィールを作成する
     */
    suspend fun checkAndCreateUserProfile(): Result<Unit> {
        val firebaseUser = Firebase.auth.currentUser ?: return Result.failure(Exception("Not logged in"))
        val profileResult = getUserProfile(firebaseUser.uid)

        return if (profileResult.isSuccess && profileResult.getOrNull() == null) {
            // プロフィールが存在しない場合のみ、初期データを作成
            val newUser = User(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName ?: ""
            )
            saveUserProfile(newUser)
        } else {
            // 既存のプロフィールがあるか、エラーが発生した場合は何もしない
            Result.success(Unit)
        }
    }

    /**
     * ▼▼▼ 新しく追加した関数 ▼▼▼
     * 複数のUIDからユーザープロフィールのマップを取得する
     */
    suspend fun getUsers(uids: List<String>): Result<Map<String, User>> {
        if (uids.isEmpty()) return Result.success(emptyMap())
        return try {
            // 'in'クエリを使って、一度の通信で複数のユーザー情報を取得
            val snapshot = usersCollection.where("uid", "in", uids).get()
            val usersMap = snapshot.documents.mapNotNull { doc ->
                val user = doc.data<User>()
                user.uid to user
            }.toMap()
            Result.success(usersMap)
        } catch (e: Exception) {
            println("Get Users Error: ${e.message}")
            Result.failure(e)
        }
    }
}

