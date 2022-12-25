data class Valve(val neighbors: List<String>, val flow: Int, val pressureRelease: Int = 0)

val globalPathStore = mutableMapOf<Set<String>, Int>()

data class TurnOnAction(val target: String) {
    override fun toString(): String {
        return "${this::class.simpleName}($target)"
    }
}

data class GameState(val currentValve: String, val remainingTime: Int, val valves: Map<String, Valve>) {
    val score = valves.values.sumOf { it.pressureRelease }

//    override fun toString(): String {
//        val builder = StringBuilder()
//        for (valve in valves) {
//            builder
//                .append("Valve ")
//                .append(valve.key)
//                .append(" has flow rate=")
//                .append(valve.value.flow)
//                .append("; tunnels lead to valves ")
//                .append(valve.value.neighbors.toString())
//                .appendLine()
//        }
//        return builder.toString()
//    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("At \"$currentValve\" ")
        for (valve in valves) {
            if (valve.value.pressureRelease > 0) {
                builder
                    .append(valve.key)
                    .append(" (${valve.value.flow} -> ${valve.value.pressureRelease}) ")
            }
        }
        return builder.toString()
    }

    fun getPossibleActions(): List<TurnOnAction> {
        val result = mutableListOf<TurnOnAction>()
        for (v in valves.filter { it.value.flow > 0 && it.value.pressureRelease == 0 }) {
            result.add(TurnOnAction(v.key))
        }
        return result
    }

    fun doAction(act: TurnOnAction): GameState {
        assert(remainingTime > 0)
        val vToTurnOn = valves[act.target] ?: throw Error("Valve ${act.target} does not exist")
        assert(vToTurnOn.flow > 0)
        val timeToTravel = findShortestPath(currentValve, act.target)
        val newValves = valves.toMutableMap()
        val remainingTimeAfterAction = remainingTime - timeToTravel - 1
        newValves[act.target] = Valve(
            vToTurnOn.neighbors,
            vToTurnOn.flow,
            remainingTimeAfterAction * vToTurnOn.flow,
        )
        return GameState(act.target, remainingTimeAfterAction, newValves)
    }

    /**
     * @return The time it takes to navigate from valve1 to valve2
     */
    fun findShortestPath(valve1: String, valve2: String): Int {
        if (valve1 == valve2) {
            return 0
        }
        val cache = globalPathStore[setOf(valve1, valve2)]
        if (cache is Int) {
            return cache
        }

        val q = ArrayDeque<Pair<String, Int>>()
        val visited = mutableSetOf(valve1)
        q.add(Pair(valve1, 0))
        while (q.isNotEmpty()) {
            val current = q.removeFirst()
            val neighbors = valves[current.first]!!.neighbors.filterNot { visited.contains(it) }
            for (neighbor in neighbors) {
                val timeUsed = current.second + 1
                if (neighbor == valve2) {
                    globalPathStore[setOf(valve1, valve2)] = timeUsed
                    return timeUsed
                }
                q.add(Pair(neighbor, timeUsed))
            }
        }
        throw Error("Could not find path from $valve1 to $valve2")
    }
}

fun parseGameState(input: List<String>, initialValve: String, remainingTime: Int): GameState {
    val valves = mutableMapOf<String, Valve>()
    for (line in input) {
        val parts = line.split(", ", " ")
        val name = parts[1]
        val flow = parts[4].split("=", ";")[1].toInt()
        val neighbors = parts.subList(9, parts.size)
        valves[name] = Valve(neighbors, flow)
    }
    return GameState(initialValve, remainingTime, valves)
}

/**
 * A quick test if the shortest path works
 */
fun checkShortestPath(input: List<String>) {
    val remainingTime = 30
    val initialValve = "AA"
    val game = parseGameState(input, initialValve, remainingTime)
    println(game.findShortestPath("AA", "II"))
    println(game.findShortestPath("AA", "HH"))
    println(game.findShortestPath("AA", "JJ"))
    println(game.findShortestPath("CC", "AA"))
}

fun main() {
    print("What???")
    val smallInput = readInput("Day16_test")
    val realInput = readInput("Day16")

    fun part1(input: List<String>) {
        val remainingTime = 30
        val initialValve = "AA"
        val game = parseGameState(input, initialValve, remainingTime)
        val stack = ArrayDeque<GameState>(1000)
        stack.add(game)
        var maxScore = 0
        while (stack.isNotEmpty()) {
            val g = stack.removeFirst()
            if (g.remainingTime < 0) {
                continue
            }
            val score = g.score
            if (score > maxScore) {
                maxScore = score
                println(g)
                println("New High Score: $score (stack size ${stack.size})")

            }

            stack.addAll(g.getPossibleActions().map { g.doAction(it) })
        }
    }

//    checkShortestPath(smallInput)
    part1(realInput)

}