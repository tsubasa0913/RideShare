package com.websarva.wings.android.rideshare.shared.data.session

/**
 * ログインしているユーザーの情報を管理するためのシングルトンオブジェクト
 */
object UserSession {
    private var currentUserId: String? = null

    /**
     * ログイン時にユーザーIDを保存する
     */
    fun login(uid: String) {
        currentUserId = uid
    }

    /**
     * 現在ログインしているユーザーのIDを取得する
     */
    fun getCurrentUserId(): String? {
        return currentUserId
    }

    /**
     * ログアウト時にユーザーIDをクリアする
     */
    fun logout() {
        currentUserId = null
    }
}
