data class Valve(val neighbors: List<String>, val flow: Int, val pressureRelease: Int = 0)
data class Player(val currentValve: String, val remainingTime: Int)

// We use these for the stack where we store the search progression
data class OnePlayerGame(val g: GameState, val p1: Player)
data class TwoPlayerGame(val g: GameState, val p1: Player, val p2: Player)

val globalPathStore = mutableMapOf<Set<String>, Int>()

data class ValveOnAction(val target: String)

data class GameState(val valves: Map<String, Valve>) {
    val score = valves.values.sumOf { it.pressureRelease }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Used valves ${valves.values.filter { it.pressureRelease > 0 }.size}:")
        for (valve in valves) {
            if (valve.value.pressureRelease > 0) {
                builder
                    .append(valve.key)
                    .append(" (${valve.value.flow} -> ${valve.value.pressureRelease}) ")
            }
        }
        return builder.toString()
    }

    fun getPossibleActions(): List<ValveOnAction> {
        val result = mutableListOf<ValveOnAction>()
        for (v in valves.filter { it.value.flow > 0 && it.value.pressureRelease == 0 }) {
            result.add(ValveOnAction(v.key))
        }
        return result
    }

    fun doAction(act: ValveOnAction, player: Player): Pair<GameState, Player> {
        assert(player.remainingTime > 0)
        val vToTurnOn = valves[act.target] ?: throw Error("Valve ${act.target} does not exist")
        assert(vToTurnOn.flow > 0)
        val timeToTravel = findShortestPath(player.currentValve, act.target)
        val newValves = valves.toMutableMap()
        val remainingTimeAfterAction = player.remainingTime - timeToTravel - 1
        newValves[act.target] = Valve(
            vToTurnOn.neighbors,
            vToTurnOn.flow,
            remainingTimeAfterAction * vToTurnOn.flow,
        )
        return Pair(GameState(newValves), Player(act.target, remainingTimeAfterAction))
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

    fun getUnusedValveValue(): Int {
        return valves.values.filter { it.pressureRelease == 0 && it.flow > 0 }.sumOf { it.flow }
    }
}

fun parseGameState(input: List<String>): GameState {
    val valves = mutableMapOf<String, Valve>()
    for (line in input) {
        val parts = line.split(", ", " ")
        val name = parts[1]
        val flow = parts[4].split("=", ";")[1].toInt()
        val neighbors = parts.subList(9, parts.size)
        valves[name] = Valve(neighbors, flow)
    }
    return GameState(valves)
}

/**
 * A quick test if the shortest path works
 */
@Suppress("unused")
fun checkShortestPath(input: List<String>) {
    val game = parseGameState(input)
    println(game.findShortestPath("AA", "II"))
    println(game.findShortestPath("AA", "HH"))
    println(game.findShortestPath("AA", "JJ"))
    println(game.findShortestPath("CC", "AA"))
}

fun main() {
    val smallInput = readInput("Day16_test")
    val realInput = readInput("Day16")

    fun part1(input: List<String>) {
        val remainingTime = 30
        val initialValve = "AA"
        val me = Player(initialValve, remainingTime)
        val game = parseGameState(input)
        val stack = ArrayDeque<OnePlayerGame>()
        stack.add(OnePlayerGame(game, me))
        var maxScore = 0
        while (stack.isNotEmpty()) {
            val (g, p) = stack.removeFirst()
            if (p.remainingTime < 0) {
                continue
            }
            val score = g.score
            if (score > maxScore) {
                maxScore = score
                print(".")
            }

            g.getPossibleActions().map { g.doAction(it, p) }.forEach { stack.addFirst(OnePlayerGame(it.first, it.second)) }
        }
        println()
        println("Best Score Part 1: $maxScore")

    }

    /**
     * For part 2 we do the same as for part 1, but we just alternate two players between each move.
     */
    fun part2(input: List<String>) {
        val remainingTime = 26
        val initialValve = "AA"


            val me = Player(initialValve, remainingTime)
            val elephant = Player(initialValve, remainingTime)
            val game = parseGameState(input)

            val stack = ArrayDeque<TwoPlayerGame>()
            stack.add(TwoPlayerGame(game, me, elephant))
            var maxScore = 0

            var iter = 0
            while (stack.isNotEmpty()) {
                val (g, p1, p2) = stack.removeFirst()
                val score = g.score

                if (p1.remainingTime < 0 || p2.remainingTime < 0) {
                    continue
                }

                // We over-estimate what additional score we can get from the remaining closed valves.
                // If it can't get bigger than our current max score, we can prune this whole subtree.
                val remainingValueEstimate = g.getUnusedValveValue() * (p1.remainingTime+p2.remainingTime)/2
                if (score + remainingValueEstimate < maxScore) {
                    continue
                }


                if (score > maxScore) {
                    maxScore = score
                    print(".")
                }

                // Alternating moves between the player and the elephant
                if (iter.mod(2) == 0) {
                    g
                        .getPossibleActions()
                        .map { g.doAction(it, p1) }
                        .forEach { stack.addFirst(TwoPlayerGame(it.first, it.second, p2)) }

                }else{
                    g
                        .getPossibleActions()
                        .map { g.doAction(it, p2) }
                        .forEach { stack.addFirst(TwoPlayerGame(it.first, p1, it.second)) }
                }
                iter++
            }
        println()
        println("Best Score Part 2: $maxScore")

    }

    println("Part 1 Small Input")
    part1(smallInput)
    println()
    println()

    println("Part 1 Real Input")
    part1(realInput)
    println()
    println()

    println("Part 2 Small Input")
    part2(smallInput)
    println()
    println()

    println("Part 2 Real Input")
    part2(realInput)
    println()
    println()
}