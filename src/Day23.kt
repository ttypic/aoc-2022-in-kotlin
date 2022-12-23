fun main() {

    fun processRound(elfPositions: Set<Point>, directions: List<Direction>): Set<Point> {
        val currentToNext: MutableMap<Point, Point> = mutableMapOf()
        val proposeToCurrent: MutableMap<Point, Point> = mutableMapOf()

        elfPositions.forEach { point ->
            val noNeedToMove = directions.all { direction -> point.directionPoints(direction).all { !elfPositions.contains(it) } }
            val direction = directions.firstOrNull { direction -> point.directionPoints(direction).all { !elfPositions.contains(it) } }
            if (direction == null || noNeedToMove) {
                proposeToCurrent[point] = point
                currentToNext[point] = point
            } else if (proposeToCurrent.contains(point.toDirection(direction))) {
                val collision = proposeToCurrent[point.toDirection(direction)]!!
                currentToNext[point] = point
                currentToNext[collision] = collision
            } else {
                currentToNext[point] = point.toDirection(direction)
                proposeToCurrent[point.toDirection(direction)] = point
            }
        }

        return currentToNext.values.toSet()
    }

    fun part1(input: List<String>): Int {
        var elfPositions = input.flatMapIndexed { y, line -> line.mapIndexedNotNull { x, char -> if (char == '#') Point(x, y) else null } }.toSet()
        val directions = Direction.values().toMutableList()

        repeat(10) {
            elfPositions = processRound(elfPositions, directions)
            directions.shift()
        }

        val maxX = elfPositions.maxOf { it.x }
        val maxY = elfPositions.maxOf { it.y }
        val minX = elfPositions.minOf { it.x }
        val minY = elfPositions.minOf { it.y }

        return (maxX - minX + 1) * (maxY - minY + 1) - elfPositions.size
    }

    fun part2(input: List<String>): Int {
        var elfPositions = input.flatMapIndexed { y, line -> line.mapIndexedNotNull { x, char -> if (char == '#') Point(x, y) else null } }.toSet()
        val directions = Direction.values().toMutableList()
        var rounds = 0
        do {
            rounds++
            val prevPositions = elfPositions
            elfPositions = processRound(elfPositions, directions)
            directions.shift()
        } while (prevPositions != elfPositions)

        return rounds
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 110)
    check(part2(testInput) == 20)

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}

enum class Direction {
    north,
    south,
    west,
    east
}

fun Point.directionPoints(direction: Direction): List<Point> {
    return when(direction) {
        Direction.north -> listOf(Point(x - 1, y - 1), Point(x, y - 1), Point(x + 1, y - 1))
        Direction.south -> listOf(Point(x - 1, y + 1), Point(x, y + 1), Point(x + 1, y + 1))
        Direction.west -> listOf(Point(x - 1, y - 1), Point(x - 1, y), Point(x - 1, y + 1))
        Direction.east -> listOf(Point(x + 1, y - 1), Point(x + 1, y), Point(x + 1, y + 1))
    }
}

fun Point.toDirection(direction: Direction): Point {
    return when(direction) {
        Direction.north -> Point(x, y - 1)
        Direction.south -> Point(x, y + 1)
        Direction.west -> Point(x - 1, y)
        Direction.east -> Point(x + 1, y)
    }
}

fun MutableList<Direction>.shift() {
    val first = removeFirst()
    add(first)
}

fun Set<Point>.print() {
    val maxX = maxOf { it.x }
    val maxY = maxOf { it.y }
    val minX = minOf { it.x }
    val minY = minOf { it.y }
    println((minY..maxY).joinToString(separator = "\n") { y ->
        (minX..maxX).joinToString(separator = "") { x ->
            if (contains(Point(x, y))) "#" else "."
        }
    })
}
