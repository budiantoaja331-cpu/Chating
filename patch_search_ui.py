import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

# Add states
state_target = """    val savedPostIds by viewModel.savedPostIds.collectAsState()
    val currentUserId = viewModel.currentUserId
    var selectedStoryForComments: Story? by remember { mutableStateOf(null) }"""
    
state_replacement = """    val savedPostIds by viewModel.savedPostIds.collectAsState()
    val currentUserId = viewModel.currentUserId
    var selectedStoryForComments: Story? by remember { mutableStateOf(null) }
    
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchStoriesResult by viewModel.searchStoriesResult.collectAsState()
    val searchUsersResult by viewModel.searchUsersResult.collectAsState()"""
content = content.replace(state_target, state_replacement)

# Update UI layout
layout_target = """    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshStories() },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            when (uiState) {"""

layout_replacement = """    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Cari postingan atau nama teman...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                leadingIcon = { Icon(androidx.compose.material.icons.Icons.Filled.androidx.compose.material.icons.filled.Search, contentDescription = "Search") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
            
            if (searchQuery.isNotBlank()) {
                if (isSearching) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (searchUsersResult.isNotEmpty()) {
                            item {
                                Text("Pengguna", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                            }
                            items(searchUsersResult) { user ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { /* TODO */ }
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Person, contentDescription = null, modifier = Modifier.size(40.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(user.name, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                        
                        if (searchStoriesResult.isNotEmpty()) {
                            item {
                                Text("Postingan", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                            }
                            items(searchStoriesResult, key = { it.id }) { story ->
                                StoryCard(
                                    story = story,
                                    currentUserId = currentUserId,
                                    isBookmarked = savedPostIds.contains(story.id),
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) },
                                    onReportClick = { viewModel.reportStory(story.id) }
                                )
                            }
                        }
                        
                        if (searchUsersResult.isEmpty() && searchStoriesResult.isEmpty()) {
                            item {
                                Text(
                                    "Tidak ada hasil ditemukan.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(32.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = { viewModel.refreshStories() },
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (uiState) {"""

content = content.replace(layout_target, layout_replacement)

# Close the column
close_target = """        if (selectedStoryForComments != null) {
            ModalBottomSheet("""
close_replacement = """        } // close Column
        
        if (selectedStoryForComments != null) {
            ModalBottomSheet("""
content = content.replace(close_target, close_replacement)

with open(path, 'w') as f:
    f.write(content)
