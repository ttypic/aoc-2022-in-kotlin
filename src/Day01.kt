fun main() {
    fun <T : Comparable<T>> Sequence<T>.topThree(): List<T> {
        val topThreeValues = mutableListOf<T>()
        val iterator = iterator()
        while (iterator.hasNext()) {
            val e = iterator.next()
            if (topThreeValues.size < 3) {
                topThreeValues.add(e)
                topThreeValues.sort()
            } else if (topThreeValues.first() < e) {
                topThreeValues[0] = e
                topThreeValues.sort()
            }
        }
        return topThreeValues
    }

    fun inputToElfCalories(input: List<String>): Sequence<Int> {
        val iterator = input.iterator()
        val getNextElfCalories = nextElfCalories@{
            if (!iterator.hasNext()) return@nextElfCalories null
            sequence {
                while (iterator.hasNext()) {
                    val next = iterator.next()
                    if (next.isEmpty()) return@sequence
                    yield(next.toInt())
                }
            }
        }
        return generateSequence(getNextElfCalories).map { it.sum() }
    }

    fun part1(input: List<String>): Int {
        return inputToElfCalories(input).max()
    }

    fun part2(input: List<String>): Int {
        return inputToElfCalories(input).topThree().sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24_000)
    check(part2(testInput) == 45_000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
