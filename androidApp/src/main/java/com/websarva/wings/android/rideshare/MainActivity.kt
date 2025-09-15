package com.websarva.wings.android.rideshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.websarva.wings.android.rideshare.shared.data.session.UserSession
import com.websarva.wings.android.rideshare.ui.theme.RideShareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RideShareTheme {
                // アプリのメインのナビゲーションを管理
                // AppNavigator()
                ChatScreen()
            }
        }
    }
}

/*
@Composable
fun AppNavigator() {
    // ログイン状態を記憶する変数。rememberSaveableでアプリが閉じられても状態を保持
    var isLoggedIn by rememberSaveable { mutableStateOf(UserSession.getCurrentUserId() != null) }

    if (isLoggedIn) {
        // ログイン済みの場合はメイン画面を表示
        MainAppScreen()
    } else {
        // 未ログインの場合はログイン画面を表示
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    }
}

@Composable
fun MainAppScreen() {
    // 現在選択されている画面を記憶する変数
    var currentScreen by rememberSaveable { mutableStateOf("list") }

    Scaffold(
        bottomBar = {
            // 画面下部のナビゲーションバー
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == "list",
                    onClick = { currentScreen = "list" },
                    icon = { Icon(Icons.Default.List, contentDescription = "乗車一覧") },
                    label = { Text("探す") }
                )
                NavigationBarItem(
                    selected = currentScreen == "requests",
                    onClick = { currentScreen = "requests" },
                    icon = { Icon(Icons.Default.MailOutline, contentDescription = "リクエスト管理") },
                    label = { Text("リクエスト") }
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
        // 各画面をBoxで囲み、Scaffoldからのpaddingを適用する
        Box(modifier = Modifier.padding(innerPadding)) {
            // 選択されている画面に応じて中身を切り替える
            when (currentScreen) {
                "list" -> RideListScreen()
                "requests" -> RequestManagementScreen()
                "post" -> RidePostScreen(onPostSuccess = { currentScreen = "list" })
            }
        }
    }
}
*/
