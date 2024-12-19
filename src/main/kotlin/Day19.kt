class Day19 : AbstractSolver("19", "6", "16") {

    private data class Case(val pattern: String)
    private data class Input(val cases: List<Case>, val availablePatterns: List<String>)

    private val CACHE_PART1: MutableMap<String, Boolean> = mutableMapOf()
    private val CACHE_PART2: MutableMap<String, Long> = mutableMapOf()

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val cases: MutableList<Case> = mutableListOf()
        val availablePatterns: MutableList<String> = mutableListOf()
        var casePart = false
        for (line: String in input) {
            if (line.isBlank()) {
                casePart = true
                continue
            } else if (casePart) {
                val case = Case(line)
                cases.add(case)
            } else {
                line.split(", ").forEach(availablePatterns::add)
            }
        }
        val createdInput = Input(cases, availablePatterns)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): String {
        CACHE_PART1.clear()
        CACHE_PART2.clear()
        val input = createInput(inputLines, true)
        logger.info { "${input.availablePatterns}" }
        var solution: Long = 0
        for (case in input.cases) {
            if (isPatternPossible(case.pattern, 0, input.availablePatterns)) {
                solution++
            }
        }
        return solution.toString()
    }

    private fun isPatternPossible(pattern: String, idx: Int, availablePatterns: List<String>): Boolean {
        if (idx >= pattern.length) {
            return true
        }
        val remaining = pattern.substring(idx)
        if (CACHE_PART1.contains(remaining)) {
            return CACHE_PART1[remaining]!!
        }
        for (aval in availablePatterns) {
            if (remaining.startsWith(aval)) {
                val result = isPatternPossible(pattern, idx + aval.length, availablePatterns)
                CACHE_PART1[remaining] = result
                if (result) {
                    return true
                }
            }
        }
        CACHE_PART1[remaining] = false
        return false
    }

    override fun solvePart2(inputLines: List<String>): String {
        CACHE_PART1.clear()
        CACHE_PART2.clear()
        val input = createInput(inputLines, true)
        logger.info { "${input.availablePatterns}" }
        var solution: Long = 0
        for (case in input.cases) {
            logger.info { "---- case: ${case.pattern} ----" }
            if(!isPatternPossible(case.pattern, 0, input.availablePatterns)){
                logger.info { "${case.pattern} -> not possible" }
                continue
            }
            val possibilities = countPossibleSolutions(case.pattern, 0, input.availablePatterns)
            logger.info { "${case.pattern} -> $possibilities" }
            solution += possibilities
        }
        return solution.toString()
    }

    private fun countPossibleSolutions(pattern: String, idx: Int, availablePatterns: List<String>): Long {
        if (idx == pattern.length) {
            return 1
        }
        if (idx > pattern.length) {
            throw IllegalArgumentException("$pattern, $idx")
        }
        val remaining = pattern.substring(idx)
        var possible = true
        if(!isPatternPossible(pattern, idx, availablePatterns)){
            logger.info { "not possible? $pattern at $idx" }
            possible = false
        }
        if (CACHE_PART2.contains(remaining)) {
            return CACHE_PART2[remaining]!!
        }
        var count = 0L
        for (aval in availablePatterns) {
            if (remaining.startsWith(aval)) {
                val result = countPossibleSolutions(pattern, idx + aval.length, availablePatterns)
                if(!possible && result > 0){
                    logger.info { "but possible? $pattern at $idx with $aval" }
                }
                count += result
            }
        }
        check(!CACHE_PART2.contains(remaining))
        CACHE_PART2[remaining] = count
        return count
    }
}

