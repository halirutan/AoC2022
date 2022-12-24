data class File(val name: String, val size: Int)

class Directory(private val name: String) {
    var parent: Directory? = null
    private var children = mutableListOf<Directory>()
    private val files = mutableSetOf<File>()
    var size: Int = -1

    fun getDirectorySize(): Int {
        if (size == -1) {
            size = 0
            for (d in children) {
                size += d.getDirectorySize()
            }
            for (f in files) {
                size += f.size
            }
        }
        return size
    }

    fun getDirectoriesWithMaxSize(maxSize: Int): List<Directory> {
        getDirectorySize()
        val result = mutableListOf<Directory>()
        val stack = mutableListOf<Directory>()
        stack.addAll(children)
        while (stack.size > 0) {
            val current = stack[0]
            if (current.size <= maxSize) {
                result.add(current)
            }
            stack.addAll(current.children)
            stack.removeFirst()
        }
        return result
    }

    fun getDirectoriesWithMinSize(minSize: Int): List<Directory> {
        getDirectorySize()
        val result = mutableListOf<Directory>()
        val stack = mutableListOf<Directory>()
        stack.addAll(children)
        while (stack.size > 0) {
            val current = stack[0]
            if (current.size >= minSize) {
                result.add(current)
            }
            stack.addAll(current.children)
            stack.removeFirst()
        }
        return result
    }

    fun createDirectory(name: String) {
        val dir = Directory(name)
        dir.parent = this
        this.children.add(dir)
    }

    fun addFile(name: String, size: Int) {
        files.add(File(name, size))
    }

    fun changeDirectory(d: String): Directory {
        val dir = children.find { it.name == d }
        return if (dir is Directory) {
            dir
        } else {
            throw Error("Could not change into directory $d")
        }
    }

    /**
     * I like this, although it's not needed, so we keep it in.
     *
     * @return Printable string of the directory with all subdirectories.
     */
    @Suppress("unused")
    fun listDirectory(): String {
        val builder = StringBuilder()
        listDirectoryHelper(builder, 0)
        return builder.toString()
    }

    /**
     * Recursively visit all directories and create a printable string for each.
     */
    private fun listDirectoryHelper(builder: StringBuilder, indent: Int) {
        val spaces = " ".repeat(indent)
        for (directory in children) {
            builder
                .append(spaces)
                .append("dir ")
                .append(directory.name)
                .append(" ")
                .append(directory.getDirectorySize())
                .appendLine()
            directory.listDirectoryHelper(builder, indent + 2)
        }
        for (file in files) {
            builder
                .append(spaces)
                .append(file.size)
                .append(" ")
                .append(file.name)
                .appendLine()
        }
    }

    override fun toString(): String {
        return "$name ($size)"
    }
}

class FileSystem {
    val root = Directory("/")
    private var currentDir = root

    fun parseInput(input: List<String>) {
        var linePointer = 0
        while (linePointer < input.size) {
            linePointer += parseCmd(input, linePointer)
        }
    }

    private fun parseCmd(input: List<String>, line: Int): Int {
        val parts = input[line].split(" ")
        // We expect always something like "$ cmd possibleOption"
        if (parts.size < 2 || parts[0] != "$") throw Error("Expected command but got: ${input[line]}")
        return when (parts[1]) {
            "cd" -> {
                if (parts.size != 3) throw Error("cd command expects an argument")
                cmdCd(parts[2])
            }

            "ls" -> cmdLs(input, line)
            else -> throw Error("Unknown command: ${input[line]}")
        }
    }

    /**
     * Reads in the output that shows the directory content and stores it in the current directory.
     */
    private fun cmdLs(input: List<String>, linePointer: Int): Int {
        var line = linePointer + 1
        while (line < input.size) {
            val parts = input[line].split(" ")
            if (parts[0] == "$") break
            if (parts[0] == "dir") {
                currentDir.createDirectory(parts[1])
            } else {
                val size = parts[0].toInt()
                currentDir.addFile(parts[1], size)
            }
            line++
        }
        return line - linePointer
    }

    // let's assume we can only change into dirs that have already been created with ls
    private fun cmdCd(dir: String): Int {
        var dirs = dir.split("/")
        if (dirs[0].isEmpty()) {
            currentDir = root
            dirs = dirs.drop(1)
        }
        for (d in dirs.filterNot { it.isEmpty() }) {
            currentDir = when (d) {
                ".." -> {
                    val parent = currentDir.parent
                    if (parent is Directory) {
                        parent
                    } else {
                        throw Error("Try to navigate to parent but it doesn't exist")
                    }
                }

                else -> {
                    currentDir.changeDirectory(d)
                }
            }
        }
        return 1
    }
}

fun main() {
    val realInput = readInput("Day07")


    fun part1(input: List<String>) {
        val fs = FileSystem()
        fs.parseInput(input)
//        println(fs.root.listDirectory())
        val result = fs.root.getDirectoriesWithMaxSize(100000)
        println("Result Part 1: ${result.sumOf { it.size }}")
    }

    fun part2(input: List<String>) {
        val fsSize = 70_000_000
        val requiredSize = 30_000_000
        val fs = FileSystem()
        fs.parseInput(input)
        val rootSize = fs.root.getDirectorySize()
        val sizeToBeFreed = requiredSize - (fsSize - rootSize)
        println("Root size $rootSize, currently free ${fsSize - rootSize}, required to free $sizeToBeFreed")
        val result = fs.root.getDirectoriesWithMinSize(sizeToBeFreed)
        println("Result Part 2: ${result.minOf { it.size }}")
    }

    part1(realInput)
    println()
    part2(realInput)


}