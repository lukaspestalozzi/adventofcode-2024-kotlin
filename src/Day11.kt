import java.util.*

class Day11 : AbstractSolver("11", 55312, 0) {

    private data class Input(val case: List<Long>)

    private val SINGLETON_1 = Collections.singletonList(1L)

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
        return input.sumOf { applyRulesAndCountStones(it, 25) }
    }

    private fun applyRulesAndCountStones(n: Long, remainingBlinks: Int): Long {
        if (remainingBlinks == 0) {
            return 1
        }
        return applyRules(n).sumOf { applyRulesAndCountStones(it, remainingBlinks - 1) }
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
        return n.toString().length % 2 == 0 // may be implemented more efficient
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines).case
        var solution: Long = input.sumOf { applyRulesAndCountStones(it, 75) }
        return solution
    }

}
