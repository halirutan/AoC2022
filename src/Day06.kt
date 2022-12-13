import kotlin.collections.ArrayDeque

class BufferFold(private val bufferSize: Int) {
    private val data = ArrayDeque<Char>(initialCapacity = bufferSize)
    var processCount = 0
        private set

    fun process(c: Char): Boolean {
        if (processCount < bufferSize) {
            data.addLast(c)
        } else {
            data.removeFirst()
            data.addLast(c)
        }
        processCount++
        return data.size == bufferSize && data.toSet().size == bufferSize
    }

}

fun main() {
    fun solution(input: List<String>, bufferSize: Int): Int {
        assert(input.size == 1)
        val buffer = BufferFold(bufferSize)
        input[0].forEach {
            if (buffer.process(it)) {
                return buffer.processCount
            }
        }
        throw Error("No unique packet could be found.")
    }

    val testInput = readInput("Day06_test")
    val realInput = readInput("Day06")
    println("Part 1 test: ${solution(testInput, 4)}")
    println("Part 1 real: ${solution(realInput, 4)}")

    println("Part 2 test: ${solution(testInput, 14)}")
    println("Part 2 real: ${solution(realInput, 14)}")

}