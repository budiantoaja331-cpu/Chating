sed -i '1i\import androidx.annotation.Keep' app/src/main/java/com/example/StoryViewModel.kt
sed -i 's/data class Story/@Keep\ndata class Story/' app/src/main/java/com/example/StoryViewModel.kt

sed -i '1i\import androidx.annotation.Keep' app/src/main/java/com/example/ActiveCallManager.kt
sed -i 's/data class ActiveCall/@Keep\ndata class ActiveCall/' app/src/main/java/com/example/ActiveCallManager.kt

sed -i '1i\import androidx.annotation.Keep' app/src/main/java/com/example/CallRecord.kt
sed -i 's/data class CallRecord/@Keep\ndata class CallRecord/' app/src/main/java/com/example/CallRecord.kt

sed -i '1i\import androidx.annotation.Keep' app/src/main/java/com/example/ChatModels.kt
sed -i 's/data class ChatMessage/@Keep\ndata class ChatMessage/' app/src/main/java/com/example/ChatModels.kt

sed -i '1i\import androidx.annotation.Keep' app/src/main/java/com/example/UserManager.kt
sed -i 's/data class User/@Keep\ndata class User/' app/src/main/java/com/example/UserManager.kt
