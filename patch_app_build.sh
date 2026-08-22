sed -i '/alias(libs.plugins.google.services)/a \  alias(libs.plugins.google.firebase.crashlytics)' app/build.gradle.kts
sed -i '/implementation(libs.firebase.appcheck.playintegrity)/a \  implementation(libs.firebase.crashlytics)' app/build.gradle.kts
