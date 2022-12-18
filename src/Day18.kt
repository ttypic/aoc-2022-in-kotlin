fun main() {

    fun part1(input: List<String>): Int {
        val drops = input.map {
            val (x, y, z) = it.split(",").map(String::toInt)
            Point3(x, y, z)
        }.toSet()

        return drops.sumOf { drop ->
            drop.slideNeighbors().filter { !drops.contains(it) }.size
        }
    }

    fun part2(input: List<String>): Int {
        val drops = input.map {
            val (x, y, z) = it.split(",").map(String::toInt)
            Point3(x, y, z)
        }.toSet()

        val maxX = drops.maxOf { it.x }
        val maxY = drops.maxOf { it.y }
        val maxZ = drops.maxOf { it.z }

        val minX = drops.minOf { it.x }
        val minY = drops.minOf { it.y }
        val minZ = drops.minOf { it.z }

        return drops.sumOf { lavaDrop ->
            lavaDrop.slideNeighbors().filter { !drops.contains(it) }.filter { airDrop ->
                var freeAirDrops = airDrop.slideNeighbors().filter { !drops.contains(it) }.toSet()
                val visitedDrops = mutableSetOf<Point3>()
                do {
                    visitedDrops.addAll(freeAirDrops)
                    freeAirDrops = freeAirDrops.flatMap { it.slideNeighbors() }.filter { !drops.contains(it) && !visitedDrops.contains(it) }.toSet()
                    val isTrapped = freeAirDrops.all { it.x in minX..maxX && it.y in minY..maxY && it.z in minZ..maxZ}
                } while (freeAirDrops.isNotEmpty() && isTrapped)

                freeAirDrops.isNotEmpty()
            }.size
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 64)
    check(part2(testInput) == 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}

data class Point3(val x: Int, val y: Int, val z: Int)

fun Point3.slideNeighbors(): List<Point3> {
    val (x, y, z) = this
    return listOf(Point3(x - 1, y, z), Point3(x + 1, y, z), Point3(x, y - 1, z), Point3(x, y + 1, z), Point3(x, y, z - 1), Point3(x, y, z + 1))
}
