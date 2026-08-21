package com.example

import androidx.annotation.Keep
import java.util.UUID

@Keep
data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val targetUserId: String = "",
    val sourceUserId: String = "",
    val sourceUserName: String = "",
    val sourceUserAvatar: String = "",
    val type: String = "", // "like" or "comment"
    val storyId: String = "",
    val content: String = "", 
    val timestamp: Long = System.currentTimeMillis(),
    val read: Boolean = false
) {
    @get:com.google.firebase.firestore.Exclude val formattedTime: String
        get() {
            val diff = System.currentTimeMillis() - timestamp
            val minute = 60 * 1000L
            val hour = 60 * minute
            val day = 24 * hour
            return when {
                diff < minute -> "now"
                diff < hour -> "${diff / minute}m"
                diff < day -> "${diff / hour}h"
                else -> "${diff / day}d"
            }
        }
}
