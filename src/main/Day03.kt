class Day03 : AbstractSolver("03", "161", "48") {

    private data class Input(val case: String)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        return Input(input.joinToString(""))
    }

    override fun solvePart1(inputLines: List<String>): String {
        val input = createInput(inputLines)
        val solution = sumMul(input.case)
        return solution.toString()
    }

    /**
     * Find the starting indexes of all "do()" and "don't" substrings. Then use the solution for part1 only on the "do()" substrings.
     */
    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines)
        var solution = 0
        val DO = "do()"
        val string = DO + input.case
        val doDontRegex = "do\\(\\)|don't\\(\\)".toRegex()
        val doDontStartIdxes = doDontRegex.findAll(string).map { it.range.start }.toList()
        for (index in doDontStartIdxes.indices) {
            val startIndexInString = doDontStartIdxes[index]
            val endIndexInString = if (index >= doDontStartIdxes.count()-1) string.count() else doDontStartIdxes[index + 1]
            val subs = string.substring(startIndex = startIndexInString, endIndex = endIndexInString)
            if (subs.startsWith(DO)) {
                solution += sumMul(subs)
            }
        }
        return solution.toString()
    }

    private fun sumMul(s: String): Int {
        val regex = """mul\((\d{1,3}),(\d{1,3})\)""".toRegex()
        val matches = regex.findAll(s).map { it.groups }.toList()
        val sumMuls =
            matches.sumOf { m -> m[1]?.value?.toInt()?.times(m[2]?.value?.toInt()!!) ?: throw IllegalArgumentException(m.toString()) }
        return sumMuls
    }
}

