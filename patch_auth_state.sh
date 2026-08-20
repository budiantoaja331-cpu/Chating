sed -i 's/data class Success(val userId: String, val userName: String) : AuthState()/data class Success(val userId: String, val userName: String, val profileImageUrl: String? = null) : AuthState()/' app/src/main/java/com/example/AuthViewModel.kt

sed -i 's/_authState.value = AuthState.Success(user.uid, user.displayName ?: "User")/_authState.value = AuthState.Success(user.uid, user.displayName ?: "User", user.photoUrl?.toString())/' app/src/main/java/com/example/AuthViewModel.kt
