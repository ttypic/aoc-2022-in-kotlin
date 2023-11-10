package `2022`

import readInput

fun main() {

    fun Iterable<Char>.priority(): Int {
       return this.sumOf { if (it in 'a'..'z') it - 'a' + 1 else it - 'A' + 27 }
    }

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val firstCompartment = it.substring(0, it.length / 2)
            val secondCompartment = it.substring(it.length / 2)
            val intersect = firstCompartment.asIterable().intersect(secondCompartment.toSet())
            intersect.priority()
        }
    }

    fun part2(input: List<String>): Int {
        return input.chunked(3).sumOf { group ->
            val common = group.reduce { commonItems, elfItems ->
                val intersect = commonItems.asIterable().intersect(elfItems.toSet())
                String(intersect.toCharArray())
            }
            common.asIterable().priority()
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
