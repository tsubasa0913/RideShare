package com.websarva.wings.android.rideshare

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform