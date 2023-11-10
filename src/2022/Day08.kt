package `2022`

import readInput

fun main() {

    fun part1(input: List<String>): Int {
        val forest = input.map { it.asIterable().map { char -> char.toString().toInt() } }

        return forest.flatMapIndexed { row, trees ->
            trees.filterIndexed { column, treeHeight ->
                val checkTop = forest.subList(0, row).map { it[column] }.all { it < treeHeight }
                val checkBottom = forest.subList(row + 1, forest.size).map { it[column] }.all { it < treeHeight }
                val checkLeft = trees.subList(0, column).all { it < treeHeight }
                val checkRight = trees.subList(column + 1, trees.size).all { it < treeHeight }

                checkTop || checkBottom || checkLeft || checkRight
            }
        }.size
    }

    fun part2(input: List<String>): Int {
        val forest = input.map { it.asIterable().map { char -> char.toString().toInt() } }

        return forest.flatMapIndexed { row, trees ->
            trees.mapIndexed { column, treeHeight ->
                val top = forest.subList(0, row).reversed().map { it[column] }.visibleTrees(treeHeight)
                val bottom = forest.subList(row + 1, forest.size).map { it[column] }.visibleTrees(treeHeight)
                val left = trees.subList(0, column).reversed().visibleTrees(treeHeight)
                val right = trees.subList(column + 1, trees.size).visibleTrees(treeHeight)
                top * bottom * left * right
            }
        }.max()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

fun List<Int>.visibleTrees(treeHeight: Int): Int {
    return (takeWhile { it < treeHeight }.size + 1).coerceAtMost(size)
}
