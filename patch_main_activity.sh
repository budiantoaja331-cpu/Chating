sed -i 's/MainAppScreen(state.userId, state.userName)/MainAppScreen(state.userId, state.userName, state.profileImageUrl)/' app/src/main/java/com/example/MainActivity.kt
sed -i 's/fun MainAppScreen(userId: String, userName: String)/fun MainAppScreen(userId: String, userName: String, profileImageUrl: String? = null)/' app/src/main/java/com/example/MainActivity.kt
