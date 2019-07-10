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

> 보통 람다를 무명 클래스로 컴파일
> inline 을 통해 무명 클래스가 아닌 바이트코드로 대체할 수 있다.

### 8.2.1 인라이닝이 작동하는 방식

> inline 으로 선언된 함수는 본문이 인라인이 된다.
> -> 함수를 호출하는 코드를 함수를 호출하는 바이트코드 대신에 함수 본문을 번역한 바이트코드로 컴파일한다.

```kotlin
inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}
```

https://javap.yawk.at/#ZUnbc8
P.366 ~ P.367 참고
```kotlin
// 람다로...
fun foo(l: Lock) {
    println("Before sync")

    synchronized(l) {
        println("Action")
    }

    println("After sync")
}

// 함수 타입으로...
class LockOwner(val lock: Lock) {
    fun runUnderLock(body: () -> Unit) {
        synchronized(lock, body)
    }
}
```

inline 되는 영역에 차이가 있다.

### 8.2.2 인라인 함수의 한계

> "Illegal usage of inline-parameter" 오류
> inline 람다에서 저장한 변수를 외부에서 활용할 때

```kotlin
fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {
    return TransformingSequence(this, transform)
}
```

여기서 사용되는 transform 은 inline 람다로 전달할 수 없다.
noinline 키워드를 사용하면 인라이닝을 금지할 수 있다.
서드파티 라이브러리 안에 인라인 함수를 정의하고 외부에서 사용하거나 자바에서 코틀린 인라인 함수를 사용하는 경우 일반 함수로 컴파일 한다.

### 8.2.3 컬렉션 연산 인라이닝

```kotlin
data class Person(val name: String, val age: Int)

val people = listOf(Person("Alice", 29), Person("Bob", 30))
```

```kotlin
>>> println(people.filter { it.age < 30 })
[Person(name=Alice, age=29)]

>>> val result = mutableListOf<Person>()
    for (person in people) {
        if (person.age < 30) result.add(person)
    }
    println(result)
[Person(name=Alice, age=29)]
```

Kotlin 의 filter 함수는 inline
```
public inline fun <T> Iterable<T>.filter(predicate: (T) -> Boolean): List<T> {
    return filterTo(ArrayList<T>(), predicate)
}
```

asSequence 를 활용할 수도 있다. [nwtech/share](https://oss.navercorp.com/nwtech/share/issues/268)

### 8.2.4 함수를 인라인으로 선언해야 하는 경우

그럼 무작정 inline 을 사용해야 할까? **아니다**
- 일반함수
  - JVM 이 알아서 인라이닝을 지원
  - 코드 중복
  - 그냥 호출하는게 스택 트레이스도 깔끔
- 람다 파라미터 함수
  - 함수 호출 비용 감소
  - 무명 클래스 객체 생성 비용 감소
  - JVM 의 자동 인라이닝 기능은 완벽하지 않다.
  - 람다에서 지원하지 않는 몇 가지 기능 사용 가능 (non-local)

inline 함수는 최대한 작은 코드에 사용하길 권장. (Kotlin 표준 라이브러리의 inline 은 대부분 짧음)

### 8.2.5 자원 관리를 위해 인라인된 람다 사용

```java
static String readFirstLineFromFile(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLine();
    }
}
```

```kotlin
fun readFirstLineFromFile(path: String): String {
    BufferedReader(FileReader(path)).use { br ->
        return br.readLine() // non-local
    }
}
```

use 함수는 Closable 자원에 대한 확장함수. try-with-resource(JDK 7 이상) 와 동일

## 8.3 고차 함수 안에서 흐름 제어

### 8.3.1 람다 안의 return 문: 람다를 둘러싼 함수로부터 반환

```kotlin
fun lookForAlice(people: List<Person>) {
    for (person in people) {
        if (person.name == "Alice") {
            println("Found!")
            return
        }
    }

    println("Alice is not found")
}

fun lookForAlice2(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") {
            println("Found!")
            return  //lookForAlice2 에서 반환
        }
    }

    println("Alice is not found")
}
```

non-local return 은 람다를 인자로 받은 함수가 인라인 함수인 경우만 가능
```kotlin
@kotlin.internal.HidesMembers
public inline fun <T> Iterable<T>.forEach(action: (T) -> Unit): Unit {
    for (element in this) action(element)
}
```

### 8.3.2 람다로부터 반환: 레이블을 사용한 return

```kotlin
fun lookForAlice3(people: List<Person>) {
    people.forEach lable@{
        if (it.name == "Alice") return@lable
    }

    println("Alice must be somewhere")
}

fun lookForAlice4(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") return@forEach
    }

    println("Alice must be somewhere")
}
```

### 8.3.3 무명 함수: 기본적으로 로컬 return

```kotlin
fun lookForAlice5(people: List<Person>) {
    people.forEach(fun(person) {
        if (person.name == "Alice") return
        println("${person.name} is not Alice")
    })
}
```

무명 함수도 일반 함수와 같이 반환 타입 지정을 해야 하지만 식을 본문으로 하는 무명 함수의 반환 타입은 생략할 수 있다.

```kotlin
people.filter(fun(person): Boolean {
    return person.age < 30
})

people.filter(fun(person) = person.age < 30)
```

무명 함수는 일반 함수와 유사해 보이지만 실제로는 람다 식에 대한 문법적 편의일 뿐이다. (람다식과 유사하게 동작)

```kotlin
fun lookForAlice4(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") return // lable 이 없는 경우 fun lookForAlice4 가 종료
    }

    println("Alice must be somewhere")
}
```