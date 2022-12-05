import java.io.File
import java.math.BigInteger
import java.security.MessageDigest

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt")
    .readLines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')


fun <T> Sequence<T>.splitBy(predicate: (T) -> Boolean): Sequence<Sequence<T>> {
    val iterator = this.iterator()
    return generateSequence {
        if (!iterator.hasNext()) return@generateSequence null
        sequence {
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (predicate(next)) return@sequence
                yield(next)
            }
        }
    }
}

fun <T> List<T>.splitBy(predicate: (T) -> Boolean): List<List<T>> {
    return this.asSequence().splitBy(predicate).map { it.toList() }.toList()
}

fun <T : Comparable<T>> Sequence<T>.top(n: Int): List<T> {
    val topN = mutableListOf<T>()
    val iterator = iterator()
    while (iterator.hasNext()) {
        val e = iterator.next()
        if (topN.size < n) {
            topN.add(e)
            topN.sort()
        } else if (topN.first() < e) {
            topN[0] = e
            topN.sort()
        }
    }
    return topN
}

fun <T : Comparable<T>> Sequence<T>.topThree(): List<T> = this.top(3)
