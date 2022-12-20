fun main() {

    fun mixing(coordinates: List<Long>, items: MutableList<IndexedValue<Long>>, iterations: Int) {
        repeat(iterations) {
            coordinates.forEachIndexed { coordIndex, coord ->
                val index = items.indexOfFirst { it.index == coordIndex }
                if (coord < 0L) {
                    items.removeAt(index)
                    var nextIndex = index + coord
                    if (nextIndex <= 0L) {
                        val nums = (nextIndex / items.size) * -1 + 1
                        nextIndex += nums * items.size
                    }
                    items.add(nextIndex.toInt(), IndexedValue(coordIndex, coord))
                } else if (coord > 0L) {
                    items.removeAt(index)
                    var nextIndex = index + coord
                    if (nextIndex >= items.size.toLong()) {
                        val nums = (nextIndex / items.size)
                        nextIndex -= nums * items.size
                    }
                    items.add(nextIndex.toInt(), IndexedValue(coordIndex, coord))
                }
            }
        }
    }

    fun part1(input: List<String>): Long {
        val coordinates = input.map { it.toLong() }
        val state = coordinates.withIndex().toMutableList()
        mixing(coordinates, state, 1)
        val index = state.indexOfFirst { it.value == 0L }
        return state[(index + 1000) % coordinates.size].value + state[(index + 2000) % coordinates.size].value + state[(index + 3000) % coordinates.size].value
    }

    fun part2(input: List<String>): Long {
        val coordinates = input.map { it.toLong() * 811589153 }
        val state = coordinates.withIndex().toMutableList()
        mixing(coordinates, state, 10)
        val index = state.indexOfFirst { it.value == 0L }
        return state[(index + 1000) % coordinates.size].value + state[(index + 2000) % coordinates.size].value + state[(index + 3000) % coordinates.size].value
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 3L)
    check(part2(testInput) == 1623178306L)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}

data class IndexedCoord(val index: Int, val coord: Int)
