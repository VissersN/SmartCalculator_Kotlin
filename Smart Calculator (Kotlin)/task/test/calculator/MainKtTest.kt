package calculator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class MainKtTest {
    @Test
    fun `Using 3+ should return false`() {
        val list = listOf("1", "+", "3+")
        val result = expressionCheck(list)
        assertFalse(result)
    }
}