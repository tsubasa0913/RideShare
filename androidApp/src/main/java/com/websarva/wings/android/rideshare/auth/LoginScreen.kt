package com.websarva.wings.android.rideshare.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.websarva.wings.android.rideshare.R

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 1. Googleログインのオプションを設定
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // `google-services.json`から自動生成されるIDを要求
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    // 2. Googleログイン画面を起動し、結果を受け取るための仕組み
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val idToken = account.idToken
            // 3. 取得したIDトークンをViewModelに渡してFirebase認証を開始
            loginViewModel.signInWithGoogle(idToken)
        } catch (e: ApiException) {
            println("Google Sign-In failed with error code: ${e.statusCode}")
        }
    }

    // ログイン成功時に親コンポーネント(MainActivity)に通知
    if (uiState.loginSuccess) {
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    // --- UI部分 ---
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("大学乗り合いアプリへようこそ", style = MaterialTheme.typography.headlineSmall)

            // 4. このボタンが押されると、上記2のランチャーがGoogleログイン画面を起動する
            Button(onClick = { launcher.launch(googleSignInClient.signInIntent) }) {
                Text("Googleでサインイン")
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            uiState.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

