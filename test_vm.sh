cat << 'INNER_EOF' > app/src/test/java/com/example/ViewModelTest.kt
package com.example

import org.junit.Test
import androidx.lifecycle.ViewModelProvider

class ViewModelTest {
    @Test
    fun testVm() {
        val vmClass = StoryViewModel::class.java
        val instance = vmClass.newInstance()
        println(instance)
    }
}
INNER_EOF
gradle testDebugUnitTest --tests "com.example.ViewModelTest"
