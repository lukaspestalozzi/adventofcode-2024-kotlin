class Day13 : AbstractSolver("13", 480, 0) {

    companion object {
        const val COST_A = 3
        const val COST_B = 1
        const val NO_SOLUTION = Int.MAX_VALUE
    }

    private data class Pos(val x: Int, val y: Int) {
        fun move(pos: Pos): Pos {
            return Pos(x + pos.x, y + pos.y)
        }
    }

    private data class Case(val a: Pos, val b: Pos, val prize: Pos)
    private data class Input(val cases: List<Case>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val cases: MutableList<Case> = mutableListOf()
        var idx = 0
        while (idx < input.size) {
            val caseList = input.slice(idx..idx + 2)
            idx += 4
            val case = parseCase(caseList)
            cases.add(case)
        }
        val createdInput = Input(cases)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    private fun parseCase(lines: List<String>): Case {
        check(lines.size == 3)
        val buttonRegex = """Button.* X\+(\d+),\s+Y\+(\d+)""".toRegex()
        val prizeRegex = """Prize: X=(\d+),\s+Y=(\d+)""".toRegex()
        val (ax, ay) = buttonRegex.matchEntire(lines[0])?.destructured ?: throw IllegalArgumentException(lines[0])
        val (bx, by) = buttonRegex.matchEntire(lines[1])?.destructured ?: throw IllegalArgumentException(lines[1])
        val (px, py) = prizeRegex.matchEntire(lines[2])?.destructured ?: throw IllegalArgumentException(lines[2])
        return Case(Pos(ax.toInt(), ay.toInt()), Pos(bx.toInt(), by.toInt()), Pos(px.toInt(), py.toInt()))
    }

    override fun solvePart1(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        var solution: Long = 0
        for(case in input.cases){
            solution += solveCase(case)
        }
        return solution
    }

    private fun solveCase(case: Case): Int {
        val sol = solveCase(currPos = Pos(0, 0), goal = case.prize, buttonA = case.a, buttonB = case.b, cutoff = Int.MAX_VALUE)
        logger.info { "$case -> $sol" }
        return if(sol > NO_SOLUTION*0.9) 0 else sol
    }

    private fun solveCase(currPos: Pos, goal: Pos, buttonA: Pos, buttonB: Pos, cutoff: Int): Int {
        if (goal.x < currPos.x || goal.y < currPos.y) {
//            logger.info { "too high $currPos > $goal" }
            return NO_SOLUTION
        }
        if (goal.x == currPos.x || goal.y == currPos.y) {
            return 0
        }
        val takeA = COST_A + solveCase(currPos.move(buttonA), goal, buttonA, buttonB, cutoff=cutoff)
        val takeB = COST_B + solveCase(currPos.move(buttonB), goal, buttonA, buttonB, cutoff=if(takeA < cutoff) takeA else cutoff)
        return if(takeA < takeB) takeA else takeB
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution
    }
}

