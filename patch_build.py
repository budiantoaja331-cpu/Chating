import re

path = 'app/build.gradle.kts'
with open(path, 'r') as f:
    content = f.read()

if 'firebase.messaging' not in content:
    content = content.replace('implementation(libs.firebase.auth)', 'implementation(libs.firebase.auth)\n    implementation(libs.firebase.messaging)')
    
with open(path, 'w') as f:
    f.write(content)
