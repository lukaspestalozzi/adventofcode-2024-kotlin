fun main() {
    val solver = Day22()

    val logger = solver.logger

    fun solveAndPrint(solver: AbstractSolver, partNr: Int, testInput: Boolean = false, function: (List<String>) -> String): String {
        val dayPartString = "Day ${solver.dayNr}; Part ${partNr}${if (testInput) " on Test-Input" else ""}"
        logger.info("Solving $dayPartString ... ")
        val input = solver.readInput(testInput = testInput)
        val solution = function(input)
        logger.info("Solved  $dayPartString: $solution")
        return solution.toString()
    }

    // Test Input
    // Part 1
    if (solver.expectedTestPart1.isNotBlank()) {
        val solTest1 = solveAndPrint(solver, 1, testInput = true, function = solver::solvePart1)
        check(solTest1 == solver.expectedTestPart1) { "$solTest1 == ${solver.expectedTestPart1}" }
    } else {
        logger.info { "Skipping Test 1, solver.expectedTestPart1 ='${solver.expectedTestPart1}' (blank)" }
    }

    // Part 2
    if (solver.expectedTestPart2.isNotBlank()) {
        val solTest2 = solveAndPrint(solver, 2, testInput = true, function = solver::solvePart2)
        check(solTest2 == solver.expectedTestPart2) { "$solTest2 == ${solver.expectedTestPart2}" }
    } else {
        logger.info { "Skipping Test 2, solver.expectedTestPart2 ='${solver.expectedTestPart2}' (blank)" }
    }

    // Solve both parts
    println("---------------")
    solveAndPrint(solver, 1, function = solver::solvePart1)
    solveAndPrint(solver, 2, function = solver::solvePart2)
}
