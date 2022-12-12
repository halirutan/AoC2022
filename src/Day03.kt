import kotlin.streams.toList

class Rucksack(contents: String) {
    private val contents1: IntArray
    private val contents2: IntArray

    init {
        assert(contents.isNotEmpty() && contents.length.mod(2) == 0)
        val chars = contents.chars().toArray() ?: throw Error("Converting string to array failed.")
        contents1 = chars.copyOfRange(0, chars.size.div(2))
        contents2 = chars.copyOfRange(chars.size.div(2), chars.size)
    }

    fun findSharedItems(): List<Int> {
        val result = mutableListOf<Int>()
        val sorted1 = contents1.sorted()
        val sorted2 = contents2.sorted()
        var i = 0
        var j = 0
        while (i < sorted1.size && j < sorted2.size) {
            if (sorted1[i] == sorted2[j]) {
                result.add(sorted1[i])
            }

            if (sorted1[i] < sorted2[j]) {
                i++
            } else {
                j++
            }
        }
        return result
    }
}

fun Int.toRucksackPriority(): Int {
    return if (this in 65..90) {
        this - 65 + 27
    } else {
        this - 97 + 1
    }
}

fun main() {

    fun part1(input: List<String>): Int {
        return input.sumOf {
            Rucksack(it).findSharedItems().toSet().sumOf { item -> item.toRucksackPriority() }
        }
    }

    fun part2(input: List<String>): Int {
        assert(input.size.mod(3) == 0)
        var result = 0
        var i = 0
        while (i < input.size) {
            val r1 = input[i].chars().toList().toSet()
            val r2 = input[i+1].chars().toList().toSet()
            val r3 = input[i+2].chars().toList().toSet()
            i += 3
            val commonItem = r1.intersect(r2).intersect(r3)
            assert(commonItem.size == 1)
            result += commonItem.sum().toRucksackPriority()
        }
        return result
    }

    val testInput = readInput("Day03_test")
    println("Part 1 test: ${part1(testInput)}")
    val realInput = readInput("Day03")
    println("Part 1 real: ${part1(realInput)}")

    println("Part 2 test: ${part2(testInput)}")
    println("Part 2 real: ${part2(realInput)}")

}