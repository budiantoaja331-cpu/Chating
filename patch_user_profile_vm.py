import re

path = 'app/src/main/java/com/example/UserProfileViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    private val profilesCollection = db.collection("users")
    
    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)"""

replacement = """    private val profilesCollection = db.collection("users")
    
    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    
    private val _blockedUserProfiles = MutableStateFlow<List<UserProfile>>(emptyList())
    val blockedUserProfiles: StateFlow<List<UserProfile>> = _blockedUserProfiles.asStateFlow()

    fun loadBlockedUsers(blockedUserIds: List<String>) {
        if (blockedUserIds.isEmpty()) {
            _blockedUserProfiles.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                // Fetch in chunks of 10 to respect Firestore whereIn limits
                val chunks = blockedUserIds.chunked(10)
                val profiles = mutableListOf<UserProfile>()
                for (chunk in chunks) {
                    val snapshot = profilesCollection.whereIn(com.google.firebase.firestore.FieldPath.documentId(), chunk).get().await()
                    profiles.addAll(snapshot.toObjects(UserProfile::class.java))
                }
                _blockedUserProfiles.value = profiles
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error loading blocked users", e)
            }
        }
    }
    
    fun unblockUser(targetUserId: String) {
        viewModelScope.launch {
            try {
                // Remove from UserSessionManager immediately so other screens update
                UserSessionManager.unblockUser(targetUserId)
                
                // Then fetch the updated list 
                // The UserSessionManager is already updating Firestore
                
                // Update local blocked user profiles list
                _blockedUserProfiles.update { it.filter { profile -> profile.id != targetUserId } }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error unblocking user", e)
            }
        }
    }"""

content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)
