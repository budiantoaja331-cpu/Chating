sed -i '/\[versions\]/a firebaseCrashlytics = "3.0.2"' gradle/libs.versions.toml
sed -i '/firebase-messaging = /a firebase-crashlytics = { group = "com.google.firebase", name = "firebase-crashlytics" }' gradle/libs.versions.toml
sed -i '/google-services = /a google-firebase-crashlytics = { id = "com.google.firebase.crashlytics", version.ref = "firebaseCrashlytics" }' gradle/libs.versions.toml
