# Add imports
sed -i '/import androidx.compose.material.icons.filled.Chat/a import androidx.compose.material.icons.filled.Call\nimport androidx.compose.material.icons.outlined.Call' app/src/main/java/com/example/MainActivity.kt

# Add to sealed class
sed -i '/object Chat : Screen/a \    object CallHistory : Screen("call_history", "Panggilan", Icons.Filled.Call, Icons.Outlined.Call)' app/src/main/java/com/example/MainActivity.kt

# Add to items list
sed -i '/Screen.Chat,/a \        Screen.CallHistory,' app/src/main/java/com/example/MainActivity.kt

# Add composable to NavHost
sed -i '/composable(Screen.Chat.route)/i \            composable(Screen.CallHistory.route) { \n                CallHistoryScreen(viewModel = viewModel(factory = appViewModelFactory), onNavigateToCall = { otherId, otherName, isVideo -> \n                    // placeholder navigation for now, later will go to actual call screen \n                }) \n            }' app/src/main/java/com/example/MainActivity.kt
