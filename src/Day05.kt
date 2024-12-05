import kotlin.math.floor

class Day05 : AbstractSolver("05", 143, 123) {

    private data class Case(val list: List<Int>)
    private data class Rule(val first: Int, val second: Int)
    private data class Input(val cases: List<Case>, val rules: List<Rule>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val cases: MutableList<Case> = mutableListOf()
        val rules: MutableList<Rule> = mutableListOf()
        var parseRules = true
        for (line: String in input) {
            if (line.isBlank()) {
                parseRules = false
                continue
            }
            if (parseRules) {
                val splitted = line.split("|").map { s -> s.toInt() }
                check(splitted.count() == 2)
                val rule = Rule(first = splitted.first(), second = splitted[1])
                rules.add(rule)
            } else {
                val case = Case(line.split(",").map { s -> s.toInt() })
                check(case.list.count() % 2 == 1)
                cases.add(case)
            }
        }
        val createdInput = Input(cases, rules)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): Int {
        val input = createInput(inputLines)
        val invertedRules: List<Rule> = input.rules.map { Rule(it.second, it.first) } // first must be after second
        val rulesBackwards: Map<Int, Set<Int>> = toMultimap(invertedRules, Rule::second, Rule::first) // key must be before values
        val validCases = mutableListOf<Case>()
        for (case in input.cases) {
            if (isValid(case, rulesBackwards)) {
                validCases.add(case)
            }
        }
        val solution = validCases.sumOf { c -> findMiddleNumber(c.list) }
        return solution
    }

    /**
     * rulesBackwards: key must be before values
     */
    private fun isValid(case: Case, rulesBackwards: Map<Int, Set<Int>>): Boolean {
        val seen: MutableSet<Int> = mutableSetOf()
        for (i in case.list) {
            val forbiddenPrev = rulesBackwards.getOrDefault(i, emptySet<Int>())
            val valid = seen.intersect(forbiddenPrev).isEmpty()
            if (!valid) {
                return false
            }
            seen.add(i)
        }
        return true
    }

    override fun solvePart2(inputLines: List<String>): Int {
        val input = createInput(inputLines)
        val invertedRules: List<Rule> = input.rules.map { Rule(it.second, it.first) } // first must be after second
        val rulesBackwards: Map<Int, Set<Int>> = toMultimap(invertedRules, Rule::second, Rule::first) // key must be before values
        val correctedCases = mutableListOf<Case>()
        for (case in input.cases) {
            val correctedList : MutableList<Int> = case.list.toMutableList()
            var violatedRule = violatedRule(correctedList, rulesBackwards)
            if(violatedRule != null){
                correctedCases.add(Case(correctedList))
            }
            while (violatedRule != null) {
                // swap
                val idxFirst = case.list.indexOf(violatedRule.first)
                correctedList.remove(violatedRule.second)
                correctedList.add(idxFirst, violatedRule.second)
                violatedRule = violatedRule(correctedList, rulesBackwards)
            }
        }
        val solution = correctedCases.sumOf { c -> findMiddleNumber(c.list) }
        return solution
    }

    /**
     * rulesBackwards: key must be before values
     */
    private fun violatedRule(case: List<Int>, rulesBackwards: Map<Int, Set<Int>>): Rule? {
        val seen: MutableSet<Int> = mutableSetOf()
        for (i in case) {
            val forbiddenPrev = rulesBackwards.getOrDefault(i, emptySet<Int>())
            val intersection = seen.intersect(forbiddenPrev)
            if (intersection.isNotEmpty()) {
                val rule = Rule(i, intersection.first())
                return rule
            }
            seen.add(i)
        }
        return null
    }

    private fun <T, K, V> toMultimap(coll: Collection<T>, keyFun: (T) -> K, valFun: (T) -> V): Map<K, Set<V>> {
        val groupBy = coll.groupBy(keyFun, valFun).mapValues { it.value.toSet() }
        return groupBy
    }

    private fun findMiddleNumber(list: List<Int>): Int {
        val idx = floor(list.count() / 2.0).toInt()
        return list[idx]
    }
}
