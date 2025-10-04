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
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // CoroutineScopeを取得

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // ▼▼▼ 参照先を自動生成されるIDに戻しました ▼▼▼
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val idToken = account.idToken
            // ▼▼▼ ViewModelの定義に合わせて引数を1つに修正しました ▼▼▼
            loginViewModel.signInWithGoogle(idToken)
        } catch (e: ApiException) {
            println("Google Sign-In failed with error code: ${e.statusCode}")
        }
    }

    if (uiState.loginSuccess) {
        LaunchedEffect(Unit) {
            onLoginSuccess()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("大学乗り合いアプリへようこそ", style = MaterialTheme.typography.headlineSmall)

            Button(onClick = {
                // ログイン画面を起動する前に、まずGoogleクライアントからサインアウトする
                scope.launch {
                    googleSignInClient.signOut().addOnCompleteListener {
                        // サインアウトが完了してから、ログインインテントを起動
                        launcher.launch(googleSignInClient.signInIntent)
                    }
                }
            }) {
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

