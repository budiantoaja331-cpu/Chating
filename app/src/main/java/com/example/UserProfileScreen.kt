package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel = viewModel(),
    storyViewModel: StoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val storyUiState by storyViewModel.uiState.collectAsState()
    val savedPostIds by storyViewModel.savedPostIds.collectAsState()
    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editBio by remember { mutableStateOf("") }
    var editNickname by remember { mutableStateOf("") }
    var editAge by remember { mutableStateOf("") }
    var editInterests by remember { mutableStateOf("") }

    var selectedStoryForComments: Story? by remember { mutableStateOf(null) }
    var selectedTab by remember { mutableStateOf(0) } // 0 = My Posts, 1 = Saved

    val predefinedInterests = listOf("conten", "hiburan", "cari patner fantasi", "d'patner", "cari pasangan seumur hidup", "sewa pacar", "penyedia pacar sewa")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya", fontWeight = FontWeight.Bold) },
                actions = {
                    if (uiState is UserProfileUiState.Success) {
                        IconButton(onClick = {
                            if (isEditing) {
                                // Save changes
                                viewModel.updateProfile(editName, editBio, editNickname, editAge.toIntOrNull() ?: 0, editInterests)
                                isEditing = false
                            } else {
                                // Enter edit mode
                                val profile = (uiState as UserProfileUiState.Success).profile
                                editName = profile.name
                                editBio = profile.bio
                                editNickname = profile.nickname
                                editAge = if (profile.age > 0) profile.age.toString() else ""
                                editInterests = profile.interests
                                isEditing = true
                            }
                        }) {
                            Icon(
                                imageVector = if (isEditing) Icons.Filled.Edit else Icons.Filled.Edit,
                                contentDescription = if (isEditing) "Simpan Profil" else "Edit Profil",
                                tint = if (isEditing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            when (uiState) {
                is UserProfileUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 64.dp))
                }
                is UserProfileUiState.Error -> {
                    val message = (uiState as UserProfileUiState.Error).message
                    Text(
                        text = "Error: $message",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(32.dp)
                    )
                }
                is UserProfileUiState.Success -> {
                    val profile = (uiState as UserProfileUiState.Success).profile
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (profile.avatarUrl.isNotBlank()) {
                                        AsyncImage(
                                            model = profile.avatarUrl,
                                            contentDescription = "Profile Picture",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Filled.Person,
                                            contentDescription = "Avatar",
                                            modifier = Modifier.size(64.dp),
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(24.dp))
                                if (isEditing) {
                                    OutlinedTextField(
                                        value = editName,
                                        onValueChange = { editName = it },
                                        label = { Text("Nama") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = editBio,
                                        onValueChange = { editBio = it },
                                        label = { Text("Bio") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        maxLines = 5
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = editNickname,
                                        onValueChange = { editNickname = it },
                                        label = { Text("Nama Panggilan") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    OutlinedTextField(
                                        value = editAge,
                                        onValueChange = { editAge = it },
                                        label = { Text("Umur") },
                                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Pilih Minat Anda:",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                                    )
                                    
                                    val selectedInterests = editInterests.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toMutableSet()
                                    
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        predefinedInterests.forEach { interest ->
                                            val isSelected = selectedInterests.contains(interest)
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = {
                                                    if (isSelected) {
                                                        selectedInterests.remove(interest)
                                                    } else {
                                                        selectedInterests.add(interest)
                                                    }
                                                    editInterests = selectedInterests.joinToString(", ")
                                                },
                                                label = { Text(interest) }
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        onClick = {
                                            viewModel.updateProfile(editName, editBio, editNickname, editAge.toIntOrNull() ?: 0, editInterests)
                                            isEditing = false
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Simpan Profil")
                                    }
                                } else {
                                    Text(
                                        text = profile.name.ifEmpty { "No Name" },
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = profile.bio.ifEmpty { "No bio added yet." },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                                            Text("Data Diri", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text("Panggilan: ${profile.nickname.ifEmpty { "-" }}", style = MaterialTheme.typography.bodyMedium)
                                            Text("Umur: ${if (profile.age > 0) "${profile.age} tahun" else "-"}", style = MaterialTheme.typography.bodyMedium)
                                            
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text("Minat:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            if (profile.interests.isNotEmpty()) {
                                                FlowRow(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    profile.interests.split(",").map { it.trim() }.filter { it.isNotEmpty() }.forEach { interest ->
                                                        AssistChip(
                                                            onClick = { },
                                                            label = { Text(interest) }
                                                        )
                                                    }
                                                }
                                            } else {
                                                Text("-", style = MaterialTheme.typography.bodyMedium)
                                            }
                                        }
                                    }
                                }
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            
                            TabRow(selectedTabIndex = selectedTab) {
                                Tab(
                                    selected = selectedTab == 0,
                                    onClick = { selectedTab = 0 },
                                    text = { Text("Unggahan Saya") }
                                )
                                Tab(
                                    selected = selectedTab == 1,
                                    onClick = { selectedTab = 1 },
                                    text = { Text("Tersimpan") }
                                )
                            }
                        }

                        // My Posts or Saved Posts
                        if (storyUiState is StoryUiState.Success) {
                            val allStories = (storyUiState as StoryUiState.Success).stories
                            val filteredStories = if (selectedTab == 0) {
                                allStories.filter { it.authorId == storyViewModel.currentUserId }
                            } else {
                                allStories.filter { savedPostIds.contains(it.id) }
                            }
                            
                            if (filteredStories.isEmpty()) {
                                item {
                                    Text(
                                        text = if (selectedTab == 0) "Belum ada unggahan." else "Belum ada unggahan tersimpan.",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(32.dp)
                                    )
                                }
                            } else {
                                items(filteredStories, key = { it.id }) { story ->
                                    StoryCard(
                                        story = story,
                                        currentUserId = storyViewModel.currentUserId,
                                        isBookmarked = savedPostIds.contains(story.id),
                                        onLikeClick = { storyViewModel.toggleLike(story.id, story.likedByUsers) },
                                        onCommentClick = { selectedStoryForComments = story },
                                        onBookmarkClick = { storyViewModel.toggleBookmark(story.id) },
                                        onBlockClick = { storyViewModel.blockUser(story.authorId) },
                                        onReportClick = { storyViewModel.reportStory(story.id) }
                                    )
                                }
                            }
                        } else if (storyUiState is StoryUiState.Loading) {
                            item {
                                CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                            }
                        }
                    }
                }
            }
        }
    }
    
    selectedStoryForComments?.let { selectedStory ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var newCommentContent by remember { mutableStateOf("") }
        val upToDateStory = (storyUiState as? StoryUiState.Success)?.stories?.find { it.id == selectedStory.id } ?: selectedStory
        ModalBottomSheet(
            onDismissRequest = { selectedStoryForComments = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Komentar",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(upToDateStory.comments) { comment ->
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = comment.authorName,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = comment.formattedTime,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = comment.content,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newCommentContent,
                        onValueChange = { newCommentContent = it },
                        placeholder = { Text("Tambahkan komentar...") },
                        modifier = Modifier.weight(1f),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            storyViewModel.addComment(upToDateStory.id, newCommentContent)
                            newCommentContent = ""
                        },
                        enabled = newCommentContent.isNotBlank()
                    ) {
                        Text("Kirim")
                    }
                }
            }
        }
    }
}
