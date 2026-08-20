sed -i -e '/\/\/ Like Button/,/Spacer(modifier = Modifier.width(6.dp))/c\
                // Like Button\
                val scale by animateFloatAsState(\
                    targetValue = if (isLiked) 1.2f else 1f,\
                    animationSpec = spring(\
                        dampingRatio = Spring.DampingRatioMediumBouncy,\
                        stiffness = Spring.StiffnessLow\
                    ),\
                    label = "like_animation"\
                )\
                Row(\
                    verticalAlignment = Alignment.CenterVertically,\
                    modifier = Modifier\
                        .clip(RoundedCornerShape(16.dp))\
                        .clickable { onLikeClick() }\
                        .padding(4.dp)\
                ) {\
                    Icon(\
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,\
                        contentDescription = "Like",\
                        tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,\
                        modifier = Modifier.size(20.dp).scale(scale)\
                    )\
                    Spacer(modifier = Modifier.width(6.dp))' app/src/main/java/com/example/StoryScreen.kt
