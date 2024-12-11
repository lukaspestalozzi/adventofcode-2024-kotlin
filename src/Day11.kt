import java.util.*

class Day11 : AbstractSolver("11", 55312, 0) {

    private data class Input(val case: List<Long>)
    private data class SolvedN(val n: Long, val blinks: Int)

    private val SINGLETON_1 = Collections.singletonList(1L)
    private val CACHE_SOLVE: MutableMap<SolvedN, Long> = mutableMapOf()

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        check(input.size == 1)
        val inputLine = input.first()
        val case = inputLine.split(" ").map { it.toLong() }
        val createdInput = Input(case)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): Long {
        val input = createInput(inputLines).case
        return input.sumOf { applyRulesAndCountStonesCached(it, 25) }
    }

    private fun applyRules(n: Long): List<Long> {
        // rule 1
        if (n == 0L) {
            return SINGLETON_1
        }
        // rule 2
        else if (hasEvenNumberOfDigits(n)) {
            val s = n.toString()
            val middleIdx = s.length / 2
            return listOf(s.substring(0, middleIdx).toLong(), s.substring(middleIdx).toLong())
        }
        // rule 3
        else {
            return Collections.singletonList(n * 2024L)
        }
    }

    private fun hasEvenNumberOfDigits(n: Long): Boolean {
        return n.toString().length % 2 == 0
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines).case
        return input.sumOf { applyRulesAndCountStonesCached(it, 75) }
    }

    private fun applyRulesAndCountStonesCached(n: Long, remainingBlinks: Int): Long {
        if (remainingBlinks == 0) {
            return 1
        }
        val k = SolvedN(n = n, blinks = remainingBlinks)
        if (CACHE_SOLVE.containsKey(k)) {
            return CACHE_SOLVE[k]!!
        }
        val result = applyRules(n).sumOf { applyRulesAndCountStonesCached(it, remainingBlinks - 1) }
        CACHE_SOLVE[k] = result
        return result
    }
}
