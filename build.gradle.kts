plugins {
    kotlin("jvm") version "1.7.22"
}

repositories {
    mavenCentral()
}

tasks {
    sourceSets {
        main {
            java.srcDirs("src")
        }
    }

    wrapper {
        gradleVersion = "7.6"
    }
}

tasks.register("generateDay") {
    doLast {
        val day: String by project
        val paddedDay = day.padStart(2, '0')
        val file = File("src/Day${paddedDay}.kt")
        file.createNewFile()
        file.writeText("""
            fun main() {
    
                fun part1(input: List<String>): Int {
                    return 0
                }
    
                fun part2(input: List<String>): Int {
                    return 0
                }
    
                // test if implementation meets criteria from the description, like:
                val testInput = readInput("Day${paddedDay}_test")
                println(part1(testInput))
                println(part2(testInput))
                check(part1(testInput) == 0)
                check(part2(testInput) == 0)
    
                val input = readInput("Day${paddedDay}")
                println(part1(input))
                println(part2(input))
            }
        """.trimIndent())
        File("src/Day${paddedDay}.txt").createNewFile()
        File("src/Day${paddedDay}_test.txt").createNewFile()
    }
}
