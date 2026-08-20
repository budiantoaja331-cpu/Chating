sed -i '/val context = androidx.compose.ui.platform.LocalContext.current/d' app/src/main/java/com/example/ChatListScreen.kt
sed -i '/val userManager = remember { UserManager(context) }/d' app/src/main/java/com/example/ChatListScreen.kt
sed -i '/val currentUserId = remember { userManager.getUserId() }/d' app/src/main/java/com/example/ChatListScreen.kt
sed -i 's/currentUserId = currentUserId/currentUserId = viewModel.currentUserId/' app/src/main/java/com/example/ChatListScreen.kt

sed -i '/val context = androidx.compose.ui.platform.LocalContext.current/d' app/src/main/java/com/example/StoryScreen.kt
sed -i '/val userManager = remember { UserManager(context) }/d' app/src/main/java/com/example/StoryScreen.kt
sed -i '/val currentUserId = remember { userManager.getUserId() }/d' app/src/main/java/com/example/StoryScreen.kt
sed -i 's/currentUserId = currentUserId/currentUserId = viewModel.currentUserId/' app/src/main/java/com/example/StoryScreen.kt
