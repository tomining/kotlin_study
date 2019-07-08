package ch8

import org.junit.Test
import kotlin.test.junit.JUnitAsserter.assertEquals

class HighOrderFuncTest {

    private val highOrderFunc: HighOrderFunc = HighOrderFunc()

    @Test
    fun `고차함수 SUM`() {
        //When
        val result = highOrderFunc.sum(1, 2)

        //Then
        assertEquals("1 + 2 = 3", result, 3)
    }

    @Test
    fun `고차함수 Action`() {
        highOrderFunc.action()
    }
}