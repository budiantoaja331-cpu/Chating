@Composable
fun RootScreen() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    
    androidx.compose.foundation.layout.Box(modifier = androidx.compose.foundation.layout.Modifier.fillMaxSize()) {
        when (val state = authState) {
            is AuthState.Success -> {
                LaunchedEffect(state.userId) {
                    android.widget.Toast.makeText(context, "Selamat datang, ${state.userName}!", android.widget.Toast.LENGTH_SHORT).show()
                }
                MainAppScreen(state.userId, state.userName, state.profileImageUrl)
            }
            else -> {
                LoginScreen(authViewModel)
            }
        }
        FirebaseDiagnosticUI()
    }
}
