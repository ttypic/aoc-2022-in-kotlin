fun main() {
    fun part1(input: List<String>): String {
        return input.sumOf { it.fromSNAFU() }.toSNAFU()
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == "2=-1=0")
    check(part2(testInput) == 0)

    val input = readInput("Day25")
    println(part1(input))
    println(part2(input))
}

fun String.fromSNAFU(): Long {
    return this.fold(0L) { acc, c ->
        when(c) {
            '=' -> acc * 5 - 2
            '-' -> acc * 5 - 1
            else -> acc * 5 + c.toString().toInt()
        }
    }
}

fun Long.toSNAFU(): String {
    val chars = mutableListOf<String>()

    var number = this
    do {
        when(val digit = number % 5) {
            in 0L..2L -> {
                chars.add(digit.toString())
                number /= 5
            }
            3L -> {
                chars.add("=")
                number = number / 5 + 1
            }
            4L -> {
                chars.add("-")
                number = number / 5 + 1
            }
            else -> error("Something wrong. Probably negative input")
        }
    } while (number != 0L)

    return chars.reversed().joinToString(separator = "")
}
