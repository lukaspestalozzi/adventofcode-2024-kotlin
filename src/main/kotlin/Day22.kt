import java.util.*

class Day22 : AbstractSolver("22", "37990510", "23") {

    private data class Case(val seed: Long)
    private data class Input(val cases: List<Case>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val cases: MutableList<Case> = mutableListOf()
        for (line: String in input) {
            val case = Case(line.toLong())
            cases.add(case)
        }
        val createdInput = Input(cases)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    private fun mixPrune(n: Long, secret: Long): Long {
        return prune(mix(n, secret))
    }

    private fun mix(n: Long, secret: Long): Long {
        return n xor secret
    }

    private fun prune(secret: Long): Long {
        return secret % 16777216
    }

    private fun nextSecret(secret: Long): Long {
        var next = secret
        // Calculate the result of multiplying the secret number by 64. Then, mix this result into the secret number. Finally, prune the secret number.
        val n1 = next * 64
        next = mixPrune(n1, next)
        //Calculate the result of dividing the secret number by 32. Round the result down to the nearest integer. Then, mix this result into the secret number. Finally, prune the secret number.
        val n2 = next / 32
        next = mixPrune(n2, next)
        //Calculate the result of multiplying the secret number by 2048. Then, mix this result into the secret number. Finally, prune the secret number.
        val n3 = next * 2048
        next = mixPrune(n3, next)
        return next
    }

    override fun solvePart1(inputLines: List<String>): String {
        val input = createInput(inputLines)
        check(mix(15, 42) == 37L)
        check(prune(100000000) == 16113920L)
        check(nextSecret(123L) == 15887950L) { nextSecret(123L) }

        var solution: Long = 0
        for (case in input.cases) {
            var n = case.seed
            for (i in 0..<2000) {
                n = nextSecret(n)
            }
            solution += n
        }
        return solution.toString()
    }

    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines)
        val priceSequences: MutableList<List<Int>> = mutableListOf(mutableListOf<Int>())
        for (case in input.cases) {
            val prices = mutableListOf<Int>()
            var n = case.seed
            for (i in 0..<2000) {
                n = nextSecret(n)
                val price = (n % 10).toInt()
                check(price < 10)
                prices.add(price)
            }
            priceSequences.add(prices)
        }
        val sequenceToBananas: MutableMap<List<Int>, Int> = mutableMapOf()
        for(prices in priceSequences){
            val seen: MutableSet<List<Int>> = mutableSetOf()
            for(idx in prices.indices){
                val seq = sequenceFor(idx, prices)
                if(seq.isNotEmpty()){
                    if(seen.add(seq)) {
                        sequenceToBananas[seq] = sequenceToBananas.getOrDefault(seq, 0) + prices[idx]
                    }
                }
            }
        }

        val maxVal = sequenceToBananas.values.maxOrNull()
        return maxVal.toString()
    }

    private fun sequenceFor(idx: Int, prices: List<Int>): List<Int> {
        if (idx < 4) {
            return Collections.emptyList()
        }
        return listOf(
            prices[idx - 3] - prices[idx - 4],
            prices[idx - 2] - prices[idx - 3],
            prices[idx - 1] - prices[idx - 2],
            prices[idx - 0] - prices[idx - 1]
        )
    }
}

