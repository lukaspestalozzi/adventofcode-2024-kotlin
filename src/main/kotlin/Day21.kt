import java.util.*
import kotlin.math.abs

class Day21 : AbstractSolver("21", "126384", "") {

    companion object {
        const val R = '>'
        const val U = '^'
        const val D = 'v'
        const val L = '<'
        const val A = 'A'
        const val X = '_'

    }

    private enum class Order {
        HV, VH, BOTH
    }

    private val CACHE: MutableMap<Pair<Move, Int>, Long> = mutableMapOf()

    private data class Case(val code: String)
    private data class Input(val cases: List<Case>)
    private data class Pos(val row: Int, val col: Int)
    private data class Move(
        val u: Int = 0,
        val d: Int = 0,
        val l: Int = 0,
        val r: Int = 0,
        val order: Order = Order.BOTH
    ) {
        fun length(): Int = u + d + l + r + 1
        fun plot(): String {
            return when (order) {
                Order.VH, Order.BOTH -> U.toString().repeat(u) + D.toString().repeat(d) + R.toString()
                    .repeat(r) + L.toString().repeat(l)

                Order.HV -> R.toString().repeat(r) + L.toString().repeat(l) + U.toString().repeat(u) + D.toString()
                    .repeat(d)
            } + A
        }
    }

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val cases: MutableList<Case> = mutableListOf()
        for (line: String in input) {
            val case = Case(line)
            cases.add(case)
        }
        val createdInput = Input(cases)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    private val numToPos: Map<Char, Pos> = mapOf(
        '7' to Pos(0, 0), '8' to Pos(0, 1), '9' to Pos(0, 2),
        '4' to Pos(1, 0), '5' to Pos(1, 1), '6' to Pos(1, 2),
        '1' to Pos(2, 0), '2' to Pos(2, 1), '3' to Pos(2, 2),
        X to Pos(3, 0), '0' to Pos(3, 1), A to Pos(3, 2)
    )
    private val dirToPos: Map<Char, Pos> = mapOf(
        X to Pos(0, 0), U to Pos(0, 1), A to Pos(0, 2),
        L to Pos(1, 0), D to Pos(1, 1), R to Pos(1, 2)
    )

    private fun numericKeypad(code: String): List<Move> {
        var currChar = A
        val commands = mutableListOf<Move>()
        for (c in code) {
            commands.add(numericKeypad(currChar, c))
            currChar = c
        }
        return commands
    }

    private fun numericKeypad(from: Char, to: Char): Move {
        if (from == to) return Move()
        val posFrom = numToPos[from]!!
        val posTo = numToPos[to]!!
        val upDownDiff = posTo.row - posFrom.row
        val down = if (upDownDiff > 0) upDownDiff else 0
        val up = if (upDownDiff < 0) abs(upDownDiff) else 0
        val rightLeftDiff = posTo.col - posFrom.col
        val right = if (rightLeftDiff > 0) rightLeftDiff else 0
        val left = if (rightLeftDiff < 0) abs(rightLeftDiff) else 0

        val order: Order = if (posFrom.row == 3 && posTo.col == 0) {
            Order.VH
        } else if (posFrom.col == 0 && posTo.row == 3) {
            Order.HV
        } else Order.BOTH

        return Move(u = up, d = down, l = left, r = right, order = order)
    }

    private fun directionalKeypad(move: Move): List<Move> {
        check(move.order != Order.BOTH)
        var currChar = A
        val commands = mutableListOf<Move>()
        var u = move.u
        var r = move.r
        var d = move.d
        var l = move.l
        if (move.order == Order.HV) {
            while (l > 0) {
                commands.add(directionalKeypad(currChar, L))
                currChar = L
                l--
            }
            while (r > 0) {
                commands.add(directionalKeypad(currChar, R))
                currChar = R
                r--
            }
            while (d > 0) {
                commands.add(directionalKeypad(currChar, D))
                currChar = D
                d--
            }
            while (u > 0) {
                commands.add(directionalKeypad(currChar, U))
                currChar = U
                u--
            }
        } else {
            while (d > 0) {
                commands.add(directionalKeypad(currChar, D))
                currChar = D
                d--
            }
            while (u > 0) {
                commands.add(directionalKeypad(currChar, U))
                currChar = U
                u--
            }
            while (l > 0) {
                commands.add(directionalKeypad(currChar, L))
                currChar = L
                l--
            }
            while (r > 0) {
                commands.add(directionalKeypad(currChar, R))
                currChar = R
                r--
            }
        }

        commands.add(directionalKeypad(currChar, A))
        return commands
    }

