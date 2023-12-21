fun main() {

    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val card = line.substringAfter(":").trim()
            val (winning, numbers) = card.split('|').map { it.trim().split("\\s+".toRegex()).map(String::toInt).toSet() }
            val intersections = numbers.intersect(winning).size
            if (intersections == 0) 0 else 1 shl  (intersections - 1)
        }
    }

    fun part2(input: List<String>): Int {
        val cardNumberToCopies = mutableMapOf<Int, Int>()
        return input.mapIndexed { index, line ->
            val copies = cardNumberToCopies[index] ?: 1
            val card = line.substringAfter(":").trim()
            val (winning, numbers) = card.split('|').map { it.trim().split("\\s+".toRegex()).map(String::toInt).toSet() }
            val intersections = numbers.intersect(winning).size
            (index + 1..index + intersections).forEach { cardNumber ->
                cardNumberToCopies[cardNumber] = (cardNumberToCopies[cardNumber] ?: 1) + copies
            }
            copies
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}