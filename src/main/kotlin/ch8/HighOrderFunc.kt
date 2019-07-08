package ch8

class HighOrderFunc {
    val sum = { x: Int, y: Int -> x + y }
    val action = { println(42) }

//    val sum: (Int, Int) -> Int = { x, y -> x + y }
//    val action: () -> Unit = { println(42) }

    var canReturnNull: ((Int, Int) -> Int?) = {x, y -> null} // 꼭 괄호로 감싸줘야?
    var funOrNull: ((Int, Int) -> Int?)? = null
}