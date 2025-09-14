package com.websarva.wings.android.rideshare.shared.data.auth

import kotlinx.serialization.Serializable

// ViewModelから渡されるログイン情報。
// このファイルに定義を統一します。
@Serializable
data class LoginRequest(
    val email: String,
    val pass: String // パスワード
)