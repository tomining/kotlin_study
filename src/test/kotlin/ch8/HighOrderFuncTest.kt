package ch8

import org.junit.Test
import kotlin.test.junit.JUnitAsserter.assertEquals

class HighOrderFuncTest {

    @Test
    fun `고차함수 SUM`() {
        //When
        val result = sum(1, 2)

        //Then
        assertEquals("1 + 2 = 3", result, 3)
    }

    @Test
    fun `고차함수 Action`() {
        action()
    }

    @Test
    fun `파라미터 이름은 타입검사시 무시된다`() {
        val url = "https://www.naver.com"
        performRequest(url) {
                code, content -> println(String.format("%s, %s", code, content))
        }

        performRequest(url) {
                code, page -> println(String.format("%s, %s", code, page))
        }
    }

    @Test
    fun `filter 함수를 단순하게 구현하기`() {
        println("ab1c".filter { it in 'a'..'z' })
    }

    @Test
    fun `함수 타입의 파라미터에 대한 기본값 지정하기`() {
        val letters = listOf("Alpha", "Beta")

        println(letters.joinToString())
        println(letters.joinToString { it.toLowerCase() })
        println(letters.joinToString(separator = "!", postfix = "!", transform = { it.toUpperCase() }))
    }

    @Test
    fun `함수를 반환하는 함수 정의하기`() {
        val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
        println("Shiping costs ${calculator(Order(3))}")
    }

    @Test
    fun `람다를 활용한 중복 제거`() {
        println(averageWindowsDuration)
        println(log.averageDurationFor(OS.WINDOWS))
        println(log.averageDurationFor(OS.MAC))
        println(averageMobileDuration)
        println(log.averageDurationFor { it.os in setOf(OS.ANDROID, OS.IOS) })
        println(log.averageDurationFor { it.os === OS.IOS && it.path == "/signup" })
    }
}