import re

path = 'app/src/main/AndroidManifest.xml'
with open(path, 'r') as f:
    content = f.read()

target = """    <uses-permission android:name="android.permission.INTERNET" />"""

replacement = """    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />"""

content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)
