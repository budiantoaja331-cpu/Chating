sed -i '/val currentUserId = viewModel.currentUserId/d' app/src/main/java/com/example/ChatListScreen.kt
sed -i 's/val uiState by viewModel.uiState.collectAsState()/val uiState by viewModel.uiState.collectAsState()\n    val currentUserId = viewModel.currentUserId/' app/src/main/java/com/example/ChatListScreen.kt

sed -i '/val currentUserId = viewModel.currentUserId/d' app/src/main/java/com/example/StoryScreen.kt
sed -i 's/val uiState by viewModel.uiState.collectAsState()/val uiState by viewModel.uiState.collectAsState()\n    val currentUserId = viewModel.currentUserId/' app/src/main/java/com/example/StoryScreen.kt
