sed -i 's/val currentUserId = "my_user_id"/val context = androidx.compose.ui.platform.LocalContext.current\n    val userManager = remember { UserManager(context) }\n    val currentUserId = remember { userManager.getUserId() }/' app/src/main/java/com/example/ChatListScreen.kt

sed -i 's/currentUserId = "my_user_id"/currentUserId = currentUserId/' app/src/main/java/com/example/StoryScreen.kt
