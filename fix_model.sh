sed -i '/val likedByUsers: List<String> = emptyList(),/d' app/src/main/java/com/example/StoryViewModel.kt
sed -i '/val formattedTime: String = ""/c\
    val likedByUsers: List<String> = emptyList()\
) {\
    val formattedTime: String\
        get() {\
            val diff = System.currentTimeMillis() - timestamp\
            val minute = 60 * 1000L\
            val hour = 60 * minute\
            val day = 24 * hour\
            return when {\
                diff < minute -> "Baru saja"\
                diff < 2 * minute -> "1 menit yang lalu"\
                diff < hour -> "${diff / minute} menit yang lalu"\
                diff < 2 * hour -> "1 jam yang lalu"\
                diff < day -> "${diff / hour} jam yang lalu"\
                diff < 2 * day -> "Kemarin"\
                else -> "${diff / day} hari yang lalu"\
            }\
        }' app/src/main/java/com/example/StoryViewModel.kt
