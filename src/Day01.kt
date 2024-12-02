import kotlin.math.abs

class Day01 : AbstractSolver("01", 11, 31) {

   private data class Input(val list1: List<Int>, val list2: List<Int>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val inRegex = """(\d+)\s+(\d+)""".toRegex()
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

    override fun solvePart1(inputLines: List<String>): Int {
        val (list1, list2) = createInput(inputLines)
        val sorted1 = list1.sorted()
        val sorted2 = list2.sorted()

        val solution = sorted1.zip(sorted2).sumOf { (i1, i2) -> abs(i1 - i2) }

        return solution
    }

    override fun solvePart2(inputLines: List<String>): Int {
        val (list1, list2) = createInput(inputLines)
        val groupedList2 = list2.groupBy { i -> i }
        val solution = list1.sumOf { i1 -> (groupedList2[i1]?.count() ?: 0) * i1 }

        return solution
    }
}