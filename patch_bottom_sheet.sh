sed -i -e '/if (showAddDialog) {/,/^}/c\
    if (showAddDialog) {\
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)\
        var newStoryContent by remember { mutableStateOf("") }\
        ModalBottomSheet(\
            onDismissRequest = { showAddDialog = false },\
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
                    text = "Tulis Story Baru",\
                    style = MaterialTheme.typography.titleLarge,\
                    fontWeight = FontWeight.Bold,\
                    modifier = Modifier.padding(bottom = 16.dp)\
                )\
                OutlinedTextField(\
                    value = newStoryContent,\
                    onValueChange = { newStoryContent = it },\
                    placeholder = { Text("Apa yang sedang kamu pikirkan?") },\
                    modifier = Modifier\
                        .fillMaxWidth()\
                        .height(150.dp)\
                )\
                Spacer(modifier = Modifier.height(16.dp))\
                Button(\
                    onClick = {\
                        viewModel.addStory(newStoryContent)\
                        showAddDialog = false\
                    },\
                    modifier = Modifier.align(Alignment.End),\
                    enabled = newStoryContent.isNotBlank()\
                ) {\
                    Text("Kirim")\
                }\
            }\
        }\
    }\
}' app/src/main/java/com/example/StoryScreen.kt
