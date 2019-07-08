## 8. 고차 함수

### 8.1 고차 함수 정의

> 람다나 함수 참조를 인자로 넘길 수 있거나 람다나 함수 참조를 반환하는 함수

```kotlin
list.filter { x > 0 }
```

#### 8.1.1 함수 타입

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