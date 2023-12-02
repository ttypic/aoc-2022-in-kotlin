
val nameToDigit = mapOf(
    "one" to "1",
    "two" to "2",
    "three" to "3",
    "four" to "4",
    "five" to "5",
    "six" to "6",
    "seven" to "7",
    "eight" to "8",
    "nine" to "9",
)

fun main() {

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val digits = it.filter { char -> char.isDigit() }
            "${digits.first()}${digits.last()}".toInt()
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {
            val first = "${nameToDigit.keys.joinToString(separator = "|")}|\\d".toRegex().find(it)
                ?.let { match -> if (match.value.any { char -> char.isDigit() }) match.value else nameToDigit[match.value]!! }!!
            val last =
                "${nameToDigit.keys.joinToString(separator = "|", transform = { key -> key.reversed() })}|\\d".toRegex()
                    .find(it.reversed())
                    ?.let { match -> if (match.value.any { char -> char.isDigit() }) match.value else nameToDigit[match.value.reversed()]!! }!!
            (first + last).toInt()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
   // println(part1(testInput))
    println(part2(testInput))
    //check(part1(testInput) == 142)
    check(part2(testInput) == 281)

    val input = readInput("Day01")
    //println(part1(input))
    println(part2(input))
}
