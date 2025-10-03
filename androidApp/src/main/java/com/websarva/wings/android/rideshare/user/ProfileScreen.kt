package com.websarva.wings.android.rideshare.user

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 保存成功メッセージをスナックバーで表示
    LaunchedEffect(uiState.saveSuccessMessage) {
        // ▼▼▼ この部分を修正しました ▼▼▼
        val message = uiState.saveSuccessMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.errorMessage != null -> Text(uiState.errorMessage!!)
                uiState.user != null -> {
                    ProfileForm(
                        user = uiState.user!!,
                        onSave = { name, grade, department ->
                            viewModel.saveUserProfile(name, grade, department)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileForm(
    user: com.websarva.wings.android.rideshare.shared.data.model.User,
    onSave: (name: String, grade: String, department: String) -> Unit
) {
    // 編集可能なテキストフィールド用の状態
    var name by remember(user.name) { mutableStateOf(user.name) }
    var grade by remember(user.grade) { mutableStateOf(user.grade) }
    var department by remember(user.department) { mutableStateOf(user.department) }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("プロフィール編集", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = user.email,
            onValueChange = {},
            label = { Text("メールアドレス") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("氏名") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = grade,
            onValueChange = { grade = it },
            label = { Text("学年") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = department,
            onValueChange = { department = it },
            label = { Text("学部・学科") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { onSave(name, grade, department) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("保存する")
        }
    }
}

