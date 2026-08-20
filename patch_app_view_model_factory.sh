sed -i 's/class AppViewModelFactory(/class AppViewModelFactory(\n    private val profileImageUrl: String? = null,/' app/src/main/java/com/example/AppViewModelFactory.kt
sed -i 's/return UserProfileViewModel(userId) as T/return UserProfileViewModel(userId, userName, profileImageUrl) as T/' app/src/main/java/com/example/AppViewModelFactory.kt
