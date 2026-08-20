sed -i 's/        throw IllegalArgumentException("Unknown ViewModel class")/        if (modelClass.isAssignableFrom(UserProfileViewModel::class.java)) {\n            @Suppress("UNCHECKED_CAST")\n            return UserProfileViewModel(userId) as T\n        }\n        throw IllegalArgumentException("Unknown ViewModel class")/' app/src/main/java/com/example/AppViewModelFactory.kt

sed -i 's/UserProfileScreen()/UserProfileScreen(viewModel = viewModel(factory = appViewModelFactory))/' app/src/main/java/com/example/MainActivity.kt
