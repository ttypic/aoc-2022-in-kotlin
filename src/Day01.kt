fun main() {
    fun part1(input: List<String>): Int {
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
        val elfCalories: Sequence<Sequence<Int>> = generateSequence(getNextElfCalories)
        return elfCalories.map { it.sum() }.max()
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24_000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
