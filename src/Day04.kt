fun main() {

    fun String.toRange(): ClosedRange<Int> {
        val (start, end) = this.split("-").map(String::toInt)
        return start..end
    }

    fun ClosedRange<Int>.containedIn(other: ClosedRange<Int>) =
        endInclusive >= other.endInclusive && start <= other.start

    fun ClosedRange<Int>.overlaps(other: ClosedRange<Int>) =
        endInclusive in other || other.endInclusive in this

    fun part1(input: List<String>): Int {
        return input.filter {
            val (firstRange, secondRange) = it.split(",").map(String::toRange)
            firstRange.containedIn(secondRange) || secondRange.containedIn(firstRange)
        }.size
    }

    fun part2(input: List<String>): Int {
        return input.filter {
            val (firstRange, secondRange) = it.split(",").map(String::toRange)
            firstRange.overlaps(secondRange)
        }.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
