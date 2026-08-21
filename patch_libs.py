import re

path = 'gradle/libs.versions.toml'
with open(path, 'r') as f:
    content = f.read()

if 'firebase-messaging' not in content:
    content = content.replace('firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }', 'firebase-auth = { group = "com.google.firebase", name = "firebase-auth" }\nfirebase-messaging = { group = "com.google.firebase", name = "firebase-messaging" }')
    
with open(path, 'w') as f:
    f.write(content)
