import re

path = 'app/src/main/java/com/example/FriendProfileViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    private val _uiState = MutableStateFlow<FriendProfileUiState>(FriendProfileUiState.Loading)
    val uiState: StateFlow<FriendProfileUiState> = _uiState.asStateFlow()

    init {"""

replacement = """    private val _uiState = MutableStateFlow<FriendProfileUiState>(FriendProfileUiState.Loading)
    val uiState: StateFlow<FriendProfileUiState> = _uiState.asStateFlow()
    
    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing.asStateFlow()

    init {
        checkIfFollowing()"""

content = content.replace(target, replacement)

target2 = """    private fun loadProfile() {"""
replacement2 = """    private fun checkIfFollowing() {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(currentUser).get().await()
                if (doc.exists()) {
                    val profile = doc.toObject(UserProfile::class.java)
                    _isFollowing.value = profile?.following?.contains(friendId) ?: false
                }
            } catch (e: Exception) {
                Log.e("FriendProfile", "Error checking following state", e)
            }
        }
    }
    
    fun toggleFollow() {
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        val currentlyFollowing = _isFollowing.value
        _isFollowing.value = !currentlyFollowing
        
        viewModelScope.launch {
            try {
                if (currentlyFollowing) {
                    db.collection("users").document(currentUser).update("following", com.google.firebase.firestore.FieldValue.arrayRemove(friendId)).await()
                    db.collection("users").document(friendId).update("followers", com.google.firebase.firestore.FieldValue.arrayRemove(currentUser)).await()
                } else {
                    db.collection("users").document(currentUser).update("following", com.google.firebase.firestore.FieldValue.arrayUnion(friendId)).await()
                    db.collection("users").document(friendId).update("followers", com.google.firebase.firestore.FieldValue.arrayUnion(currentUser)).await()
                }
            } catch (e: Exception) {
                Log.e("FriendProfile", "Error toggling follow", e)
                _isFollowing.value = currentlyFollowing // revert on failure
            }
        }
    }

    private fun loadProfile() {"""
content = content.replace(target2, replacement2)

with open(path, 'w') as f:
    f.write(content)
