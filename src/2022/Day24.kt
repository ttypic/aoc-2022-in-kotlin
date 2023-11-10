package `2022`

import readInput

fun main() {
    fun part1(input: List<String>): Int {
        return Maze.readMaze(input).solve()
    }

    fun part2(input: List<String>): Int {
        var maze = Maze.readMaze(input)
        val startToGoal = maze.solve()
        maze = maze.reverse()
        val backToStart = maze.solve(startToGoal)
        maze = maze.reverse()
        return maze.solve(backToStart)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day24_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 18)
    check(part2(testInput) == 54)

    val input = readInput("Day24")
    println(part1(input))
    println(part2(input))
}

fun List<Pair<Direction, Point>>.tick(wallX: Int, wallY: Int): List<Pair<Direction, Point>> {
    return map {(direction, point) ->
        direction to point.nextBlizzard(direction, wallX, wallY)
    }
}

fun Point.nextBlizzard(direction: Direction, wallX: Int, wallY: Int): Point {
    return when(direction) {
        Direction.north -> if (y == 1) Point(x, wallY - 1) else Point(x, y - 1)
        Direction.south -> if (y == wallY - 1) Point(x, 1) else Point(x, y + 1)
        Direction.west -> if (x == 1) Point(wallX - 1, y) else Point(x - 1, y)
        Direction.east -> if (x == wallX - 1) Point(1, y) else Point(x + 1, y)
    }
}

class Maze(private var blizzards: List<List<Pair<Direction, Point>>>, private val wallX: Int, private val wallY: Int, private val entry: Point, private val exit: Point, private val period: Int) {
    private var visitedPoints: MutableSet<Pair<Int, Point>> = mutableSetOf()

    fun solve(initialStep: Int = 0): Int {
        val nextToVisit = mutableListOf(initialStep to entry)

        while (nextToVisit.isNotEmpty()) {
            val (step, point) = nextToVisit.removeFirst()
            val atom = step % period

            if (point == exit) return step - 1
            if (visitedPoints.contains(atom to point)) continue

            visitedPoints.add(atom to point)
            val blizzard = blizzards[atom].groupBy { it.second }

            val nearestPoints = Direction.values().map { direction ->
                point.toDirection(direction)
            } + point

            nearestPoints.filter { !blizzard.contains(it) && (it == exit || it == entry || it.x in 1 until wallX && it.y in 1 until wallY) }.forEach {
                nextToVisit.add((step + 1) to it)
            }
        }

        error("Can't find the exit")
    }

    fun reverse(): Maze {
        return Maze(blizzards, wallX, wallY, exit, entry, period)
    }

    fun print(atom: Int) {
        val blizzard = blizzards[atom].groupBy { it.second }
        println((1 until wallY).joinToString(separator = "\n") { y ->
            (1 until  wallX).joinToString(separator = "") { x ->
                if (blizzard.contains(Point(x, y))) {
                    val points = blizzard[Point(x, y)]!!
                    if (points.size != 1) {
                        "${points.size}"
                    } else {
                        when(points.first().first) {
                            Direction.north -> "^"
                            Direction.east -> ">"
                            Direction.west -> "<"
                            Direction.south -> "v"
                        }
                    }
                } else {
                    "."
                }
            }
        })
    }

    companion object {
        fun readMaze(input: List<String>): Maze {
            fun getDirection(char: Char): Direction {
                return when(char) {
                    '^' -> Direction.north
                    '>' -> Direction.east
                    '<' -> Direction.west
                    'v' -> Direction.south
                    else -> error("Illegal state")
                }
            }

            val initialBlizzards = input.flatMapIndexed { y, line ->
                line.mapIndexedNotNull { x, char ->
                    if (char == '#' || char == '.') null else getDirection(char) to Point(x, y)
                }
            }

            val wallY = input.size - 1
            val wallX = input.first().length - 1
            val period = (wallY - 1) * (wallX - 1) / gcd(wallY - 1L, wallX - 1L).toInt()

            val blizzards = generateSequence(initialBlizzards) { it.tick(wallX, wallY) }.take(period).toList()

            return Maze(blizzards, wallX, wallY, Point(1, 0), Point(wallX - 1, wallY), period)
        }
    }
}


