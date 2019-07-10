package ch8

import java.util.concurrent.locks.Lock

val sum = { x: Int, y: Int -> x + y }
val action = { println(42) }

//    val sum: (Int, Int) -> Int = { x, y -> x + y }
//    val action: () -> Unit = { println(42) }

var canReturnNull: ((Int, Int) -> Int?) = {x, y -> null} // 꼭 괄호로 감싸줘야?
var funOrNull: ((Int, Int) -> Int?)? = null

fun performRequest (
    url: String,
    callback: (code: Int, content: String) -> Unit
) {
    /* ... */
}

fun String.filter(perdicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        println(element)
        if (perdicate(element)) sb.append(element)
    }

    return sb.toString()
}

fun processTheAnswer(f: (Int) -> Int) {
    println(f(42))
}

fun <T> Collection<T>.joinToString(
    separator: String = ",",
    prefix: String = "",
    postfix: String = "",
    transform: (T) -> String = { it.toString() }    // 기본값으로 toString() 사용
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(transform(element))
    }

    result.append(postfix)
    return result.toString()
}

enum class Delivery { STANDARD, EXPEDITED }

class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    }

    return { order -> 1.2 * order.itemCount }
}

data class SiteVisit(
    val path: String,
    val duration: Double,
    val os: OS
)

enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

val log = listOf(
    SiteVisit("/", 34.0, OS.WINDOWS),
    SiteVisit("/", 22.0, OS.MAC),
    SiteVisit("/login", 12.0, OS.WINDOWS),
    SiteVisit("/signup", 8.0, OS.IOS),
    SiteVisit("/", 16.3, OS.ANDROID)
)

val averageWindowsDuration = log
    .filter { it.os == OS.WINDOWS }
    .map(SiteVisit::duration)
    .average()



val averageMobileDuration = log
    .filter { it.os in setOf(OS.IOS, OS.ANDROID) }
    .map(SiteVisit::duration)
    .average()

fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) =
    filter(predicate)
        .map(SiteVisit::duration)
        .average()

fun List<SiteVisit>.averageDurationFor(os: OS) = filter { it.os == os }
    .map(SiteVisit::duration)
    .average()

inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}

fun foo(l: Lock) {
    println("Before sync")

    synchronized(l) {
        println("Action")
    }

    println("After sync")
}

class LockOwner(val lock: Lock) {
    fun runUnderLock(body: () -> Unit) {
        synchronized(lock, body) // body 는 inline 되지 않는다.
    }
}