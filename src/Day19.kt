fun main() {

    fun parseBlueprint(blueprintConfig: String): Blueprint {
        val regex =
            """Blueprint (\d+): Each ore robot costs (\d+) ore. Each clay robot costs (\d+) ore. Each obsidian robot costs (\d+) ore and (\d+) clay. Each geode robot costs (\d+) ore and (\d+) obsidian""".toRegex()
        val groups = regex.find(blueprintConfig)!!.groups
        return Blueprint(
            id = groups[1]!!.value.toInt(),
            oreRobotCost = groups[2]!!.value.toInt(),
            clayRobotCost = groups[3]!!.value.toInt(),
            obsidianRobotCost = groups[4]!!.value.toInt() to groups[5]!!.value.toInt(),
            geodeRobotCost = groups[6]!!.value.toInt() to groups[7]!!.value.toInt(),
        )
    }

    fun findMaxGeodesRobot(initialState: Day19State, blueprint: Blueprint): Int {
        var states = listOf(initialState to initialState)

        do {
            states = states.flatMap { (state, prev) ->
                buildList {
                    if (state.canBuild(blueprint, Robot.geode)) {
                        add(state.buildRobot(blueprint, Robot.geode) to state)
                    } else {
                        add(state.tick(1) to state)
                        listOf(Robot.ore, Robot.clay, Robot.obsidian).forEach { robot ->
                            val canBuild = prev.canBuild(blueprint, robot)
                            val skipped = prev.eqByRobots(state)
                            val canBuildNow = state.canBuild(blueprint, robot)
                            if (!(canBuild && skipped) && canBuildNow) {
                                add(state.buildRobot(blueprint, robot) to state)
                            }
                        }
                    }
                }
            }
        } while (states.first().first.countdown != 0)

        return states.maxOf { it.first.geode }
    }


    fun part1(input: List<String>): Int {
        val blueprints = input.map { parseBlueprint(it) }
        return blueprints.sumOf {
            val max = findMaxGeodesRobot(Day19State(), it)
            it.id * max
        }
    }

    fun part2(input: List<String>): Int {
        val blueprints = input.map { parseBlueprint(it) }.take(3)
        return blueprints.map {
            findMaxGeodesRobot(Day19State(countdown = 32), it)
        }.reduce(Int::times)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 33)
    check(part2(testInput) == 62 * 56)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}

data class Blueprint(
    val id: Int,
    val oreRobotCost: Int,
    val clayRobotCost: Int,
    val obsidianRobotCost: Pair<Int, Int>,
    val geodeRobotCost: Pair<Int, Int>
)

data class Day19State(
    val countdown: Int = 24,
    val oreProducers: Int = 1,
    val clayProducers: Int = 0,
    val obsidianProducers: Int = 0,
    val geodeProducers: Int = 0,
    val ore: Int = 0,
    val clay: Int = 0,
    val obsidian: Int = 0,
    val geode: Int = 0
)

fun Day19State.isValid(): Boolean {
    return ore >= 0 && clay >= 0 && obsidian >= 0
}

fun Day19State.tick(time: Int): Day19State {
    return copy(
        countdown = countdown - time,
        ore = oreProducers * time + ore,
        clay = clayProducers * time + clay,
        obsidian = obsidianProducers * time + obsidian,
        geode = geodeProducers * time + geode,
    )
}

fun Day19State.produceGeode(blueprint: Blueprint, removeOne: Boolean = false): Day19State {
    return copy(
        geodeProducers = geodeProducers + 1,
        ore = ore - blueprint.geodeRobotCost.first,
        obsidian = obsidian - blueprint.geodeRobotCost.second,
        geode = if (removeOne) geode - 1 else geode,
    )
}

fun Day19State.produceObsidian(blueprint: Blueprint, removeOne: Boolean = false): Day19State {
    return copy(
        obsidianProducers = obsidianProducers + 1,
        ore = ore - blueprint.obsidianRobotCost.first,
        clay = clay - blueprint.obsidianRobotCost.second,
        obsidian = if (removeOne) obsidian - 1 else obsidian,
    )
}

fun Day19State.produceClay(blueprint: Blueprint, removeOne: Boolean = false): Day19State {
    return copy(
        clayProducers = clayProducers + 1,
        ore = ore - blueprint.clayRobotCost,
        clay = if (removeOne) clay - 1 else clay,
    )
}

fun Day19State.produceOre(blueprint: Blueprint, removeOne: Boolean = false): Day19State {
    val nextOre = ore - blueprint.oreRobotCost
    return copy(
        oreProducers = oreProducers + 1,
        ore = if (removeOne) nextOre - 1 else nextOre,
    )
}

fun Day19State.eqByRobots(other: Day19State): Boolean {
    return oreProducers == other.oreProducers && clayProducers == other.clayProducers &&
            obsidianProducers == other.obsidianProducers && geodeProducers == other.geodeProducers
}

fun Day19State.buildRobot(blueprint: Blueprint, robot: Robot): Day19State {
    return when (robot) {
            Robot.geode -> this.produceGeode(blueprint, true)
            Robot.obsidian -> this.produceObsidian(blueprint, true)
            Robot.clay -> this.produceClay(blueprint, true)
            Robot.ore -> this.produceOre(blueprint, true)
        }.tick(1)
}

fun Blueprint.maxOre(): Int {
    return oreRobotCost
        .coerceAtLeast(clayRobotCost)
        .coerceAtLeast(obsidianRobotCost.first)
        .coerceAtLeast(geodeRobotCost.first)
}

fun Day19State.canBuild(blueprint: Blueprint, robot: Robot): Boolean {
    val hasReason = when (robot) {
        Robot.geode -> true
        Robot.obsidian -> obsidianProducers < blueprint.geodeRobotCost.second
        Robot.clay -> clayProducers < blueprint.obsidianRobotCost.second
        Robot.ore -> oreProducers < blueprint.maxOre()
    }

    return hasReason && when (robot) {
        Robot.geode -> this.produceGeode(blueprint)
        Robot.obsidian -> this.produceObsidian(blueprint)
        Robot.clay -> this.produceClay(blueprint)
        Robot.ore -> this.produceOre(blueprint)
    }.isValid()
}

enum class Robot {
    geode,
    obsidian,
    clay,
    ore
}
