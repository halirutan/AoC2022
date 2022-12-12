fun getPairOfRange(s: String): Pair<IntRange, IntRange> {
    val split = s.split(",").map {
        val lowHigh = it.split("-")
        IntRange(lowHigh[0].toInt(), lowHigh[1].toInt())
    }
    return Pair(split[0], split[1])
}

fun IntRange.encloses(other: IntRange): Boolean {
    return this.first <= other.first && this.last >= other.last
}

fun main() {
    fun part1(input: List<String>): Int {
        var result = 0
        input.map { getPairOfRange(it) }.forEach {
            if (it.first.encloses(it.second) || it.second.encloses(it.first)) {
                result++
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        var result = 0
        input.map { getPairOfRange(it) }.forEach {
            if (it.first.contains(it.second.first) || it.first.contains(it.second.last) ||
                it.second.contains(it.first.first) || it.second.contains(it.first.last)) {
                result++
            }
        }
        return result
    }


    val testInput = readInput("Day04_test")
    println("Part 1 test: ${part1(testInput)}")

    val realInput = readInput("Day04")
    println("Part 1 real: ${part1(realInput)}")

    println("Part 2 test: ${part2(testInput)}")
    println("Part 2 real: ${part2(realInput)}")


}