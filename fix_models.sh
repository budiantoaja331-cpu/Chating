for file in app/src/main/java/com/example/StoryViewModel.kt app/src/main/java/com/example/ActiveCallManager.kt app/src/main/java/com/example/CallRecord.kt app/src/main/java/com/example/ChatModels.kt app/src/main/java/com/example/UserManager.kt; do
  sed -i '/import androidx.annotation.Keep/d' $file
  sed -i '/package com.example/a \import androidx.annotation.Keep' $file
done
