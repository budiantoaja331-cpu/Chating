sed -i 's/@Composable/import androidx.compose.ui.platform.LocalContext\nimport androidx.lifecycle.viewmodel.compose.viewModel\nimport androidx.compose.runtime.remember\n\n@Composable/' app/src/main/java/com/example/MainActivity.kt

sed -i 's/    val navController = rememberNavController()/    val navController = rememberNavController()\n    val context = LocalContext.current\n    val userManager = remember { UserManager(context) }\n    val userId = remember { userManager.getUserId() }\n    val userName = remember { userManager.getUserName() }\n    val appViewModelFactory = remember { AppViewModelFactory(userId, userName) }/' app/src/main/java/com/example/MainActivity.kt

sed -i 's/NearbyScreen(onNavigateToChat/NearbyScreen(viewModel = viewModel(factory = appViewModelFactory), onNavigateToChat/' app/src/main/java/com/example/MainActivity.kt

sed -i 's/StoryScreen()/StoryScreen(viewModel = viewModel(factory = appViewModelFactory))/' app/src/main/java/com/example/MainActivity.kt

sed -i 's/ChatListScreen(onNavigateToChat/ChatListScreen(viewModel = viewModel(factory = appViewModelFactory), onNavigateToChat/' app/src/main/java/com/example/MainActivity.kt

