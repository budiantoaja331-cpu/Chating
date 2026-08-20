cat << 'INNER_EOF' > app/src/test/java/com/example/FirestoreSerializationTest.kt
package com.example

import org.junit.Test
import com.google.firebase.firestore.Exclude
import java.lang.reflect.Modifier

class FirestoreSerializationTest {
    @Test
    fun testProps() {
        val storyClass = Story::class.java
        for (method in storyClass.methods) {
            if (method.name.startsWith("get")) {
                println(method.name)
            }
        }
    }
}
INNER_EOF
gradle testDebugUnitTest --tests "com.example.FirestoreSerializationTest"
