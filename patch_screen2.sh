sed -i 's/Box(/PullToRefreshBox(\n            isRefreshing = isRefreshing,\n            onRefresh = { viewModel.refreshStories() },/' app/src/main/java/com/example/StoryScreen.kt
