fun main() {

    fun part1(input: List<String>): Int {
        var cycle = 0
        var x = 1
        val interestingCycles = setOf(20, 60, 100, 140, 180, 220)
        return input.sumOf {
            var result = 0
            val commands = it.split(" ")
            when(commands[0]) {
                "noop" -> {
                    cycle++
                    if (cycle in interestingCycles) result = cycle * x
                }
                else -> {
                    repeat(2) {
                        cycle++
                        if (cycle in interestingCycles) result = cycle * x
                    }
                    x += commands[1].toInt()
                }
            }
            result
        }
    }

    fun part2(input: List<String>) {
        var cycle = 0
        var x = 1

        fun getSpritePixel(x: Int, cycle: Int): String {
            return if ((cycle % 40 - x) in -1..1) "#" else "."
        }

        input.flatMap {
            val pixels = mutableListOf<String>()
            val commands = it.split(" ")
            when(commands[0]) {
                "noop" -> {
                    pixels.add(getSpritePixel(x, cycle))
                    cycle++
                }
                else -> {
                    repeat(2) {
                        pixels.add(getSpritePixel(x, cycle))
                        cycle++
                    }
                    x += commands[1].toInt()
                }
            }
            pixels
        }.chunked(40).forEach{
            println(it.joinToString(separator = ""))
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 13140)
    part2(testInput)

    val input = readInput("Day10")
    println(part1(input))
    part2(input)
}
