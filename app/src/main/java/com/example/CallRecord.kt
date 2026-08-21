package com.example

import androidx.annotation.Keep

@Keep
data class CallRecord(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val callerId: String = "",
    val callerName: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val timestamp: Long = 0L,
    val durationSeconds: Int = 0,
    val isVideoCall: Boolean = false,
    val status: String = "",
    val channelId: String = ""
)
