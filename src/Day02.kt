fun main() {

    fun part1(input: List<String>): Int {
        return input.mapIndexed { index, game ->
            val reveals = game.split(";").map {
                val reds = "(\\d+) red".toRegex().find(it)?.groupValues?.get(1)?.toInt() ?: 0
                val greens = "(\\d+) green".toRegex().find(it)?.groupValues?.get(1)?.toInt() ?: 0
                val blues = "(\\d+) blue".toRegex().find(it)?.groupValues?.get(1)?.toInt() ?: 0
                Reveal(reds, greens, blues)
            }
            val reds = reveals.maxOf { it.red }
            val greens = reveals.maxOf { it.green }
            val blues = reveals.maxOf { it.blue }
            if (reds <= 12 && greens <= 13 && blues <= 14) index + 1 else 0
        }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.mapIndexed { index, game ->
            val reveals = game.split(";").map {
                val reds = "(\\d+) red".toRegex().find(it)?.groupValues?.get(1)?.toInt() ?: 0
                val greens = "(\\d+) green".toRegex().find(it)?.groupValues?.get(1)?.toInt() ?: 0
                val blues = "(\\d+) blue".toRegex().find(it)?.groupValues?.get(1)?.toInt() ?: 0
                Reveal(reds, greens, blues)
            }
            val reds = reveals.maxOf { it.red }
            val greens = reveals.maxOf { it.green }
            val blues = reveals.maxOf { it.blue }
            reds * greens * blues
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

data class Reveal(val red: Int, val green: Int, val blue: Int)
