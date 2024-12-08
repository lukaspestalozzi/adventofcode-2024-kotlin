class DayCase/*TODO add nbr*/ : AbstractSolver(TODO("add nbr as string"), 0, 0) {

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
        return solution
    }
    
    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution
    }
}

