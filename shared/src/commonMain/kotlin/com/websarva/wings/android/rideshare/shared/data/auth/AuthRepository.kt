package com.websarva.wings.android.rideshare.shared.data.auth

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth

class AuthRepository {

    /**
     * Firebase Authenticationを使ってメールアドレスとパスワードでログインします。
     * @param loginRequest ユーザーが入力したメールアドレスとパスワード。
     * @return 成功した場合はユーザーID(UID)を、失敗した場合は例外を返します。
     */
    suspend fun login(loginRequest: LoginRequest): Result<String> {
        return try {
            // KMP用Firebase SDKの認証機能を呼び出し
            val userCredential = Firebase.auth.signInWithEmailAndPassword(
                email = loginRequest.email,
                password = loginRequest.pass
            )
            // ログインに成功した場合、ユーザーの一意なID (UID) を取得
            val uid = userCredential.user?.uid ?: throw IllegalStateException("User UID is null")
            Result.success(uid)
        } catch (e: Exception) {
            // ログインに失敗した場合（パスワード間違い、ユーザーが存在しないなど）
            println("Firebase Login Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Firebase Authenticationを使って新規ユーザーを登録します。
     * @param loginRequest ユーザーが入力したメールアドレスとパスワード。
     * @return 成功した場合はユーザーID(UID)を、失敗した場合は例外を返します。
     */
    suspend fun register(loginRequest: LoginRequest): Result<String> {
        return try {
            val userCredential = Firebase.auth.createUserWithEmailAndPassword(
                email = loginRequest.email,
                password = loginRequest.pass
            )
            // 登録に成功した場合、ユーザーの一意なID (UID) を取得
            val uid = userCredential.user?.uid ?: throw IllegalStateException("User UID is null")
            Result.success(uid)
        } catch (e: Exception) {
            // 登録に失敗した場合（メールアドレスの形式が不正、パスワードが弱いなど）
            println("Firebase Register Error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * GoogleのIDトークンを使ってFirebaseにログインします。
     * @param idToken AndroidのGoogle Sign-Inから取得したIDトークン
     * @return 成功した場合はユーザーID(UID)を、失敗した場合は例外を返します。
     */
    suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<String> {
        return try {
            // IDトークンとaccessTokenからFirebaseが認識できる認証情報を作成
            val credential = GoogleAuthProvider.credential(idToken, accessToken)
            // 作成した認証情報を使ってFirebaseにサインイン
            val userCredential = Firebase.auth.signInWithCredential(credential)
            val uid = userCredential.user?.uid ?: throw IllegalStateException("User UID is null")
            Result.success(uid)
        } catch (e: Exception) {
            println("Firebase Google Sign-In Error: ${e.message}")
            Result.failure(e)
        }
    }
}