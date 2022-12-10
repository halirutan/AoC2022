fun main() {
    val rules = mapOf(
        Pair("A", "X") to 1 + 3,
        Pair("A", "Y") to 2 + 6,
        Pair("A", "Z") to 3 + 0,
        Pair("B", "X") to 1 + 0,
        Pair("B", "Y") to 2 + 3,
        Pair("B", "Z") to 3 + 6,
        Pair("C", "X") to 1 + 6,
        Pair("C", "Y") to 2 + 0,
        Pair("C", "Z") to 3 + 3,
    )

    fun part1(input: List<String>): Int {
        return input.sumOf {
            val split = it.split(" ")
            rules[Pair(split[0], split[1])] ?: throw Error("That should not happen")
        }
    }

    val translate = mapOf(
        "A" to "X",
        "B" to "Y",
        "C" to "Z"
    )

    fun fillAnswer(pair: List<String>): Pair<String, String> {
        val move = pair[0]
        return when (pair[1]) {
            "X" -> when (move) {
                "A" -> Pair(move, "Z")
                "B" -> Pair(move, "X")
                "C" -> Pair(move, "Y")
                else -> throw Error("That should not happen")
            }

            "Y" -> Pair(move, translate[move]!!)
            "Z" -> when (move) {
                "A" -> Pair(move, "Y")
                "B" -> Pair(move, "Z")
                "C" -> Pair(move, "X")
                else -> throw Error("That also should not happen")
            }
            else -> throw Error("Man.. it just keeps coming, right?")
        }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf {
            val split = it.split(" ")
            rules[fillAnswer(split)]?: throw Error("Damn Wilson")
        }
    }

    val testInput = readInput("Day02_test")
    val realInput = readInput("Day02")
    println("Part 1 test input: ${part1(testInput)}")
    println("Part 1 real input: ${part1(realInput)}")
    println("Part 2 test input: ${part2(testInput)}")
    println("Part 2 real input: ${part2(realInput)}")
}