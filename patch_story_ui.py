import re

path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target1 = """    val savedPostIds by viewModel.savedPostIds.collectAsState()"""
replacement1 = """    val savedPostIds by viewModel.savedPostIds.collectAsState()
    val feedFilter by viewModel.feedFilter.collectAsState()"""
content = content.replace(target1, replacement1)

target2 = """            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Cari postingan atau nama teman...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )
            
            if (searchQuery.isNotBlank()) {"""
replacement2 = """            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Cari postingan atau nama teman...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
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
            
            if (searchQuery.isNotBlank()) {"""
content = content.replace(target2, replacement2)

with open(path, 'w') as f:
    f.write(content)
