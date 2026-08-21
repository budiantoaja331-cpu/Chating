import os

filepath = 'app/src/main/java/com/example/CallViewModel.kt'
with open(filepath, 'r') as f:
    content = f.read()

target = """            // Bergabung ke Channel. 
            // Catatan: Jika Sertifikat Agora dinyalakan secara ketat (Strict Token),
            // parameter token (null) ini kelak harus diganti dengan Token Generator.
            rtcEngine?.joinChannel(null, channelName, 0, options)"""

replacement = """            // Bergabung ke Channel menggunakan Token Lokal.
            val tokenService = RtcTokenService()
            val token = tokenService.generateToken(channelName, 0)
            rtcEngine?.joinChannel(token, channelName, 0, options)"""

content = content.replace(target, replacement)

# We should also replace the App ID in CallViewModel initialization if it exists.
# Let's check for config.mAppId = ...
# Actually, the App ID is passed to RtcEngine.create. Let's find it.
import re
content = re.sub(r'config\.mAppId = ".*?"', 'config.mAppId = "085ae7b69ba544a887d74c00b7e9f0d9"', content)

with open(filepath, 'w') as f:
    f.write(content)
print("Updated CallViewModel.kt")
