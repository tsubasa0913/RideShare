package com.websarva.wings.android.rideshare.shared.data.user

import com.websarva.wings.android.rideshare.shared.data.model.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore

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
}
