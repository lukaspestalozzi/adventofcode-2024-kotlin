import kotlin.math.abs

class Day02 : AbstractSolver("02", 2, 4) {

    private data class Case(val list: List<Int>)
    private data class Input(val cases: List<Case>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val cases: MutableList<Case> = mutableListOf()
        for (line: String in input) {
            val case = Case(line.split(" ").map { s -> s.toInt() })
            cases.add(case)
        }
        val createdInput = Input(cases)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): Int {
        val input = createInput(inputLines)
        var solution = 0
        for (case in input.cases) {
            if (isSafePart1(case)) {
                solution++
            }
        }

        return solution
    }

    private fun isSafePart1(case: Case): Boolean {
        var curr = case.list.first()
        val increasing = curr < case.list[1]
        for (i in case.list.subList(1, case.list.count())) {
            if (!isSafe(curr, i, increasing)) {
                return false
            }
            curr = i
        }
        return true
    }

    override fun solvePart2(inputLines: List<String>): Int {
        val input = createInput(inputLines)
        var solution = 0
        for (case in input.cases) {
            if (isSafePart2(case.list, 0, 1, true, increasing = true)) {
                solution++
            } else if (isSafePart2(case.list, 0, 1, true, increasing = false)) {
                solution++
            }
        }

        return solution
    }

    private fun isSafePart2(
        list: List<Int>, lastIdx: Int, currIdx: Int, jokerRemaining: Boolean, increasing: Boolean
    ): Boolean {
        if (currIdx >= list.count()) {
            return true
        } else if (lastIdx < 0) {
            return isSafePart2(list, currIdx, currIdx + 1, jokerRemaining, increasing)
        }

        if (isSafe(list[lastIdx], list[currIdx], increasing)) {
            return isSafePart2(list, currIdx, currIdx + 1, jokerRemaining, increasing)
        } else if (jokerRemaining) {
            val deletingLast = isSafePart2(list, lastIdx - 1, currIdx, false, increasing)
            val deletingCurr = isSafePart2(list, lastIdx, currIdx + 1, false, increasing)
            return deletingLast || deletingCurr
        } else {
            return false
        }
    }

    private fun isSafe(i1: Int, i2: Int, increasing: Boolean): Boolean {
        if (increasing && i1 >= i2) {
            return false
        }
        if (!increasing && i1 <= i2) {
            return false
        }
        val diff = abs(i1 - i2)
        return !(diff < 1 || diff > 3)
    }
}