import re
path = 'app/src/main/java/com/example/StoryScreen.kt'
with open(path, 'r') as f:
    content = f.read()

target = """    selectedStoryForComments?.let { selectedStory ->"""
replacement = """    }
    }
    selectedStoryForComments?.let { selectedStory ->"""
content = content.replace(target, replacement)

with open(path, 'w') as f:
    f.write(content)
