sed -i '11i\import kotlinx.coroutines.launch' app/src/main/java/com/example/StoryViewModel.kt
sed -i '11i\import androidx.lifecycle.viewModelScope' app/src/main/java/com/example/StoryViewModel.kt
sed -i '11i\import kotlinx.coroutines.delay' app/src/main/java/com/example/StoryViewModel.kt

sed -i 's/val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()/val uiState: StateFlow<StoryUiState> = _uiState.asStateFlow()\n\n    private val _isRefreshing = MutableStateFlow(false)\n    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()/' app/src/main/java/com/example/StoryViewModel.kt
