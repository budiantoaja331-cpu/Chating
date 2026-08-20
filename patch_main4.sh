sed -i 's/@Composable\nfun MainAppScreen() {/@Composable\nfun RootScreen() {\n    val authViewModel: AuthViewModel = viewModel()\n    val authState by authViewModel.authState.collectAsState()\n\n    when (val state = authState) {\n        is AuthState.Success -> {\n            MainAppScreen(state.userId, state.userName)\n        }\n        else -> {\n            LoginScreen(authViewModel)\n        }\n    }\n}\n\n@Composable\nfun MainAppScreen(userId: String, userName: String) {/' app/src/main/java/com/example/MainActivity.kt

sed -i '/val userManager = remember { UserManager(context) }/d' app/src/main/java/com/example/MainActivity.kt
sed -i '/val userId = remember { userManager.getUserId() }/d' app/src/main/java/com/example/MainActivity.kt
sed -i '/val userName = remember { userManager.getUserName() }/d' app/src/main/java/com/example/MainActivity.kt

sed -i 's/MainAppScreen()/RootScreen()/g' app/src/main/java/com/example/MainActivity.kt
