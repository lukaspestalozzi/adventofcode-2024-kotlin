class Day13 : AbstractSolver("13", "480", "0") {

    companion object {
        const val COST_A = 3
        const val COST_B = 1
    }

    private data class Pos(val x: Long, val y: Long) {
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
        return Case(Pos(ax.toLong(), ay.toLong()), Pos(bx.toLong(), by.toLong()), Pos(px.toLong(), py.toLong()))
    }

    override fun solvePart1(inputLines: List<String>): String {
        val input = createInput(inputLines)
        var solution: Double = 0.0
        for (case in input.cases) {
            solution += solveCase(case)
        }
        return solution.toString()
    }

    private fun solveCase(case: Case): Double {
        val ax = case.a.x
        val ay = case.a.y
        val bx = case.b.x
        val by = case.b.y
        val px = case.prize.x.toDouble()
        val py = case.prize.y.toDouble()

        if (bx * ay != ax * by) {
            val numA: Double = (py * bx - px * by) / (bx * ay - ax * by)
            val numB: Double = (py * ax - px * ay) / (ax * by - bx * ay)
            if (numA < 0 || numB < 0) {
                logger.info { "not possible because negative" }
                return 0.0
            }
            if (numA > numA.toLong() || numB > numB.toLong()) {
                logger.info { "not possible because double" }
                return 0.0
            }
            logger.info { "numA=$numA ; numB=$numB" }
            val sol = COST_A * numA + COST_B * numB
            return sol
        } else {
            throw IllegalArgumentException("$case")
        }
    }

    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines)
        val scale = 10000000000000
        var solution: Double = 0.0
        for (case in input.cases) {
            val bigCase = Case(case.a, case.b, prize = Pos(case.prize.x+scale, case.prize.y+scale))
            solution += solveCase(bigCase)
        }
        return solution.toString()
    }
}