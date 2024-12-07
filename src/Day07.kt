class Day07 : AbstractSolver("07", 3749, 0) {

    companion object {
        const val OBSTACLE = '#'
        const val FLOOR = '.'
        const val OUTSIDE = '-'
        const val START = '^'
    }

    private data class Case(val result: Long, val list: List<Long>)
    private data class Input(val cases: List<Case>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val cases: MutableList<Case> = mutableListOf()
        for (line: String in input) {
            val split1 = line.split(": ")

            val case = Case(result = split1[0].toLong(), split1[1].split(" ").map { s -> s.toLong() })
            cases.add(case)
        }
        val createdInput = Input(cases)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        var solution: Long = 0
        for (case in input.cases) {
            solution += solveCase(case)
        }
        return solution
    }

    private fun solveCase(case: Case): Long {
        return solve(case.result, 0, currIdx = 0, remaining = case.list)
    }

    private fun solve(expectedResult: Long, acc: Long, currIdx: Int, remaining: List<Long>): Long {
        if (currIdx >= remaining.count()) {
            return if (acc == expectedResult) acc else 0
        }
        if (acc > expectedResult) {
            return 0
        }
        val nextNbr = remaining[currIdx]
        val add = solve(expectedResult, acc + nextNbr, currIdx = currIdx + 1, remaining = remaining)
        if (add != 0L) {
            return add
        }
        val mul = solve(expectedResult, acc * nextNbr, currIdx = currIdx + 1, remaining = remaining)
        return mul
    }


    override fun solvePart2(inputLines: List<String>): Number {
        val solution = 0L

        return solution
    }
}

