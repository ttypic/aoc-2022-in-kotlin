import kotlin.experimental.and

fun main() {

    fun printField(field: List<Int>) {
        println(field.reversed().joinToString(separator = "\n") {
            it.toString(2)
                .padStart(7, '0')
                .replace('0', '.')
                .replace('1', '#')
        })
    }


    fun part1(input: List<String>): Int {
        val directions = input.first()
        var topY = -1
        val field = mutableListOf<Int>()

        var time = 0

        (0 until 2022).forEach { rockIndex ->
            val rock = Rock.values()[rockIndex % 5]
            while (field.size <= topY + 9) {
                field.add(0)
            }
            var left = 2
            var bottom = topY + 4
            var currentTop: Int
            do {
                val direction = directions[time % directions.length]
                left = rock.move(direction, field, left, bottom)
                time++
                val canFall = rock.canFall(field, left, bottom - 1)
                currentTop = bottom + rock.height - 1
                bottom--
            } while (canFall)
            rock.settle(field, left, bottom + 1)
            if (currentTop > topY) topY = currentTop
        }

        return topY + 1
    }

    fun part2(input: List<String>): Long {
        val directions = input.first()
        var topY = -1
        val field = mutableListOf<Int>()

        var time = 0

        val statToIndex = mutableMapOf<PossibleCycle, Int>()
        val indexToTop = mutableMapOf<Int, Int>()
        lateinit var cycle: Pair<Int, Int>

        for(rockIndex in 0 until 1000000000000) {
            val rock = Rock.values()[(rockIndex % 5).toInt()]
            while (field.size <= topY + 9) {
                field.add(0)
            }
            var left = 2
            var bottom = topY + 4
            var currentTop: Int
            do {
                val direction = directions[time % directions.length]
                left = rock.move(direction, field, left, bottom)
                time++
                val canFall = rock.canFall(field, left, bottom - 1)
                currentTop = bottom + rock.height - 1
                bottom--
            } while (canFall)
            rock.settle(field, left, bottom + 1)
            if (currentTop > topY) topY = currentTop
            indexToTop[rockIndex.toInt()] = topY
            if (currentTop > directions.length) {
                val state = PossibleCycle(field.subList(currentTop - directions.length, currentTop).toList(), time % directions.length, rock)
                val prevIndex = statToIndex[state]
                if (prevIndex != null) {
                    cycle = prevIndex to rockIndex.toInt()
                    break
                } else {
                    statToIndex[state] = rockIndex.toInt()
                }
            }
        }

        val inCycleRocks = 1000000000000 - cycle.first - 1
        val cycleLength = cycle.second - cycle.first
        val cycleHeight = topY - indexToTop[cycle.first]!!
        val inCycleHeight = (inCycleRocks / cycleLength) * cycleHeight
        val restHeight = indexToTop[(inCycleRocks % cycleLength).toInt() + cycle.first]!!
        return inCycleHeight + restHeight + 1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 3068)
    check(part2(testInput) == 1514285714288)

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}

@Suppress("EnumEntryName")
enum class Rock(val width: Int, val height: Int) {
    first(4, 1),
    second(3, 3),
    third(3, 3),
    fourth(1, 4),
    fifth(2, 2),
}

fun Rock.canFall(field: List<Int>, left: Int, bottom: Int): Boolean {
    if (bottom < 0) return false
    return when(this) {
        Rock.first -> field[bottom][left] == 0 && field[bottom][left + 1] == 0 && field[bottom][left + 2] == 0 && field[bottom][left + 3] == 0
        Rock.second -> field[bottom + 1][left] == 0 && field[bottom + 1][left + 1] == 0 && field[bottom + 1][left + 2] == 0 && field[bottom][left + 1] == 0 && field[bottom + 2][left + 1] == 0
        Rock.third -> field[bottom][left] == 0 && field[bottom][left + 1] == 0 && field[bottom][left + 2] == 0 && field[bottom + 1][left + 2] == 0 && field[bottom + 2][left + 2] == 0
        Rock.fourth -> field[bottom][left] == 0 && field[bottom + 1][left] == 0 && field[bottom + 2][left] == 0 && field[bottom + 3][left] == 0
        Rock.fifth -> field[bottom][left] == 0 && field[bottom][left + 1] == 0 && field[bottom + 1][left] == 0 && field[bottom + 1][left + 1] == 0
    }
}

fun Rock.move(direction: Char, field: List<Int>, left: Int, bottom: Int): Int {
    return when(direction) {
        '<' -> if (left >= 1 && canFall(field, left - 1, bottom)) left - 1 else left
        '>' -> if (left + width < 7 && canFall(field, left + 1, bottom)) left + 1 else left
        else -> error("Should be < or >")
    }
}

fun Rock.settle(field: MutableList<Int>, left: Int, bottom: Int) {
    when(this) {
        Rock.first -> {
            field[bottom] += "1111".toInt(2) shl (3 - left)
        }
        Rock.second -> {
            field[bottom + 2] += "010".toInt(2) shl (4 - left)
            field[bottom + 1] += "111".toInt(2) shl (4 - left)
            field[bottom] += "010".toInt(2) shl (4 - left)
        }
        Rock.third -> {
            field[bottom + 2] += "001".toInt(2) shl (4 - left)
            field[bottom + 1] += "001".toInt(2) shl (4 - left)
            field[bottom] += "111".toInt(2) shl (4 - left)
        }
        Rock.fourth -> {
            field[bottom + 3] += "1".toInt(2) shl (6 - left)
            field[bottom + 2] += "1".toInt(2) shl (6 - left)
            field[bottom + 1] += "1".toInt(2) shl (6 - left)
            field[bottom] += "1".toInt(2) shl (6 - left)
        }
        Rock.fifth -> {
            field[bottom + 1] += "11".toInt(2) shl (5 - left)
            field[bottom] += "11".toInt(2) shl (5 - left)
        }
    }
}

private operator fun Int.get(index: Int): Int {
    return if (((1 shl (6 - index)) and this) == 0) 0 else 1
}

private data class PossibleCycle(val lastRocks: List<Int>, val position: Int, val rock: Rock)
