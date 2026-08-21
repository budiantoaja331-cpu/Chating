import os

filepath = 'app/build.gradle.kts'
with open(filepath, 'r') as f:
    content = f.read()

# Replace old App ID with the new one
content = content.replace('"122f5efcb34a4653b36e4360473be629"', '"085ae7b69ba544a887d74c00b7e9f0d9"')

with open(filepath, 'w') as f:
    f.write(content)
print("Updated build.gradle.kts")
