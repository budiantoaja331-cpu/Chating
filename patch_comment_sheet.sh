sed -i '/^}$/c\
    selectedStoryForComments?.let { selectedStory ->\
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)\
        var newCommentContent by remember { mutableStateOf("") }\
        // Find the most up-to-date story object in case a comment was just added\
        val upToDateStory = (uiState as? StoryUiState.Success)?.stories?.find { it.id == selectedStory.id } ?: selectedStory\
        ModalBottomSheet(\
            onDismissRequest = { selectedStoryForComments = null },\
            sheetState = sheetState,\
            containerColor = MaterialTheme.colorScheme.surface\
        ) {\
            Column(\
                modifier = Modifier\
                    .fillMaxWidth()\
                    .padding(16.dp)\
                    .padding(bottom = 32.dp)\
            ) {\
                Text(\
                    text = "Komentar",\
                    style = MaterialTheme.typography.titleLarge,\
                    fontWeight = FontWeight.Bold,\
                    modifier = Modifier.padding(bottom = 16.dp)\
                )\
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {\
                    items(upToDateStory.comments) { comment ->\
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {\
                            Row(verticalAlignment = Alignment.CenterVertically) {\
                                Text(\
                                    text = comment.authorName,\
                                    fontWeight = FontWeight.Bold,\
                                    style = MaterialTheme.typography.bodyMedium\
                                )\
                                Spacer(modifier = Modifier.width(8.dp))\
                                Text(\
                                    text = comment.formattedTime,\
                                    style = MaterialTheme.typography.bodySmall,\
                                    color = MaterialTheme.colorScheme.onSurfaceVariant\
                                )\
                            }\
                            Spacer(modifier = Modifier.height(4.dp))\
                            Text(\
                                text = comment.content,\
                                style = MaterialTheme.typography.bodyMedium\
                            )\
                        }\
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)\
                    }\
                }\
                Spacer(modifier = Modifier.height(16.dp))\
                Row(verticalAlignment = Alignment.CenterVertically) {\
                    OutlinedTextField(\
                        value = newCommentContent,\
                        onValueChange = { newCommentContent = it },\
                        placeholder = { Text("Tambahkan komentar...") },\
                        modifier = Modifier.weight(1f),\
                        maxLines = 3\
                    )\
                    Spacer(modifier = Modifier.width(8.dp))\
                    Button(\
                        onClick = {\
                            viewModel.addComment(upToDateStory.id, newCommentContent)\
                            newCommentContent = ""\
                        },\
                        enabled = newCommentContent.isNotBlank()\
                    ) {\
                        Text("Kirim")\
                    }\
                }\
            }\
        }\
    }\
}\
' app/src/main/java/com/example/StoryScreen.kt
