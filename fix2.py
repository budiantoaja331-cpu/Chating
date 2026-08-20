import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# Replace the broken RootScreen
pattern = r'@Composable\s*fun RootScreen\(\)\s*\{.*?FirebaseDiagnosticUI\(\)\s*\}'
replacement = """@Composable
fun RootScreen() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    
    Box(modifier = Modifier.fillMaxSize()) {
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
}"""

content = re.sub(pattern, replacement, content, flags=re.DOTALL)
with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
