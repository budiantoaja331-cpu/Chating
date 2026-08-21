import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    selectedStoryForComments?.let { selectedStory ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var newCommentContent by remember { mutableStateOf("") }
        
        // Find the most up-to-date story object in case a comment was just added
        val upToDateStory = (uiState as? StoryUiState.Success)?.stories?.find { it.id == selectedStory.id } ?: selectedStory

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
    }"""

replacement = """    selectedStoryForComments?.let { selectedStory ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var newCommentContent by remember { mutableStateOf("") }
        
        // Find the most up-to-date story object for comments count/other details
        val upToDateStory = (uiState as? StoryUiState.Success)?.stories?.find { it.id == selectedStory.id } ?: selectedStory
        
        val commentsList by viewModel.currentComments.collectAsState()
        
        LaunchedEffect(selectedStory.id) {
            viewModel.loadCommentsForStory(selectedStory.id)
        }
        
        DisposableEffect(selectedStory.id) {
            onDispose {
                viewModel.clearCommentsListener()
            }
        }

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
                    if (commentsList.isEmpty()) {
                        item {
                            Text(
                                text = "Belum ada komentar.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                    items(commentsList) { comment ->
                        Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                if (comment.authorAvatarUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = comment.authorAvatarUrl,
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text(
                                        text = if (comment.authorName.isNotEmpty()) comment.authorName.first().toString().uppercase() else "?",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
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
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = comment.content,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
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
    }"""

content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)

print("Updated comments bottom sheet")
