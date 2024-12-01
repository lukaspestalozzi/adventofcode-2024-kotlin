
class Day02 : AbstractSolver("02", TODO(), TODO()) {

    private data class Input(val list1: List<Int>, val list2: List<Int>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val inRegex = """(\d+)\s+(\d+)""".toRegex()
        TODO()
        val list1: MutableList<Int> = mutableListOf()
        val list2: MutableList<Int> = mutableListOf()
        for (line: String in input) {
            val (i1, i2) = inRegex.matchEntire(line)?.destructured ?: throw IllegalArgumentException(line)
            list1.add(i1.toInt())
            list2.add(i2.toInt())
        }
        val createdInput = Input(list1, list2)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(input: List<String>): Int {
        val (list1, list2) = createInput(input)

        val solution = TODO()

        return solution
    }

    override fun solvePart2(input: List<String>): Int {
        val (list1, list2) = createInput(input)

        val solution = TODO()

        return solution
    }
}