import re

path = 'app/src/main/java/com/example/MainActivity.kt'
with open(path, 'r') as f:
    content = f.read()

target_screen = """    object Chat : Screen("chat", "Obrolan", Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat)"""
replacement_screen = """    object Chat : Screen("chat", "Obrolan", Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat)
    object Notifications : Screen("notifications", "Notifikasi", Icons.Filled.Notifications, Icons.Outlined.Notifications)"""
content = content.replace(target_screen, replacement_screen)

target_nav = """            composable(Screen.Story.route) { 
                StoryScreen(
                    viewModel = viewModel(factory = appViewModelFactory),
                    onNavigateToCreatePost = { navController.navigate(Screen.CreatePost.route) }
                ) 
            }"""
replacement_nav = """            composable(Screen.Story.route) { 
                StoryScreen(
                    viewModel = viewModel(factory = appViewModelFactory),
                    onNavigateToCreatePost = { navController.navigate(Screen.CreatePost.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
                ) 
            }
            composable(Screen.Notifications.route) {
                NotificationScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }"""
content = content.replace(target_nav, replacement_nav)

with open(path, 'w') as f:
    f.write(content)
