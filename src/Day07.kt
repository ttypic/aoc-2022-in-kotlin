fun main() {

    fun readFileSystem(input: List<String>): Directory {
        val root = Directory(name = "/", children = mutableListOf())
        var currentFolder = root
        input.drop(1).forEach { command ->
            if (command.startsWith("\$ cd")) {
                val folder = command.split(" ")[2]
                currentFolder = if (currentFolder.children.any { it.name == folder }) {
                    currentFolder.children.find { it.name == folder } as Directory
                } else if (folder == "..") {
                    currentFolder.parent!!
                } else {
                    val newFolder = Directory(parent = currentFolder, name = folder, children = mutableListOf())
                    currentFolder.children.add(newFolder)
                    newFolder
                }
            } else if (!command.startsWith("\$")) {
                val split = command.split(" ")
                if (split[0] == "dir") {
                    val newFolder = Directory(parent = currentFolder, name = split[1], children = mutableListOf())
                    currentFolder.children.add(newFolder)
                } else {
                    val newFile = File(parent = currentFolder, name = split[1], size = split[0].toInt())
                    currentFolder.children.add(newFile)
                }
            }
        }
        return root
    }

    fun findSmallFoldersToDelete(folder: Directory): List<Directory> {
        val smallDirectories = if (folder.size < 100_000) listOf(folder) else listOf()
        return if (folder.children.any { it is Directory }) {
            folder.children.filterIsInstance<Directory>().flatMap { findSmallFoldersToDelete(it) } + smallDirectories
        } else {
            smallDirectories
        }
    }


    fun findBigFoldersToDelete(folder: Directory, atLeastSize: Int): List<Directory> {
        val bigDirectories = if (folder.size >= atLeastSize) listOf(folder) else listOf()
        return if (folder.children.any { it is Directory }) {
            folder.children.filterIsInstance<Directory>().flatMap { findBigFoldersToDelete(it, atLeastSize) } + bigDirectories
        } else {
            bigDirectories
        }
    }


    fun part1(input: List<String>): Int {
        val root = readFileSystem(input)
        val folders = findSmallFoldersToDelete(root)
        return  folders.sumOf { it.size }
    }

    fun part2(input: List<String>): Int {
        val root = readFileSystem(input)
        val needToFreeSize = 30000000 - (70000000 - root.size)
        return findBigFoldersToDelete(root, needToFreeSize).minOf { it.size }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

interface Path {
    val parent: Directory?
    val name: String
    val size: Int
}

class Directory(override val parent: Directory? = null, override val name: String, val children: MutableList<Path>): Path {
    override val size: Int
        get() = children.sumOf { it.size }
}

data class File(override val parent: Directory, override val name: String, override val size: Int): Path
