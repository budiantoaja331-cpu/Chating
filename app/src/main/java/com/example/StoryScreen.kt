package com.example

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryScreen(
    viewModel: StoryViewModel = viewModel(),
    onNavigateToCreatePost: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToUserProfile: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val savedPostIds by viewModel.savedPostIds.collectAsState()
    val feedFilter by viewModel.feedFilter.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentUserId = viewModel.currentUserId
    var selectedStoryForComments: Story? by remember { mutableStateOf(null) }
    
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val searchStoriesResult by viewModel.searchStoriesResult.collectAsState()
    val searchUsersResult by viewModel.searchUsersResult.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Beranda", 
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifikasi"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreatePost,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, "New Post")
            }
        },
        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Cari postingan, nama, atau @username...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Text("✕", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
            
            if (searchQuery.isBlank()) {
                val filters = listOf("All", "Following", "Trending")
                TabRow(
                    selectedTabIndex = filters.indexOf(feedFilter).takeIf { it >= 0 } ?: 0,
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    filters.forEachIndexed { index, filter ->
                        Tab(
                            selected = feedFilter == filter,
                            onClick = { viewModel.setFeedFilter(filter) },
                            text = { 
                                Text(
                                    text = when (filter) {
                                        "All" -> "Semua"
                                        "Following" -> "Diikuti"
                                        "Trending" -> "Populer"
                                        else -> filter
                                    }
                                )
                            }
                        )
                    }
                }
            }
            
            if (searchQuery.isNotBlank()) {
                if (isSearching) {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (searchUsersResult.isEmpty() && searchStoriesResult.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔍", style = MaterialTheme.typography.displayMedium)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tidak ditemukan pengguna atau postingan dengan kata kunci \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (searchUsersResult.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Pengguna (${searchUsersResult.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(searchUsersResult, key = { "user_${it.id}" }) { user ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .clickable { onNavigateToUserProfile(user.id) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 1.dp
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (user.avatarUrl.isNotEmpty()) {
                                            AsyncImage(
                                                model = user.avatarUrl,
                                                contentDescription = user.name,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(CircleShape)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(48.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = user.name.take(1).uppercase().ifEmpty { "U" },
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = user.name.ifEmpty { "Pengguna" },
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                            val handleText = if (user.nickname.isNotEmpty()) "@${user.nickname}" else "@${user.id.take(8)}"
                                            Text(
                                                text = handleText,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (user.bio.isNotEmpty()) {
                                                Text(
                                                    text = user.bio,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (searchStoriesResult.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Postingan (${searchStoriesResult.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(searchStoriesResult, key = { "story_${it.id}" }) { story ->
                                StoryCard(
                                    story = story,
                                    currentUserId = currentUserId,
                                    isBookmarked = savedPostIds.contains(story.id),
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) },
                                    onReportClick = { viewModel.reportStory(story.id) },
                                    onShareClick = {
                                        val sendIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_TEXT, "Lihat postingan dari ${story.authorName} di Chatmicall: \"${story.content}\"")
                                            type = "text/plain"
                                        }
                                        val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                        context.startActivity(shareIntent)
                                    }
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
                    when (uiState) {
                is StoryUiState.Loading -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(4) {
                            StorySkeleton()
                        }
                    }
                }
                is StoryUiState.Error -> {
                    val message = (uiState as StoryUiState.Error).message
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(32.dp)
                    )
                }
                is StoryUiState.Success -> {
                    val stories = (uiState as StoryUiState.Success).stories
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        if (stories.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Belum ada postingan. Jadilah yang pertama!",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            items(stories, key = { it.id }) { story ->
                                StoryCard(
                                    story = story,
                                    currentUserId = viewModel.currentUserId,
                                    isBookmarked = savedPostIds.contains(story.id),
                                    onLikeClick = { viewModel.toggleLike(story.id, story.likedByUsers) },
                                    onCommentClick = { selectedStoryForComments = story },
                                    onBookmarkClick = { viewModel.toggleBookmark(story.id) },
                                    onBlockClick = { viewModel.blockUser(story.authorId) },
                                    onReportClick = { viewModel.reportStory(story.id) },
                                    onShareClick = {
                                        val sendIntent = android.content.Intent().apply {
                                            action = android.content.Intent.ACTION_SEND
                                            putExtra(android.content.Intent.EXTRA_TEXT, "Lihat postingan dari ${story.authorName} di Chatmicall: \"${story.content}\"")
                                            type = "text/plain"
                                        }
                                        val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                        context.startActivity(shareIntent)
                                    }
                                )
                            }
                        }
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
        
        LaunchedEffect(selectedStory.id) {
            viewModel.loadCommentsForStory(selectedStory.id)
        }
        
        DisposableEffect(selectedStory.id) {
            onDispose { viewModel.clearCommentsListener() }
        }
        
        val comments by viewModel.currentComments.collectAsState()
        
        // Find the most up-to-date story object in case a comment was just added
        val upToDateStory = (uiState as? StoryUiState.Success)?.stories?.find { it.id == selectedStory.id } ?: selectedStory
        ModalBottomSheet(
            onDismissRequest = { 
                selectedStoryForComments = null 
                viewModel.clearCommentsListener()
            },
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
                    items(comments) { comment ->
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
                                text = parseMarkdownLite(comment.content),
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
                            viewModel.addComment(upToDateStory.id, newCommentContent)
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


@Composable
fun StoryCard(
    story: Story,
    currentUserId: String,
    isBookmarked: Boolean = false,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onBookmarkClick: () -> Unit = {},
    onBlockClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onShareClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (story.authorAvatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = story.authorAvatarUrl,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = if (story.authorName.isNotEmpty()) story.authorName.first().toString().uppercase() else "?",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = story.authorName,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = story.authorHandle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = story.formattedTime,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (story.authorId != currentUserId) {
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Filled.MoreVert, contentDescription = "Pilihan lainnya")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Laporkan Postingan") },
                                onClick = {
                                    expanded = false
                                    onReportClick()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Blokir Pengguna") },
                                onClick = { 
                                    expanded = false
                                    onBlockClick() 
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content
            Text(
                text = parseMarkdownLite(story.content),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Actions
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 48.dp, top = 8.dp)
            ) {
                val isLiked = story.likedByUsers.contains(currentUserId)
                
                // Like Button
                val scale by animateFloatAsState(
                    targetValue = if (isLiked) 1.2f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "like_animation"
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onLikeClick() }
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp).scale(scale)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = story.likesCount.toString(),
                        color = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Comment Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onCommentClick() }
                        .padding(4.dp)

                ) {
                    Icon(
                        imageVector = Icons.Filled.ChatBubbleOutline,
                        contentDescription = "Comment",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = story.commentsCount.toString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Share Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onShareClick() }
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // Bookmark Button
                // val isBookmarked handled by param
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onBookmarkClick() }
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun StorySkeleton() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .shimmerEffect()
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.height(16.dp).fillMaxWidth(0.5f).shimmerEffect())
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.height(12.dp).fillMaxWidth(0.3f).shimmerEffect())
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Content
            Box(modifier = Modifier.height(16.dp).fillMaxWidth().shimmerEffect())
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.height(16.dp).fillMaxWidth(0.8f).shimmerEffect())
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.height(16.dp).fillMaxWidth(0.6f).shimmerEffect())
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Actions
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 48.dp)
            ) {
                Box(modifier = Modifier.size(24.dp).shimmerEffect())
                Box(modifier = Modifier.size(24.dp).shimmerEffect())
                Box(modifier = Modifier.size(24.dp).shimmerEffect())
                Box(modifier = Modifier.size(24.dp).shimmerEffect())
            }
        }
    }
}
