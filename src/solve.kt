fun main() {
    val solver = Day01()

    val logger = solver.logger

    fun solveAndPrint(
        solver: AbstractSolver, partNr: Int, testInput: Boolean = false, function: (List<String>) -> Int
    ): Int {
        val dayPartString = "Day ${solver.dayNr}; Part ${partNr}${if (testInput) " on Test-Input" else ""}"
        logger.info("Solving $dayPartString ... ")
        val input = solver.readInput(testInput = testInput)
        val solution = function(input)
        logger.info("Solved  $dayPartString: $solution")
        return solution
    }

    // Test Input
    val solTest1 = solveAndPrint(solver, 1, testInput = true, function = solver::solvePart1)
    check(solTest1 == solver.expectedTestPart1)
    val solTest2 = solveAndPrint(solver, 2, testInput = true, function = solver::solvePart2)
    check(solTest2 == solver.expectedTestPart2)

    // Solve both parts
    println("---------------")
    solveAndPrint(solver, 1, function = solver::solvePart1)
    solveAndPrint(solver, 2, function = solver::solvePart2)
}