fun main() {

    lateinit var nameToValve: Map<String, Valve>
    lateinit var valves: List<Valve>
    lateinit var valuableValves: List<Valve>
    val pathToCost: MutableMap<Pair<Valve, Valve>, Int> = mutableMapOf()

    fun parseInput(input: List<String>) {
        valves = input.map { s ->
            val (_, name, rate, neighbors) = "Valve (\\w+) has flow rate=(\\d+); \\w+ \\w+ to \\w+ ([\\w\\s,]+)".toRegex().find(s)!!.groups.map { it!!.value }
            Valve(name, rate.toInt(), neighbors.split(",").map { it.trim() })
        }
        valuableValves = valves.filter { it.rate > 0 }
        nameToValve = valves.associateBy { it.name }
        val first = nameToValve["AA"]!!

        (valuableValves + first).forEach { start ->
            var currentNodes = start.neighbors
            (1..30).forEach { minute ->
                currentNodes = currentNodes.flatMap {
                    val end = (nameToValve[it]!!)
                    val path = start to end
                    if (pathToCost.contains(path)) {
                        emptyList()
                    } else {
                        pathToCost[path] = minute
                        end.neighbors
                    }
                }
            }
        }
    }

    fun findMaxPressure(countdown: Int, current: Valve, restValves: List<Valve>, pressure: Int): Int {
        if (countdown <= 0) return pressure
        return restValves.maxOfOrNull { valve ->
            val cost = pathToCost[current to valve]!!
            val nextCountdown = countdown - cost - 1
            findMaxPressure(nextCountdown, valve, restValves - valve, pressure + nextCountdown * valve.rate)
        } ?: pressure
    }

    fun part1(input: List<String>): Int {
        parseInput(input)
        val start = nameToValve["AA"]!!
        return findMaxPressure(30, start, valuableValves, 0)
    }

    fun part2(input: List<String>): Int {
        parseInput(input)
        val start = nameToValve["AA"]!!
        val splitValves = valuableValves.buildSubsets()
        return splitValves
            .asSequence()
            .filter { it.isNotEmpty() && it.size != valuableValves.size }
            .maxOf { my ->
                val elephant = valuableValves.toSet() - my
                findMaxPressure(26, start, my.toList(), 0) +
                        findMaxPressure(26, start, elephant.toList(), 0)
            }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 1651)
    check(part2(testInput) == 1707)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))

    /*fun printPath(path: List<Valve>) {
        var min = 0
        var pressure = 0
        path.windowed(2).forEach { (prev, next) ->
            println("Open valve=${prev.name} at min=${min}")
            pressure += (30 - min) * prev.rate
            val cost = pathToCost[prev to next]!!
            min += cost
            println("Go to valve=${next.name} and now min=${min}")
            min += 1
        }
        pressure += (30 - min) * path.last().rate
        println("Released $pressure")
    }*/
}

private data class Valve(val name: String, val rate: Int, val neighbors: List<String>)

private fun List<Valve>.buildSubsets(): List<Set<Valve>> {
    val subsets = mutableListOf<Set<Valve>>()

    fun addAllSubsets(subset: Set<Valve> = emptySet(), index: Int = 0) {
        if (index == size) {
            subsets.add(subset)
            return
        }
        addAllSubsets(subset, index + 1)
        addAllSubsets(subset + this[index], index + 1)
    }

    addAllSubsets()

    return subsets
}



