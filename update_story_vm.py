import re

path = 'app/src/main/java/com/example/StoryViewModel.kt'
with open(path, 'r') as f:
    content = f.read()

content = content.replace('private fun loadStories()', 'fun loadStories()')
# add a simple refresh method if we want, or just use loadStories().
content = content.replace('fun loadStories() {', 'fun refreshStories() {\n        loadStories()\n    }\n\n    fun loadStories() {')

with open(path, 'w') as f:
    f.write(content)
print("Updated view model")
