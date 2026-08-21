import re

path = 'app/src/main/java/com/example/MainActivity.kt'
with open(path, 'r') as f:
    content = f.read()

target = """        when (val state = authState) {
            is AuthState.Success -> {
                LaunchedEffect(state.userId) {
                    android.widget.Toast.makeText(context, "Selamat datang, ${state.userName}!", android.widget.Toast.LENGTH_SHORT).show()
                }
                MainAppScreen(state.userId, state.userName, state.profileImageUrl)
            }
            else -> {
                LoginScreen(authViewModel)
            }
        }"""

replacement = """        when (val state = authState) {
            is AuthState.Success -> {
                if (state.isProfileComplete) {
                    LaunchedEffect(state.userId) {
                        android.widget.Toast.makeText(context, "Selamat datang, ${state.userName}!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    MainAppScreen(state.userId, state.userName, state.profileImageUrl)
                } else {
                    val profileViewModel = remember(state.userId) { UserProfileViewModel(state.userId, state.userName, state.profileImageUrl) }
                    ProfileSetupScreen(
                        viewModel = profileViewModel,
                        onProfileComplete = {
                            authViewModel.markProfileComplete()
                        }
                    )
                }
            }
            else -> {
                LoginScreen(authViewModel)
            }
        }"""

if target in content:
    content = content.replace(target, replacement)
    with open(path, 'w') as f:
        f.write(content)
    print("Replaced successfully")
else:
    print("Target not found")
