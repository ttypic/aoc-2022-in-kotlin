fun main() {

    fun getElfCalories(input: List<String>): Sequence<Int> {
        return input.asSequence()
            .splitBy { it.isEmpty() }
            .map { it.map(String::toInt).sum() }
    }

    fun part1(input: List<String>): Int {
        return getElfCalories(input).max()
    }

    fun part2(input: List<String>): Int {
        return getElfCalories(input).topThree().sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24_000)
    check(part2(testInput) == 45_000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
