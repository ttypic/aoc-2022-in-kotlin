package `2022`

import readInput

fun main() {

    fun parseInput(input: List<String>): Day12Input {
        val field = input.map { it.asIterable().toMutableList() }

        var startPoint = Point(0, 0)
        var endPoint = Point(0, 0)

        field.forEachIndexed { i,line ->
            line.forEachIndexed { j, char ->
                when (char) {
                    'S' -> {
                        startPoint = Point(i, j)
                        field[i][j] = 'a'
                    }
                    'E' -> {
                        endPoint = Point(i, j)
                        field[i][j] = 'z'
                    }
                }
            }
        }

        return Day12Input(field, startPoint, endPoint)
    }

    fun buildWeights(input: Day12Input): Map<Point, Int> {
        fun List<List<Char>>.canReach(currentPoint: Point, nextPoint: Point): Boolean {
            val m = size
            val n = first().size
            if (currentPoint.x !in 0 until m || currentPoint.y !in 0 until n) return false
            if (nextPoint.x !in 0 until m || nextPoint.y !in 0 until n) return false
            return this[nextPoint.x][nextPoint.y] - this[currentPoint.x][currentPoint.y] <= 1
        }
        val (field, _, endPoint) = input
        val weights = mutableMapOf(endPoint to 0)
        var step = 0
        do {
            val currentPoints = weights.filter { (_, weight) -> weight == step }.keys
            step++
            currentPoints.forEach { point ->
                point.nearestPoints().forEach {
                    if (field.canReach(it, point) && !weights.contains(it)) {
                        weights[it] = step
                    }
                }
            }
        } while (currentPoints.isNotEmpty())
        return weights
    }

    fun part1(input: List<String>): Int {
        val parsedInput = parseInput(input)
        val weights = buildWeights(parsedInput)
        return weights[parsedInput.startPoint]!!
    }

    fun part2(input: List<String>): Int {
        val parsedInput = parseInput(input)
        val weights = buildWeights(parsedInput)
        val (field) = parsedInput

        val possibleStarts = field.flatMapIndexed { i,line ->
            line.mapIndexedNotNull { j, char ->
                if (char == 'a') Point(i, j) else null
            }
        }

        return possibleStarts.mapNotNull { weights[it] }.min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day12_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 31)
    check(part2(testInput) == 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

data class Day12Input(val field: List<List<Char>>, val startPoint: Point, val endPoint: Point)

fun Point.nearestPoints(): List<Point> {
    return listOf(Point(x - 1, y), Point(x, y - 1), Point(x + 1, y), Point(x, y + 1))
}