    private fun directionalKeypad(from: Char, to: Char): Move {
        if (from == to) return Move()
        val posFrom = dirToPos[from]!!
        val posTo = dirToPos[to]!!
        val upDownDiff = posTo.row - posFrom.row
        val down = if (upDownDiff > 0) upDownDiff else 0
        val up = if (upDownDiff < 0) abs(upDownDiff) else 0
        val rightLeftDiff = posTo.col - posFrom.col
        val right = if (rightLeftDiff > 0) rightLeftDiff else 0
        val left = if (rightLeftDiff < 0) abs(rightLeftDiff) else 0

        val order: Order = if (posFrom.row == 0 && posTo.col == 0) {
            Order.VH
        } else if (posFrom.col == 0 && posTo.row == 0) {
            Order.HV
        } else Order.BOTH
        return Move(u = up, d = down, l = left, r = right, order = order)
    }

    override fun solvePart1(inputLines: List<String>): String {
        CACHE.clear()
        val input = createInput(inputLines, true)
        check(numericKeypad('A', '0') == Move(l = 1)) { numericKeypad('A', '0') }
        check(numericKeypad('0', '2') == Move(u = 1)) { numericKeypad('0', '2') }
        check(numericKeypad('2', '9') == Move(u = 2, r = 1)) { numericKeypad('2', '9') }
        check(numericKeypad('9', 'A') == Move(d = 3))

        check(directionalKeypad(A, U) == Move(l = 1))
        check(directionalKeypad(U, R) == Move(r = 1, d = 1)) { directionalKeypad(U, R) }
        check(directionalKeypad(A, L) == Move(l = 2, d = 1, order = Order.VH)) { directionalKeypad(A, L) }
        check(directionalKeypad(L, A) == Move(u = 1, r = 2, order = Order.HV))
        var solution: Long = 0

        for (case in input.cases) {
            var result = 0L
            val numpadCommands = numericKeypad(case.code)
            for (move in numpadCommands) {
                val res = determineShortestCommands(1, move)
                result += res
            }

            solution += (result * case.code.substringBefore('A').toInt())
        }

        return solution.toString()
    }

    private fun determineShortestCommands(level: Int, move: Move): Long {
        if (level == -1) {
            return move.length().toLong()
        }
        if (CACHE.containsKey(Pair(move, level))) {
            return CACHE[Pair(move, level)]!!
        }
       val  result= if (move.order == Order.BOTH) {
            val vh = Move(u = move.u, d = move.d, l = move.l, r = move.r, order = Order.VH)
            val hv = Move(u = move.u, d = move.d, l = move.l, r = move.r, order = Order.HV)
            val resVh = determineShortestCommands(level, vh)
            val resHv = determineShortestCommands(level, hv)
            if (resVh < resHv) resVh else resHv
        } else {
            directionalKeypad(move).sumOf { determineShortestCommands(level - 1, it) }
        }
        CACHE[Pair(move, level)] = result
        return result
    }

    override fun solvePart2(inputLines: List<String>): String {
        CACHE.clear()
        val input = createInput(inputLines, true)
        var solution: Long = 0

        for (case in input.cases) {
            var result = 0L
            val numpadCommands = numericKeypad(case.code)
            for (move in numpadCommands) {
                val res = determineShortestCommands(24, move)
                result += res
            }

            solution += (result * case.code.substringBefore('A').toInt())
        }

        return solution.toString()
    }
}

