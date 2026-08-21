import re

path = 'app/src/main/java/com/example/UserProfileViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    fun updateProfile(name: String, bio: String, nickname: String = "", age: Int = 0, interests: String = "") {"""
replacement = """    fun updateAvatar(uri: android.net.Uri, onComplete: (Boolean) -> Unit) {
        val currentState = _uiState.value
        if (currentState !is UserProfileUiState.Success) return

        viewModelScope.launch {
            try {
                val storageRef = com.google.firebase.storage.FirebaseStorage.getInstance().reference
                val avatarRef = storageRef.child("avatars/$userId.jpg")
                avatarRef.putFile(uri).await()
                val downloadUrl = avatarRef.downloadUrl.await().toString()
                
                val updatedProfile = currentState.profile.copy(avatarUrl = downloadUrl)
                _uiState.value = UserProfileUiState.Success(updatedProfile)
                profilesCollection.document(userId).set(updatedProfile).await()
                onComplete(true)
            } catch (e: Exception) {
                android.util.Log.e("UserProfileViewModel", "Error updating avatar", e)
                onComplete(false)
            }
        }
    }

    fun updateProfile(name: String, bio: String, nickname: String = "", age: Int = 0, interests: String = "") {"""
content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)
