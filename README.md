# 고차 함수

## 8.1 고차 함수 정의

> 람다나 함수 참조를 인자로 넘길 수 있거나 람다나 함수 참조를 반환하는 함수

```kotlin
list.filter { x > 0 }
```

### 8.1.1 함수 타입

```kotlin
val sum = { x: Int, y: Int -> x + y }
val action = { println(42) }
```

컴파일러는 알아서 함수 타입을 추론한다.

```Kotlin
val sum: (Int, Int) -> Int = { x, y -> x + y }
val action: () -> Unit = { println(42) }
```

- Unit 이란? 의미 있는 값을 반환하지 않는 함수 반환 타입에 쓰는 특별한 타입. void 같은?
- 함수 선언시 생략이 가능하지만 함수 타입 선언시 필수

```kotlin
var canReturnNull: ((Int, Int) -> Int?)? = {x, y -> null} // 꼭 괄호로 감싸줘야?
var funOrNull: ((Int, Int) -> Int?)? = null
```

파라미터 이름은 타입 검사 시 무시된다.
```kotlin
fun performRequest (
    url: String,
    callback: (code: Int, content: String) -> Unit
) {
    /* ... */
}

val url = "https://www.naver.com"
performRequest(url) { code, content -> println(String.format("%s, %s", code, content)) }
performRequest(url) { code, page -> println(String.format("%s, %s", code, page)) }
```

### 8.1.2 인자로 받은 함수 호출

> 인자로 받은 함수를 호출하는 구문은 일반 함수를 호출하는 구문과 같다.

```kotlin
fun String.filter(perdicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        println(element)
        if (perdicate(element)) sb.append(element)
    }

    return sb.toString()
}
```

### 8.1.3 자바에서 코틀린 함수 타입 사용

> 자바8 람다를 넘기면 Kotlin 의 함수 타입의 값으로 변환
> FunctionN 인터페이스를 구현하는 클래스의 인스턴스를 저장하며, 그 클래스의 invoke 메소드 본문에는 람다의 본문이 들어간다.

```kotlin
fun processTheAnswer(f: (Int) -> Int) {
    println(f(42))
}
```

```java
processTheAnswer(number -> number + 1);

processTheAnswer(
    new Function1<Integer, Integer>() {
        @Override
        public Integer invoke(Integer number) {
            System.out.println(number);
            return number + 1;
        }
    }
);
```

반환 타입이 Unit 인 함수나 람다를 자바로 작성할 때에는 Unit 타입을 명시적으로 반환해 주어야 한다.

```java
List<String> strings = new ArrayList<>();
strings.add("42");

CollectionsKt.forEach(strings, s -> {
    System.out.println(s);
    return Unit.INSTANCE;
});
```

### 8.1.4 디폴트 값을 지정한 함수 타입 파라미터나 널이 될 수 있는 함수 타입 파라미터

```kotlin
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
```

아래처럼 활용할 수 있다.

```
val letters = listOf("Alpha", "Beta")

println(letters.joinToString())
println(letters.joinToString { it.toLowerCase() })
println(letters.joinToString(separator = "!", postfix = "!", transform = { it.toUpperCase() }))
```

### 8.1.5 함수를 함수에서 반환

```kotlin
enum class Delivery { STANDARD, EXPEDITED }

class Order(val itemCount: Int)

fun getShippingCostCalculator(delivery: Delivery): (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    }

    return { order -> 1.2 * order.itemCount }
}
```

```
>>> val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
>>> println("Shipping costs ${calculator(Order(3))}")
>>> Shipping costs 12.3
```

### 8.1.6 람다를 활용한 중복 제거

```kotlin
val averageWindowsDuration = log
    .filter { it.os == OS.WINDOWS }
    .map(SiteVisit::duration)
    .average()
>>> println(averageWindowsDuration)
23.0

fun List<SiteVisit>.averageDurationFor(os: OS) = filter { it.os == os }
    .map(SiteVisit::duration)
    .average()
>>> println(log.averageDurationFor(OS.WINDOWS))
23.0
>>> println(log.averageDurationFor(OS.MAC))
22.0

val averageMobileDuration = log
    .filter { it.os in setOf(OS.IOS, OS.ANDROID) }
    .map(SiteVisit::duration)
    .average()
>>> println(averageMobileDuration)
12.15

fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) =
    filter(predicate)
    .map(SiteVisit::duration)
    .average()
>>> println(log.averageDurationFor { it.os in setOf(OS.ANDROID, OS.IOS) })
12.15
>>> println(log.averageDurationFor { it.os === OS.IOS && it.path == "/signup" })
8.0
```

## 8.2 인라인 함수: 람다의 부가 비용 없애기

