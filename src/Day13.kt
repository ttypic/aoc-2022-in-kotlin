fun main() {

    fun part1(input: List<String>): Int {
        return input.splitBy { it.isEmpty() }.mapIndexed { index, (left, right) ->
            val leftPacket = Packet.parsePacket(left)
            val rightPacket = Packet.parsePacket(right)
            if (leftPacket <= rightPacket) {
                index + 1
            } else {
                0
            }
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val packets = input.filter { it.isNotBlank() }.map { Packet.parsePacket(it) }
        val signal1 = ListPacket(mutableListOf(ListPacket(mutableListOf(IntPacket(2)))))
        val signal2 = ListPacket(mutableListOf(ListPacket(mutableListOf(IntPacket(6)))))
        val sorted = (packets + signal1 + signal2).sorted()
        return (sorted.indexOf(signal1) + 1) * (sorted.indexOf(signal2) + 1)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 13)
    check(part2(testInput) == 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}


sealed interface Packet: Comparable<Packet> {
    companion object  {
        fun parsePacket(packetPresentation: String): Packet {
            val tokens = "\\d+|]|\\[".toRegex().findAll(packetPresentation).map { it.value }
            if (tokens.first() != "[") {
                return IntPacket(tokens.first().toInt())
            }

            val listPacket = ListPacket(mutableListOf())
            val stack = ArrayDeque<ListPacket>()
            stack.addFirst(listPacket)
            tokens.drop(1).forEach {
                when(it) {
                    "[" -> {
                        stack.addFirst(ListPacket(mutableListOf()))
                    }
                    "]" -> {
                        val currentPacket = stack.removeFirst()
                        stack.firstOrNull()?.value?.add(currentPacket)
                    }
                    else -> {
                        stack.first().value.add(IntPacket(it.toInt()))
                    }
                }
            }

            return listPacket
        }
    }
}

data class IntPacket(val value: Int): Packet {
    override fun compareTo(other: Packet): Int {
        return when(other) {
            is IntPacket -> {
                value - other.value
            }
            is ListPacket -> {
                ListPacket(mutableListOf(this)).compareTo(other)
            }
        }
    }

    override fun toString(): String {
        return value.toString()
    }
}

data class ListPacket(val value: MutableList<Packet>): Packet {
    override fun compareTo(other: Packet): Int {
        when(other) {
            is IntPacket -> {
                return this.compareTo(ListPacket(mutableListOf(other)))
            }
            is ListPacket -> {
                value.forEachIndexed { index, packet ->
                    if (index >= other.value.size) return 1
                    val compare = packet.compareTo(other.value[index])
                    if (compare < 0) return -1
                    if (compare > 0) return 1
                }
                return value.size - other.value.size
            }
        }
    }

    override fun toString(): String {
        return value.joinToString(separator = ",", prefix = "[", postfix = "]")
    }
}
