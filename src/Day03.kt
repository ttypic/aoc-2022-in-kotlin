fun main() {

    fun part1(input: List<String>): Long {
        return input.mapIndexed { i, line ->
            var observed = ""
            var partNumber = false
            var sum = 0L
            line.forEachIndexed { j, char ->
                if (char.isDigit()) {
                    observed += char
                    partNumber = partNumber || input.isAdjacentToSymbol(i, j)
                } else {
                    if (partNumber) sum += observed.toLong()
                    observed = ""
                    partNumber = false
                }
            }
            if (partNumber) sum + observed.toLong() else sum
        }.sum()
    }

    fun part2(input: List<String>): Long {
        val partNumberPositions = input.flatMapIndexed { i, line ->
            var observed = ""
            var partNumber = false
            val positions = mutableListOf<EngineNumber>()
            line.forEachIndexed { j, char ->
                if (char.isDigit()) {
                    observed += char
                    partNumber = partNumber || input.isAdjacentToSymbol(i, j)
                } else {
                    if (partNumber) positions.add(
                        EngineNumber(observed.toLong(), i, (j - observed.length) until j)
                    )
                    observed = ""
                    partNumber = false
                }
            }
            val columns = line.length
            if (partNumber) positions.add(EngineNumber(observed.toLong(), i, (columns - observed.length) until columns))
            positions
        }

        return input.mapIndexed { i, line ->
            line.mapIndexed secondIndexed@{ j, char ->
                if (char != '*') return@secondIndexed 0L
                val components = partNumberPositions.filter { it.row in (i - 1 .. i + 1) && it.columns.intersects(j - 1..j + 1) }
                if (components.size == 2) {
                    val (first, second) = components
                    first.value * second.value
                } else {
                    0L
                }
            }.sum()
        }.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 4361L)
    check(part2(testInput) == 467835L)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

private fun List<String>.isAdjacentToSymbol(i: Int, j: Int): Boolean {
    val adjacentPositions = listOf(
        i - 1 to j - 1,
        i - 1 to j,
        i - 1 to j + 1,
        i to j - 1,
        i to j + 1,
        i + 1 to j - 1,
        i + 1 to j,
        i + 1 to j + 1,
    ).filter { (x, y) -> x >= 0 && y >= 0 && x < size && y < get(x).length }
    return adjacentPositions.any { (x, y) -> get(x)[y].let { !it.isDigit() && it != '.' } }
}

private data class EngineNumber(val value: Long, val row: Int, val columns: ClosedRange<Int>)
private fun ClosedRange<Int>.intersects(other: ClosedRange<Int>): Boolean {
    return start in other || other.start in this
}