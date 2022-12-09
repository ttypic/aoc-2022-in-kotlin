fun main() {

    fun part1(input: List<String>): Int {
        val uniqueTailPoints = mutableSetOf(Point(0, 0))
        var head = Point(0, 0)
        var tail = Point(0, 0)
        input.forEach {
            val (direction, steps) = it.split(" ")
            repeat(steps.toInt()) {
                head = head.move(direction)
                tail = tail.moveTowards(head)
                uniqueTailPoints.add(tail)
            }
        }
        return uniqueTailPoints.size
    }

    fun part2(input: List<String>): Int {
        val uniqueTailPoints = mutableSetOf(Point(0, 0))
        var head = Point(0, 0)
        var tails = List(9) { Point(0, 0) }
        input.forEach {
            val (direction, steps) = it.split(" ")
            repeat(steps.toInt()) {
                head = head.move(direction)
                tails = tails.moveTowards(head)
                uniqueTailPoints.add(tails.last())
            }
        }
        return uniqueTailPoints.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 1)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}


data class Point(val x: Int, val y: Int)

fun Point.isNear(other: Point): Boolean {
    return x in (other.x - 1 .. other.x + 1) &&
    y in (other.y - 1 .. other.y + 1)
}

fun Point.move(direction: String): Point {
    return when (direction) {
        "R" -> Point(x + 1, y)
        "L" -> Point(x - 1, y)
        "U" -> Point(x, y + 1)
        "D" -> Point(x, y - 1)
        else -> throw IllegalArgumentException("Unrecognized direction $direction")
    }
}

fun Point.moveTowards(other: Point): Point {
    if (isNear(other)) return this
    val newX = if (other.x > x) x + 1 else if (other.x < x) x - 1 else x
    val newY = if (other.y > y) y + 1 else if (other.y < y) y - 1 else y
    return Point(newX, newY)
}

fun List<Point>.moveTowards(head: Point): List<Point> {
    var nextHead = head
    return map { point ->
        nextHead = point.moveTowards(nextHead)
        nextHead
    }
}
