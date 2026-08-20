cat << 'INNER_EOF' > app/src/test/java/com/example/FirestoreSerializationTest.kt
package com.example

import org.junit.Test
import org.junit.Assert.assertEquals
import com.google.firebase.firestore.Exclude
import java.lang.reflect.Modifier

class FirestoreSerializationTest {
    @Test
    fun testStory() {
        val s = Story()
        println(s)
    }
}
INNER_EOF
gradle testDebugUnitTest --tests "com.example.FirestoreSerializationTest"
