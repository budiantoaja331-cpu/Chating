sed -i -e '/\/\/ Share Button/,/modifier = Modifier.size(20.dp)\n                    )\n                }/c\
                // Share Button\
                Row(\
                    verticalAlignment = Alignment.CenterVertically,\
                    modifier = Modifier.padding(4.dp)\
                ) {\
                    Icon(\
                        imageVector = Icons.Filled.Share,\
                        contentDescription = "Share",\
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,\
                        modifier = Modifier.size(20.dp)\
                    )\
                }\
                \
                // Bookmark Button\
                val isBookmarked = story.bookmarkedByUsers.contains(currentUserId)\
                Row(\
                    verticalAlignment = Alignment.CenterVertically,\
                    modifier = Modifier\
                        .clip(RoundedCornerShape(16.dp))\
                        .clickable { onBookmarkClick() }\
                        .padding(4.dp)\
                ) {\
                    Icon(\
                        imageVector = if (isBookmarked) androidx.compose.material.icons.Icons.Filled.Bookmark else androidx.compose.material.icons.Icons.Filled.BookmarkBorder,\
                        contentDescription = "Bookmark",\
                        tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,\
                        modifier = Modifier.size(20.dp)\
                    )\
                }\
' app/src/main/java/com/example/StoryScreen.kt
