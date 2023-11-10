package `2022`

import readInput

fun main() {

    fun calculate(phrase: MonkeyPhrase, nameToResult: MutableMap<String, Long>, nameToPhrase: Map<String, MonkeyPhrase>): Long {
        if (nameToResult.contains(phrase.name)) {
            return nameToResult[phrase.name]!!
        }
        when (phrase) {
            is MonkeyPhrase.Operation -> {
                val firstOperand = calculate(nameToPhrase[phrase.firstOperand]!!, nameToResult, nameToPhrase)
                val secondOperand = calculate(nameToPhrase[phrase.secondOperand]!!, nameToResult, nameToPhrase)
                return when (phrase.operation) {
                    "*" -> firstOperand * secondOperand
                    "/" -> firstOperand / secondOperand
                    "+" -> firstOperand + secondOperand
                    "-" -> firstOperand - secondOperand
                    else -> error("illegal operand")
                }
            }
            is MonkeyPhrase.Num -> {
                return phrase.value
            }
        }
    }

    fun reverseCalculate(phrase: MonkeyPhrase, nameToResult: MutableMap<String, MonkeyOperationResult>, nameToPhrase: Map<String, MonkeyPhrase>): MonkeyOperationResult {
        if (nameToResult.contains(phrase.name)) {
            return nameToResult[phrase.name]!!
        }
        when (phrase) {
            is MonkeyPhrase.Operation -> {
                val firstOperand = reverseCalculate(nameToPhrase[phrase.firstOperand]!!, nameToResult, nameToPhrase)
                val secondOperand = reverseCalculate(nameToPhrase[phrase.secondOperand]!!, nameToResult, nameToPhrase)
                val result = when (phrase.operation) {
                    "*" -> firstOperand * secondOperand
                    "/" -> firstOperand / secondOperand
                    "+" -> firstOperand + secondOperand
                    "-" -> firstOperand - secondOperand
                    else -> error("illegal operand")
                }
                nameToResult[phrase.name] = result
                return result
            }
            is MonkeyPhrase.Num -> {
                error("illegal state")
            }
        }
    }

    fun part1(input: List<String>): Long {
        val phrases = input.map { MonkeyPhrase.parse(it) }
        val nameToPhrase = phrases.associateBy { it.name }
        val rootMonkey = nameToPhrase["root"]!!
        val nameToResult = phrases.filterIsInstance<MonkeyPhrase.Num>().associateBy { it.name }.mapValues { it.value.value }.toMutableMap()
        return calculate(rootMonkey, nameToResult, nameToPhrase)
    }

    fun part2(input: List<String>): Long {
        val phrases = input.map { MonkeyPhrase.parse(it) }
        val nameToPhrase = phrases.associateBy { it.name }
        val rootMonkey = nameToPhrase["root"]!! as MonkeyPhrase.Operation
        val firstOperand = nameToPhrase[rootMonkey.firstOperand]!!
        val secondOperand = nameToPhrase[rootMonkey.secondOperand]!!
        val nameToResult: MutableMap<String, MonkeyOperationResult> = phrases.filterIsInstance<MonkeyPhrase.Num>().associateBy { it.name }.mapValues {
            MonkeyOperationResult.Num(Rational(it.value.value))
        }.toMutableMap()
        nameToResult["humn"] = MonkeyOperationResult.X(Rational(0L), Rational(1L))
        val firstResult = reverseCalculate(firstOperand, nameToResult, nameToPhrase).toX()
        val secondResult = reverseCalculate(secondOperand, nameToResult, nameToPhrase).toX()
        return ((firstResult.value - secondResult.value) / (secondResult.multi - firstResult.multi)).toLong()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    println(part1(testInput))
    println(part2(testInput))
    check(part1(testInput) == 152L)
    check(part2(testInput) == 301L)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}

sealed interface MonkeyPhrase {
    val name: String

    data class Operation(
        override val name: String,
        val firstOperand: String,
        val operation: String,
        val secondOperand: String
    ): MonkeyPhrase

    data class Num(override val name: String, val value: Long): MonkeyPhrase

    companion object {
        fun parse(input: String): MonkeyPhrase {
            val name = input.substringBefore(":")
            val rest = input.substringAfter(":").trim()
            if ("^-?\\d+$".toRegex().matches(rest)) {
                return Num(name, rest.toLong())
            }
            val (_, firstMonkey, operation, secondMonkey) =  "(\\w+) (.) (\\w+)".toRegex().find(rest)!!.groups.map { it!!.value }
            return Operation(name, firstMonkey, operation, secondMonkey)
        }
    }
}

sealed interface MonkeyOperationResult {
    data class Num(val value: Rational): MonkeyOperationResult
    data class X(val value: Rational, val multi: Rational): MonkeyOperationResult

    fun toX(): X {
        return when(this) {
            is X -> this
            is Num -> X(this.value, Rational(0L))
        }
    }

    operator fun times(other: MonkeyOperationResult): MonkeyOperationResult {
        return when(this) {
            is Num -> {
                when(other) {
                    is Num -> Num(this.value * other.value)
                    is X -> X(this.value * other.value, this.value * other.multi)
                }
            }
            is X -> {
                when(other) {
                    is Num -> X(this.value * other.value, this.multi * other.value)
                    is X -> error("too complex")
                }
            }
        }
    }

    operator fun div(other: MonkeyOperationResult): MonkeyOperationResult {
        return when(this) {
            is Num -> {
                when(other) {
                    is Num -> Num(this.value / other.value)
                    is X -> error("too complex")
                }
            }
            is X -> {
                when(other) {
                    is Num -> X(this.value / other.value, this.multi / other.value)
                    is X -> error("too complex")
                }
            }
        }
    }

    operator fun plus(other: MonkeyOperationResult): MonkeyOperationResult {
        return when(this) {
            is Num -> {
                when(other) {
                    is Num -> Num(this.value + other.value)
                    is X -> X(this.value + other.value, other.multi)
                }
            }
            is X -> {
                when(other) {
                    is Num -> X(this.value + other.value, this.multi)
                    is X -> X(this.value + other.value, this.multi + other.multi)
                }
            }
        }
    }

    operator fun minus(other: MonkeyOperationResult): MonkeyOperationResult {
        return when(this) {
            is Num -> {
                when(other) {
                    is Num -> Num(this.value - other.value)
                    is X -> X(this.value - other.value, -other.multi)
                }
            }
            is X -> {
                when(other) {
                    is Num -> X(this.value - other.value, this.multi)
                    is X -> X(this.value - other.value, this.multi - other.multi)
                }
            }
        }
    }
}

data class Rational(val nominator: Long, val denominator: Long = 1) {
    operator fun times(other: Rational): Rational {
        return Rational(nominator * other.nominator, denominator * other.denominator).compact()
    }
    operator fun div(other: Rational): Rational {
        return Rational(nominator * other.denominator, denominator * other.nominator).compact()
    }
    operator fun plus(other: Rational): Rational {
        return Rational(nominator * other.denominator + other.nominator * denominator, denominator * other.denominator).compact()
    }
    operator fun minus(other: Rational): Rational {
        return Rational(nominator * other.denominator - other.nominator * denominator, denominator * other.denominator).compact()
    }
    operator fun unaryMinus(): Rational {
        return copy(nominator = -nominator)
    }

    fun toLong(): Long {
        return nominator / denominator
    }

    private fun compact(): Rational {
        val gcd = gcd(nominator, denominator)
        return Rational(nominator / gcd, denominator / gcd)
    }
}

fun gcd(first: Long, second: Long): Long = if (second == 0L) first else gcd(second, first % second)
