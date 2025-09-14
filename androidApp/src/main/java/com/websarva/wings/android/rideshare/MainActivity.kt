package com.websarva.wings.android.rideshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.websarva.wings.android.rideshare.auth.LoginScreen
import com.websarva.wings.android.rideshare.ride.RequestManagementScreen
import com.websarva.wings.android.rideshare.ride.RideListScreen
import com.websarva.wings.android.rideshare.ride.RidePostScreen
import com.websarva.wings.android.rideshare.ui.theme.RideShareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 1. アプリのテーマを適用する
            RideShareTheme {
                // 2. 表示したいUI画面を呼び出す

                // RidePostScreen()
                // LoginScreen()
                // RideListScreen()
                RequestManagementScreen()
            }
        }
    }
}

