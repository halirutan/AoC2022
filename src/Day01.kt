fun main() {
    fun transformInputList(input: List<String>): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        var container = mutableListOf<Int>()
        for (item in input) {
            if (item.isEmpty()) {
                result.add(container)
                container = mutableListOf()
            } else {
                val num = item.toInt()
                container.add(num)
            }
        }
        result.add(container)
        return result
    }


    fun part1(input: List<String>): Int {
        var currentMax = 0
        input
            .map { if (it.isNotBlank()) it.toInt() else 0 }
            .reduce { acc, i ->
                val sum = acc + i
                if (sum > currentMax) {
                    currentMax = sum
                }
                if (i == 0) {
                    0
                } else {
                    acc + i
                }
            }
        return currentMax
    }

    fun part2(input: List<String>): Int {
        val sortedSums = transformInputList(input)
            .map {
                it.sum()
            }
            .sortedDescending()
        return sortedSums[0] + sortedSums[1] + sortedSums[2]
    }


    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24000)
    check(transformInputList(testInput).size == 5)

    println(part2(testInput))


    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
