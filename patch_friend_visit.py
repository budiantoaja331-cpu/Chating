import re
path = 'app/src/main/java/com/example/FriendProfileViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(friendId).get().await()
                if (doc.exists()) {
                    val profile = doc.toObject(UserProfile::class.java)
                    if (profile != null) {
                        _uiState.value = FriendProfileUiState.Success(profile)
                    } else {"""
replacement = """    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(friendId).get().await()
                if (doc.exists()) {
                    val profile = doc.toObject(UserProfile::class.java)
                    if (profile != null) {
                        _uiState.value = FriendProfileUiState.Success(profile)
                        
                        // Send visit notification
                        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        if (currentUser != null && currentUser.uid != friendId) {
                            val myDoc = db.collection("users").document(currentUser.uid).get().await()
                            val myProfile = myDoc.toObject(UserProfile::class.java)
                            if (myProfile != null) {
                                val notif = Notification(
                                    targetUserId = friendId,
                                    sourceUserId = currentUser.uid,
                                    sourceUserName = myProfile.name,
                                    sourceUserAvatar = myProfile.avatarUrl,
                                    type = "visit"
                                )
                                db.collection("notifications").document(notif.id).set(notif)
                            }
                        }
                    } else {"""
content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)
