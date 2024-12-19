class DayOneCase/*TODO add nbr*/ : AbstractSolver(TODO("add nbr as string"), "", "") {

    private data class Input(val case: List<Int>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        check(input.size == 1)
        val inputLine = input.first()
        val case = inputLine.split(" ").map { it.toInt() }
        val createdInput = Input(case)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): String {
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution.toString()
    }
    
    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution.toString()
    }
}

