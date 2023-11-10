package `2022`

import readInput
import splitBy
import top

fun main() {

    fun part1(input: List<String>): Int {
        val monkeys = input.splitBy { it.isEmpty() }.map { Monkey.fromDesc(it) }
        repeat(20) {
            monkeys.processRound()
        }
        val (first, second) = monkeys.map { it.inspects }.asSequence().top(2)
        return first * second
    }

    fun part2(input: List<String>): Long {
        val monkeys = input.splitBy { it.isEmpty() }.map { Monkey.fromDesc(it) }
        val mod = monkeys.map {it.testValue}.fold(1) { x, y -> x * y }
        repeat(10000) {
            monkeys.processRoundByMod(mod)
        }
        val (first, second) = monkeys.map { it.inspects }.asSequence().top(2)
        return first.toLong() * second
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 10605)
    check(part2(testInput) == 2713310158)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

data class Monkey(val itemsWorries: MutableList<Int>, val operationType: String, val amount: Int, val testValue: Int, val trueMonkey: Int, val falseMonkey: Int, var inspects: Int = 0) {
    companion object {
        fun fromDesc(input: List<String>): Monkey {
            val itemsWorries = input[1].substringAfter("Starting items: ").split(",").map { it.trim().toInt() }
            var (operationType, amount) = input[2].substringAfter("Operation: new = old ").split(" ").map { it.trim() }
            val testValue = input[3].substringAfter("Test: divisible by ").toInt()
            val trueMonkey = input[4].substringAfter("If true: throw to monkey ").toInt()
            val falseMonkey = input[5].substringAfter("If false: throw to monkey ").toInt()
            var amountInt = 0
            if (amount == "old") {
                operationType += " old"
            } else {
                amountInt = amount.toInt()
            }
            return Monkey(itemsWorries.toMutableList(), operationType, amountInt, testValue, trueMonkey, falseMonkey)
        }
    }
}

fun List<Monkey>.processRound(): List<Monkey> {
    forEach { monkey ->
        monkey.itemsWorries.forEach {
            monkey.inspects++
            var newWorry = when (monkey.operationType) {
                "* old" -> it * it
                "+ old" -> it + it
                "*" -> it * monkey.amount
                else -> it + monkey.amount
            }
            newWorry /= 3
            if (newWorry % monkey.testValue == 0) {
                this[monkey.trueMonkey].itemsWorries.add(newWorry)
            } else {
                this[monkey.falseMonkey].itemsWorries.add(newWorry)
            }
        }
        monkey.itemsWorries.clear()
    }

    return this
}

fun List<Monkey>.processRoundByMod(mod: Int): List<Monkey> {
    forEach { monkey ->
        monkey.itemsWorries.forEach {
            monkey.inspects++
            val oldWorry = it.toLong()
            var newWorry = when (monkey.operationType) {
                "* old" -> oldWorry * oldWorry
                "+ old" -> oldWorry + oldWorry
                "*" -> oldWorry * monkey.amount
                else -> oldWorry + monkey.amount
            }
            newWorry %= mod
            if (newWorry % monkey.testValue == 0L) {
                this[monkey.trueMonkey].itemsWorries.add(newWorry.toInt())
            } else {
                this[monkey.falseMonkey].itemsWorries.add(newWorry.toInt())
            }
        }
        monkey.itemsWorries.clear()
    }

    return this
}
