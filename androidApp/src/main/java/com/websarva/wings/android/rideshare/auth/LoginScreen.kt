package com.websarva.wings.android.rideshare.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    // ▼▼▼ この引数を追加 ▼▼▼
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    // ViewModelのUI状態を監視
    val uiState by loginViewModel.uiState.collectAsState()

    // ユーザーが入力するメールアドレスとパスワードの状態
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ▼▼▼ ログイン成功時の処理を修正 ▼▼▼
    if (uiState.loginSuccess) {
        // LaunchedEffectを使って、UIの再描画とは独立して一度だけ実行する
        LaunchedEffect(Unit) {
            // 親(MainActivity)にログイン成功を通知
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("ログイン", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("大学メールアドレス") }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("パスワード") }
            )

            // エラーメッセージがあれば表示
            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { loginViewModel.login(email, password) },
                // ローディング中はボタンを押せないようにする
                enabled = !uiState.isLoading
            ) {
                Text("ログイン")
            }

            // ローディングインジケータ
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            }
        }
    }
}

