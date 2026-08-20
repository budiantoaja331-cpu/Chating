sed -i '/import androidx.navigation.navArgument/a import androidx.navigation.NavType' app/src/main/java/com/example/MainActivity.kt

# Replace the placeholder navigation in CallHistory
sed -i 's/\/\/ placeholder navigation for now, later will go to actual call screen/val channelId = "call_" + minOf(userId, otherId) + "_" + maxOf(userId, otherId)\n                    navController.navigate("callScreen/$channelId/$isVideo")/' app/src/main/java/com/example/MainActivity.kt

# Add CallScreen route
sed -i '/composable(Screen.Profile.route)/i \            composable(\n                route = "callScreen/{channelId}/{isVideo}",\n                arguments = listOf(\n                    navArgument("channelId") { type = NavType.StringType },\n                    navArgument("isVideo") { type = NavType.BoolType }\n                )\n            ) { backStackEntry ->\n                val channelId = backStackEntry.arguments?.getString("channelId") ?: ""\n                val isVideo = backStackEntry.arguments?.getBoolean("isVideo") ?: false\n                CallScreen(\n                    channelName = channelId,\n                    isVideoCall = isVideo,\n                    onNavigateBack = { navController.navigateUp() }\n                )\n            }' app/src/main/java/com/example/MainActivity.kt
