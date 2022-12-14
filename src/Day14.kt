fun main() {
    val startY = 0
    val startX = 500

    fun simulateSandFall(filledPoints: Set<Point>, maxY: Int): Point? {
        var x = startX
        var lastSandPoint = Point(0, 500)
        (startY..maxY).forEach { y ->
            listOf(x, x - 1, x + 1).find {
                !filledPoints.contains(Point(it, y))
            }?.let {
                x = it
                lastSandPoint = Point(x, y)
            } ?: return lastSandPoint
        }
        return null
    }

    fun simulateSandFallWithFloor(filledPoints: Set<Point>, floorY: Int): Point {
        var x = startX
        var lastSandPoint = Point(startX, startY)
        (startY until floorY).forEach { y ->
            listOf(x, x - 1, x + 1).find {
                !filledPoints.contains(Point(it, y))
            }?.let {
                x = it
                lastSandPoint = Point(x, y)
            } ?: return lastSandPoint
        }
        return lastSandPoint
    }

    fun readRocks(input: List<String>): Set<Point> {
        val rocks = input.flatMap { path ->
            path
                .split("->")
                .map {
                    val (x, y) = it.trim().split(",").map(String::toInt)
                    Point(x, y)
                }
                .windowed(2)
                .flatMap { (first, second) -> first.straightLine(second) }
        }.toSet()
        return rocks
    }

    fun printMap(rocks: Set<Point>, filledPoints: Set<Point>) {
        val maxY = filledPoints.map { it.y }.max()
        val maxX = filledPoints.map { it.x }.max()
        val minX = filledPoints.map { it.x }.min()
        println((0..maxY).joinToString(separator = "\n") { y ->
            (minX..maxX).joinToString(separator = "") { x ->
                if (rocks.contains(Point(x, y))) {
                    "#"
                } else if (filledPoints.contains(Point(x, y))) {
                    "O"
                } else {
                    "."
                }
            }
        })
    }

    fun part1(input: List<String>, debug: Boolean = false): Int {
        val rocks = readRocks(input)
        val maxY = rocks.map { it.y }.max()

        val filledPoints = rocks.toMutableSet()

        do {
            val nextSandDrop = simulateSandFall(filledPoints, maxY)
            nextSandDrop?.let { filledPoints.add(it) }
        } while (nextSandDrop != null)

        if (debug) {
            printMap(rocks, filledPoints)
        }

        return filledPoints.size - rocks.size
    }

    fun part2(input: List<String>, debug: Boolean = false): Int {
        val rocks = readRocks(input)
        val floorY = rocks.map { it.y }.max() + 2

        val filledPoints = rocks.toMutableSet()

        do {
            val nextSandDrop = simulateSandFallWithFloor(filledPoints, floorY)
            filledPoints.add(nextSandDrop)
        } while (nextSandDrop != Point(startX, startY))

        if (debug) {
            printMap(rocks, filledPoints)
        }

        return filledPoints.size - rocks.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    println(part1(testInput, true))
    println(part2(testInput, true))
    check(part1(testInput) == 24)
    check(part2(testInput) == 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

private fun Point.straightLine(to: Point): List<Point> {
    if (x == to.x) {
        return (Math.min(y, to.y)..Math.max(y, to.y)).map { Point(x, it) }
    }
    if (y == to.y) {
        return (Math.min(x, to.x)..Math.max(x, to.x)).map { Point(it, y) }
    }

    error("Not a straight line")
}
