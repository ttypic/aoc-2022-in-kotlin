package `2022`

import readInput
import splitBy

fun main() {

    fun part1(input: List<String>): Int {
        val (map, directionsLines) = input.splitBy { it.isEmpty() }
        val directions = "\\d+|R|L".toRegex().findAll(directionsLines.first()).map { it.value }.toList()
        var positionColumn = map.first().indexOfFirst { it == '.' }
        var positionRow = 0
        var degree = 0
        directions.forEach {
            when(it) {
                "R" -> degree = if (degree + 90 > 180) degree - 270 else degree + 90
                "L" -> degree = if (degree - 90 <= -180) degree + 270 else degree - 90
                else -> {
                    val steps = it.toInt()
                    when(degree) {
                        0 -> {
                            repeat(steps) {
                                val row = map[positionRow]
                                var nextPosition = positionColumn + 1
                                if (nextPosition >= row.length || row[nextPosition] == ' ') {
                                    nextPosition = row.indexOfFirst { char -> char != ' ' }
                                }
                                if (row[nextPosition] == '#') return@repeat
                                positionColumn = nextPosition
                            }

                        }
                        90 -> {
                            repeat(steps) {
                                var nextPosition = positionRow + 1
                                if (nextPosition >= map.size || positionColumn >= map[nextPosition].length || map[nextPosition][positionColumn] == ' ') {
                                    nextPosition = map.indexOfFirst { row -> positionColumn < row.length && row[positionColumn] != ' ' }
                                }
                                if (map[nextPosition][positionColumn] == '#') return@repeat
                                positionRow = nextPosition
                            }
                        }
                        -90 -> {
                            repeat(steps) {
                                var nextPosition = positionRow - 1
                                if (nextPosition < 0 || positionColumn >= map[nextPosition].length || map[nextPosition][positionColumn] == ' ') {
                                    nextPosition = map.indexOfLast { row -> positionColumn < row.length && row[positionColumn] != ' ' }
                                }
                                if (map[nextPosition][positionColumn] == '#') return@repeat
                                positionRow = nextPosition
                            }
                        }
                        180 -> {
                            repeat(steps) {
                                val row = map[positionRow]
                                var nextPosition = positionColumn - 1
                                if (nextPosition < 0 || row[nextPosition] == ' ') {
                                    nextPosition = row.indexOfLast { char -> char != ' ' }
                                }
                                if (row[nextPosition] == '#') return@repeat
                                positionColumn = nextPosition
                            }
                        }
                        else -> error("illegal state")
                    }
                }
            }
        }
        val facing = when(degree) {
            0 -> 0
            90 -> 1
            180 -> 2
            -90 -> 3
            else -> error("illegal")
        }
        return (positionRow + 1) * 1000 + 4 * (positionColumn + 1) + facing
    }

    fun nextPosition(map: List<String>, positionRow: Int, positionColumn: Int, degree: Int): Triple<Int, Int, Int> {
        return when (degree) {
            0 -> {
                var nextPositionRow = positionRow
                var nextPositionColumn = positionColumn + 1
                var nextDegree = degree
                if (nextPositionColumn >= map[nextPositionRow].length || map[nextPositionRow][nextPositionColumn] == ' ') {
                    if (positionRow in 0 until 50) {
                        nextDegree = 180
                        nextPositionColumn = 99
                        nextPositionRow = 150 - positionRow
                    }
                    if (positionRow in 50 until 100) {
                        nextDegree = -90
                        nextPositionColumn = positionRow + 50
                        nextPositionRow = 49
                    }
                    if (positionRow in 100 until 150) {
                        nextDegree = 180
                        nextPositionColumn = 149
                        nextPositionRow = 150 - positionRow
                    }
                    if (positionRow in 150 until 200) {
                        check(nextPositionColumn == 50)
                        nextDegree = -90
                        nextPositionColumn = positionRow - 100
                        nextPositionRow = 149
                    }
                }
                if (map[nextPositionRow][nextPositionColumn] == '#') {
                    Triple(positionRow, positionColumn, degree)
                } else {
                    Triple(nextPositionRow, nextPositionColumn, nextDegree)
                }
            }

            90 -> {
                var nextPositionRow = positionRow + 1
                var nextPositionColumn = positionColumn
                var nextDegree = degree
                if (nextPositionRow >= map.size || positionColumn >= map[nextPositionRow].length  || map[nextPositionRow][nextPositionColumn] == ' ') {
                    if (positionColumn in 0 until 50) {
                        check(nextPositionRow == 200)
                        nextDegree = 90
                        nextPositionColumn = positionColumn + 100
                        nextPositionRow = 0
                    }
                    if (positionColumn in 50 until 100) {
                        check(nextPositionRow == 150)
                        nextDegree = 180
                        nextPositionColumn = 49
                        nextPositionRow = positionColumn + 100
                    }
                    if (positionColumn in 100 until 150) {
                        check(nextPositionRow == 50)
                        nextDegree = 180
                        nextPositionColumn = 99
                        nextPositionRow = positionColumn - 50
                    }
                }
                if (map[nextPositionRow][nextPositionColumn] == '#') {
                    Triple(positionRow, positionColumn, degree)
                } else {
                    Triple(nextPositionRow, nextPositionColumn, nextDegree)
                }
            }

            -90 -> {
                var nextPositionRow = positionRow - 1
                var nextPositionColumn = positionColumn
                var nextDegree = degree
                if (nextPositionRow < 0 || map[nextPositionRow][nextPositionColumn] == ' ') {
                    if (positionColumn in 0 until 50) {
                        check(positionRow == 100)
                        nextDegree = 0
                        nextPositionColumn = 50
                        nextPositionRow = positionColumn + 50
                    }
                    if (positionColumn in 50 until 100) {
                        check(positionRow == 0)
                        nextDegree = 0
                        nextPositionColumn = 0
                        nextPositionRow = positionColumn + 99
                    }
                    if (positionColumn in 100 until 150) {
                        check(positionRow == 0)
                        nextDegree = -90
                        nextPositionColumn = positionColumn - 100
                        nextPositionRow = 199
                    }
                }
                if (map[nextPositionRow][nextPositionColumn] == '#') {
                    Triple(positionRow, positionColumn, degree)
                } else {
                    Triple(nextPositionRow, nextPositionColumn, nextDegree)
                }
            }

            180 -> {
                var nextPositionRow = positionRow
                var nextPositionColumn = positionColumn - 1
                var nextDegree = degree
                if (nextPositionColumn < 0 || map[nextPositionRow][nextPositionColumn] == ' ') {
                    if (positionRow in 0 until 50) {
                        nextDegree = 0
                        nextPositionColumn = 0
                        nextPositionRow = 150 - positionRow
                    }
                    if (positionRow in 50 until 100) {
                        nextDegree = 90
                        nextPositionColumn = nextPositionRow - 50
                        nextPositionRow = 100
                    }
                    if (positionRow in 100 until 150) {
                        nextDegree = 0
                        nextPositionColumn = 50
                        nextPositionRow = 150 - positionRow
                    }
                    if (positionRow in 150 until 200) {
                        nextDegree = 90
                        nextPositionColumn = nextPositionRow - 100
                        nextPositionRow = 0
                    }
                }
                if (map[nextPositionRow][nextPositionColumn] == '#') {
                    Triple(positionRow, positionColumn, degree)
                } else {
                    Triple(nextPositionRow, nextPositionColumn, nextDegree)
                }
            }

            else -> error("illegal state")
        }
    }

    fun part2(input: List<String>): Int {
        val (map, directionsLines) = input.splitBy { it.isEmpty() }
        val directions = "\\d+|R|L".toRegex().findAll(directionsLines.first()).map { it.value }.toList()
        var positionRow = 0
        var positionColumn = map.first().indexOfFirst { it == '.' }
        var degree = 0

        directions.forEach {
            when(it) {
                "R" -> degree = if (degree + 90 > 180) degree - 270 else degree + 90
                "L" -> degree = if (degree - 90 <= -180) degree + 270 else degree - 90
                else -> {
                    val steps = it.toInt()
                    repeat(steps) {
                        val (nextPositionRow, nextPositionColumn, nextDegree) = nextPosition(map, positionRow, positionColumn, degree)
                        positionRow = nextPositionRow
                        positionColumn = nextPositionColumn
                        degree = nextDegree
                    }
                }
            }
        }
        val facing = when(degree) {
            0 -> 0
            90 -> 1
            180 -> 2
            -90 -> 3
            else -> error("illegal")
        }
        return (positionRow + 1) * 1000 + 4 * (positionColumn + 1) + facing
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    println(part1(testInput))
    //println(part2(testInput))
    check(part1(testInput) == 6032)
    //check(part2(testInput) == 5031)

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}
