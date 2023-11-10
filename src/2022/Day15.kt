package `2022`

import readInput
import kotlin.math.abs

fun main() {

    fun findDistressedBeacon(sensorToDistance: List<Day15Input>, maxSize: Int): YPoint {
        (0..maxSize).forEach { y ->
            val ranges = sensorToDistance.toReverseRanges(y, maxSize)
            if (ranges.isNotEmpty()) {
                return ranges.first().start
            }
        }
        error("Can't find distressed beacon")
    }

    fun readInput(input: List<String>): List<Day15Input> {
        val sensorToDistance = input.map { s ->
            val (sensor, beacon) = "(x=|y=)(-?\\d+)"
                .toRegex().findAll(s)
                .map { it.groups[2]!!.value.toInt() }
                .chunked(2)
                .map { (x, y) -> YPoint(x, y) }
                .toList()
            val distance = sensor.dist(beacon)
            Day15Input(sensor, distance, beacon)
        }
        return sensorToDistance
    }

    fun part1(input: List<String>, row: Int): Int {
        val sensorToBeacon = readInput(input)
        val sensors = sensorToBeacon.map { it.sensor }.toSet()
        val beacons = sensorToBeacon.map { it.beacon }.toSet()
        val ranges = sensorToBeacon.toRanges(row)
        val minX = ranges.map { it.start.x }.min()
        val maxX = ranges.map { it.endInclusive.x }.max()

        return (minX..maxX).asSequence().filter { x ->
            val point = YPoint(x, row)
            !sensors.contains(point) && !beacons.contains(point)
        }.count()
    }

    fun part2(input: List<String>, maxSize: Int): Long {
        val sensorToDistance = readInput(input)
        val (x, y) = findDistressedBeacon(sensorToDistance, maxSize)
        return x * 4000000L + y
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    println(part1(testInput, 10))
    println(part2(testInput, 20))
    check(part1(testInput, 10) == 26)
    check(part2(testInput, 20) == 56000011L)

    val input = readInput("Day15")
    println(part1(input, 2000000))
    println(part2(input, 4000000))
}

private fun YPoint.dist(other: YPoint): Int {
    return abs(x - other.x) + abs(y - other.y)
}

private data class Day15Input(val sensor: YPoint, val distance: Int, val beacon: YPoint)

private data class YPoint(val x: Int, val y: Int): Comparable<YPoint> {
    override fun compareTo(other: YPoint): Int {
        return x - other.x
    }
}

private fun YPoint.prev(): YPoint {
    return YPoint(x - 1, y)
}

private fun YPoint.next(): YPoint {
    return YPoint(x + 1, y)
}

private fun List<Day15Input>.toRanges(row: Int): List<ClosedRange<YPoint>> {
    return mapNotNull { (sensor, distance) ->
        val delta = distance - abs(sensor.y - row)
        if (delta >= 0) {
            YPoint(sensor.x - delta, row)..YPoint(sensor.x + delta, row )
        } else {
            null
        }
    }
}

private fun List<Day15Input>.toReverseRanges(row: Int, maxSize: Int): List<ClosedRange<YPoint>> {
    val ranges = toRanges(row)
    return ranges.fold(listOf(YPoint(0, row)..YPoint(maxSize, row))) { acc, range ->
        acc.subtract(range)
    }
}

private fun List<ClosedRange<YPoint>>.subtract(range: ClosedRange<YPoint>): List<ClosedRange<YPoint>> {
    return filter {
        !(range.contains(it.start) && range.contains(it.endInclusive))
    }.flatMap {
        if (range.contains(it.start)) {
            listOf(range.endInclusive.next()..it.endInclusive)
        } else if (range.contains(it.endInclusive)) {
            listOf(it.start..range.start.prev())
        } else if (it.contains(range.start)) {
            listOf(
                it.start..range.start.prev(),
                range.endInclusive.next()..it.endInclusive
            )
        } else {
            listOf(it)
        }
    }
}
