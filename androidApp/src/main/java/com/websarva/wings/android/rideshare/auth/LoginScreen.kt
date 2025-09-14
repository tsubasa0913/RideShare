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
    loginViewModel: LoginViewModel = viewModel()
) {
    // ViewModelのUI状態を監視
    val uiState by loginViewModel.uiState.collectAsState()

    // ユーザーが入力するメールアドレスとパスワードの状態
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // ログイン成功時に一度だけ表示するダイアログ
    if (uiState.loginSuccess) {
        AlertDialog(
            onDismissRequest = { /* 何もしない */ },
            title = { Text("成功") },
            text = { Text("ログインに成功しました！") },
            confirmButton = {
                TextButton(onClick = { /* ホーム画面へ遷移など */ }) {
                    Text("OK")
                }
            }
        )
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