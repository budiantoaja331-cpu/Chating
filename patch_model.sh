sed -i '12i\
@Keep\
data class Comment(\
    val id: String = UUID.randomUUID().toString(),\
    val authorId: String = "",\
    val authorName: String = "",\
    val authorHandle: String = "",\
    val timestamp: Long = System.currentTimeMillis(),\
    val content: String = ""\
) {\
    val formattedTime: String\
        get() {\
            val diff = System.currentTimeMillis() - timestamp\
            val minute = 60 * 1000L\
            val hour = 60 * minute\
            val day = 24 * hour\
            return when {\
                diff < minute -> "Baru saja"\
                diff < 2 * minute -> "1m"\
                diff < hour -> "${diff / minute}m"\
                diff < 2 * hour -> "1j"\
                diff < day -> "${diff / hour}j"\
                diff < 2 * day -> "Kemarin"\
                else -> "${diff / day}h"\
            }\
        }\
}\
' app/src/main/java/com/example/StoryViewModel.kt

sed -i 's/val commentsCount: Int = 0,/val commentsCount: Int = 0,\n    val comments: List<Comment> = emptyList(),/' app/src/main/java/com/example/StoryViewModel.kt
