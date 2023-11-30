package com.example.weproject

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

data class ChatLayout(
    val senderId: String,
    val contents: String,
    val timestamp: Long = System.currentTimeMillis()
)
