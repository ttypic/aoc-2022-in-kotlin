fun main() {

    fun readStackChart(stackChart: List<String>): List<MutableList<Char>> {
        val cargoStacks = stackChart.last().trim().split("\\s+".toRegex()).map { _ -> mutableListOf<Char>() }

        for (i in 0..(stackChart.size - 2))  {
            cargoStacks.forEachIndexed { index, stack ->
                val charIndex = index * 4 + 1
                val stackLevel = stackChart[i]
                if (stackLevel.length <= charIndex || stackLevel[charIndex] == ' ') return@forEachIndexed
                stack.add(stackLevel[charIndex])
            }
        }

        return cargoStacks
    }

    fun prepareInput(input: List<String>): StacksAndCommands {
        val (stackChart, commandDescriptions) = input.splitBy { it.isEmpty() }

        return StacksAndCommands(
            readStackChart(stackChart),
            commandDescriptions.map(Command.Companion::fromDescription)
        )
    }

    fun part1(input: List<String>): String {
        val (stacks, commands) = prepareInput(input)
        val rearrangedStacks = commands.fold(stacks) { next, command ->
            command.executeCommand(next, Mover.CrateMover9000)
        }
        return rearrangedStacks.map { it.first() }.joinToString("")
    }

    fun part2(input: List<String>): String {
        val (stacks, commands) = prepareInput(input)
        val rearrangedStacks = commands.fold(stacks) { next, command ->
            command.executeCommand(next, Mover.CrateMover9001)
        }
        return rearrangedStacks.map { it.first() }.joinToString("")
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == "CMZ")
    check(part2(testInput) == "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

data class StacksAndCommands(
    val stacks: List<MutableList<Char>>,
    val commands: List<Command>,
)

data class Command(
    val to: Int,
    val from: Int,
    val count: Int,
) {
    companion object {
        private operator fun <E> List<E>.component6(): E {
            return this[5]
        }
        fun fromDescription(description: String): Command {
            val (_, count, _, from, _, to) = description.split(" ")
            return Command(
                to = to.toInt() - 1,
                from = from.toInt() - 1,
                count = count.toInt(),
            )
        }
    }
}

sealed interface Mover {
    fun move(from: MutableList<Char>, to: MutableList<Char>, count: Int)
    object CrateMover9000: Mover {
        override fun move(from: MutableList<Char>, to: MutableList<Char>, count: Int) {
            for (i in 0 until count) {
                to.add(0, from.first())
                from.removeFirst()
            }
        }
    }
    object CrateMover9001: Mover {
        override fun move(from: MutableList<Char>, to: MutableList<Char>, count: Int) {
            val (cargos) = from.chunked(count)
            to.addAll(0, cargos)
            cargos.forEach { _ -> from.removeFirst()}
        }
    }

}

fun Command.executeCommand(stacks: List<MutableList<Char>>, mover: Mover): List<MutableList<Char>> {
    mover.move(stacks[from], stacks[to], count)
    return stacks
}
