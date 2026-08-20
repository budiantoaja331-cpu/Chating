package com.example

data class CallRecord(
    val id: String = "",
    val participants: List<String> = emptyList(), // [callerId, receiverId] for easy querying
    val callerId: String = "",
    val callerName: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val timestamp: Long = 0L,
    val durationSeconds: Int = 0,
    val isVideoCall: Boolean = false,
    val status: String = "" // "completed", "missed", "rejected"
)
