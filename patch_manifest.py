import re

path = 'app/src/main/AndroidManifest.xml'
with open(path, 'r') as f:
    content = f.read()

target = """    </application>
</manifest>"""

replacement = """        <service
            android:name=".MyFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
    </application>
</manifest>"""

content = content.replace(target, replacement)
with open(path, 'w') as f:
    f.write(content)
