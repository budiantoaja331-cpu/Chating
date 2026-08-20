sed -i -e '/\/\/ Comment Button/,/modifier = Modifier.padding(4.dp)/c\
                // Comment Button\
                Row(\
                    verticalAlignment = Alignment.CenterVertically,\
                    modifier = Modifier\
                        .clip(RoundedCornerShape(16.dp))\
                        .clickable { onCommentClick() }\
                        .padding(4.dp)\
' app/src/main/java/com/example/StoryScreen.kt
