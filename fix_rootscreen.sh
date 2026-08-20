sed -i '64,88d' app/src/main/java/com/example/MainActivity.kt
sed -i '63a\
@Composable\
fun RootScreen() {\
    val authViewModel: AuthViewModel = viewModel()\
    val authState by authViewModel.authState.collectAsState()\
    val context = LocalContext.current\
\
    when (val state = authState) {\
        is AuthState.Success -> {\
            LaunchedEffect(state.userId) {\
                android.widget.Toast.makeText(context, "Selamat datang, ${state.userName}!", android.widget.Toast.LENGTH_SHORT).show()\
            }\
            MainAppScreen(state.userId, state.userName)\
        }\
        else -> {\
            LoginScreen(authViewModel)\
        }\
    }\
}' app/src/main/java/com/example/MainActivity.kt
