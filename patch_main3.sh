sed -i 's/@Composable\nfun MainAppScreen() {/@Composable\nfun RootScreen() {\n    val authViewModel: AuthViewModel = viewModel()\n    val authState by authViewModel.authState.collectAsState()\n\n    when (val state = authState) {\n        is AuthState.Success -> {\n            MainAppScreen(state.userId, state.userName)\n        }\n        else -> {\n            LoginScreen(authViewModel)\n        }\n    }\n}\n\n@Composable\nfun MainAppScreen(userId: String, userName: String) {/' app/src/main/java/com/example/MainActivity.kt

sed -i 's/val userManager = remember { UserManager(context) }//' app/src/main/java/com/example/MainActivity.kt
sed -i 's/val userId = remember { userManager.getUserId() }//' app/src/main/java/com/example/MainActivity.kt
sed -i 's/val userName = remember { userManager.getUserName() }//' app/src/main/java/com/example/MainActivity.kt

sed -i 's/MainAppScreen()/RootScreen()/' app/src/main/java/com/example/MainActivity.kt
