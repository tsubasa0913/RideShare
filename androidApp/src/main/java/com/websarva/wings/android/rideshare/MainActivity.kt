package com.websarva.wings.android.rideshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.websarva.wings.android.rideshare.auth.LoginScreen
import com.websarva.wings.android.rideshare.chat.ChatScreen
import com.websarva.wings.android.rideshare.ride.RideListScreen
import com.websarva.wings.android.rideshare.ride.RidePostScreen
import com.websarva.wings.android.rideshare.ride.RequestManagementScreen
import com.websarva.wings.android.rideshare.ride.SentRequestScreen
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import com.websarva.wings.android.rideshare.ui.theme.RideShareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RideShareTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    // UIが記憶するユーザーIDを、ログイン状態の唯一の判断基準とする
    var currentUserId by rememberSaveable { mutableStateOf(UserSession.getCurrentUserId()) }
    var chatRequestId by rememberSaveable { mutableStateOf<String?>(null) }

    // アプリ復帰時などに、UIの状態とUserSessionの状態を同期させる
    LaunchedEffect(currentUserId) {
        if (currentUserId != null && UserSession.getCurrentUserId() == null) {
            UserSession.login(currentUserId!!)
        }
    }

    if (chatRequestId != null) {
        ChatScreen(rideRequestId = chatRequestId!!)
    } else if (currentUserId != null) {
        MainAppScreen(
            onOpenChat = { requestId -> chatRequestId = requestId },
            onLogout = {
                UserSession.logout()
                chatRequestId = null
                currentUserId = null // UIの状態を直接更新
            }
        )
    } else {
        LoginScreen(onLoginSuccess = {
            // ViewModelがUserSessionを更新した後、その最新の状態でUIを更新
            currentUserId = UserSession.getCurrentUserId()
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    onOpenChat: (requestId: String) -> Unit,
    onLogout: () -> Unit
) {
    var currentScreen by rememberSaveable { mutableStateOf("list") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("大学乗り合いアプリ") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "ログアウト")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == "list",
                    onClick = { currentScreen = "list" },
                    icon = { Icon(Icons.Default.List, contentDescription = "乗車一覧") },
                    label = { Text("探す") }
                )
                NavigationBarItem(
                    selected = currentScreen == "sent",
                    onClick = { currentScreen = "sent" },
                    icon = { Icon(Icons.Default.Send, contentDescription = "送信済み一覧") },
                    label = { Text("送信済み") }
                )
                NavigationBarItem(
                    selected = currentScreen == "requests",
                    onClick = { currentScreen = "requests" },
                    icon = { Icon(Icons.Default.MailOutline, contentDescription = "リクエスト管理") },
                    label = { Text("受信箱") }
                )
                NavigationBarItem(
                    selected = currentScreen == "post",
                    onClick = { currentScreen = "post" },
                    icon = { Icon(Icons.Default.Add, contentDescription = "乗車投稿") },
                    label = { Text("投稿") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "list" -> RideListScreen()
                "sent" -> SentRequestScreen()
                "requests" -> RequestManagementScreen(onOpenChat = onOpenChat)
                "post" -> RidePostScreen(onPostSuccess = { currentScreen = "list" })
            }
        }
    }
}

