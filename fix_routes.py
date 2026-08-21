import re

path = 'app/src/main/java/com/example/MainActivity.kt'
with open(path, 'r') as f:
    content = f.read()

screen_target = """    object Story : Screen("feed", "Feed", Icons.Filled.DynamicFeed, Icons.Outlined.DynamicFeed)"""
screen_replacement = """    object Story : Screen("feed", "Feed", Icons.Filled.DynamicFeed, Icons.Outlined.DynamicFeed)
    object CreatePost : Screen("create_post", "Buat Postingan", Icons.Filled.Add, Icons.Filled.Add)"""

content = content.replace(screen_target, screen_replacement)

route_target = """            composable(Screen.Story.route) { StoryScreen(viewModel = viewModel(factory = appViewModelFactory)) }"""
route_replacement = """            composable(Screen.Story.route) { 
                StoryScreen(
                    viewModel = viewModel(factory = appViewModelFactory),
                    onNavigateToCreatePost = { navController.navigate(Screen.CreatePost.route) }
                ) 
            }
            composable(Screen.CreatePost.route) {
                CreatePostScreen(
                    viewModel = viewModel(factory = appViewModelFactory),
                    userProfileViewModel = viewModel(factory = appViewModelFactory),
                    onNavigateBack = { navController.navigateUp() }
                )
            }"""

content = content.replace(route_target, route_replacement)

with open(path, 'w') as f:
    f.write(content)
print("Updated routes")
