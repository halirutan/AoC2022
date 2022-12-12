class Cargo(input: List<String>) {
    data class Move(val quantity: Int, val from: Int, val to: Int)

    private val crates: List<MutableList<Char>> = List(9, init= { mutableListOf() })
    private val moves: MutableList<Move>

    init {
        // Read in the cargo on all crates
        for (s in input) {
            if(s[1] == '1') break
            val chars =s.toCharArray()
            for ((crateNum, i) in (1 until chars.size step 4).withIndex()) {
                if (chars[i] != ' ') {
                    crates[crateNum].add(chars[i])
                }
            }
        }
        // Reverse so that bottom crates are at the beginning
        for (crate in crates) {
            crate.reverse()
        }

        // Read the moves
        moves = mutableListOf()
        for (s in input) {
            if (s.startsWith("move")) {
                val split = s.split(" ")
                moves.add(Move(split[1].toInt(), split[3].toInt()-1, split[5].toInt()-1))
            }
        }
    }

    private fun applyMove(m: Move) {
        for (i in 1..m.quantity) {
            assert(crates[m.from].isNotEmpty())
            crates[m.to].add(crates[m.from].last())
            crates[m.from].removeLast()
        }
    }

    private fun applyMove2(m: Move) {
        val from = crates[m.from]
        assert(from.size >= m.quantity)
        val toMove = from.subList(from.size - m.quantity, from.size)
        crates[m.to].addAll(toMove)
        for (i in 1..m.quantity) {
            crates[m.from].removeLast()
        }
    }

    fun doRearranging() {
        for (m in moves) {
            applyMove(m)
        }
    }

    fun doRearranging2() {
        for (m in moves) {
            applyMove2(m)
        }
    }

    fun printCrates() {
        for (c in crates) {
            for (i in c) {
                print("[$i] ")
            }
            println()
        }
    }

    fun printMoves() {
        for (m in moves) {
            println(m)
        }
    }

    fun getTopCrates(): String {
        val builder = StringBuilder()
        for (c in crates) {
            if (c.isNotEmpty()) {
                builder.append(c.last())
            }
        }
        return builder.toString()
    }
}

fun main() {
    val testInput = readInput("Day05_test")
    val realInput = readInput("Day05")
    val c = Cargo(realInput)
    c.doRearranging()
    c.printCrates()
    println(c.getTopCrates())

    val c2 = Cargo(realInput)
    c2.doRearranging2()
    c2.printCrates()
    println(c2.getTopCrates())
}