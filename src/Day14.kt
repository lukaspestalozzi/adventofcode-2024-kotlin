import kotlin.math.abs
import kotlin.math.round

class Day14 : AbstractSolver("14", 12, 0) {

    companion object {
        const val Q1 = 0
        const val Q2 = 1
        const val Q3 = 2
        const val Q4 = 3
        const val MIDDLE = -1

    }

    private val TEST_BOUND = Bounds(11, 7)
    private val BOUND = Bounds(101, 103)

    private data class Bounds(val modX: Int, val modY: Int)
    private data class Pos(val x: Int, val y: Int) {
        fun move(vx: Int, vy: Int, bound: Bounds): Pos {
            var nextX = (x + vx) % bound.modX
            var nextY = (y + vy) % bound.modY
            while(nextX < 0){
                nextX += bound.modX
            }
            while(nextY < 0){
                nextY += bound.modY
            }
            return Pos(nextX, nextY)
        }

        fun getQuadrant(b: Bounds): Int {
            val midX = b.modX / 2
            val midY = b.modY / 2
            if (x < midX && y < midY) {
                return Q1
            }
            if (x < midX && y > midY) {
                return Q2
            }
            if (x > midX && y < midY) {
                return Q3
            }
            if (x > midX && y > midY) {
                return Q4
            } else {
                return MIDDLE
            }
        }
    }

    private data class Grid(val robots: List<Robot>, val bound: Bounds) {

        fun print() {
            val robotsByPosition: Map<Pos, List<Robot>> = robots.groupBy { it.p }
            for (rowIdx in 0..<bound.modY) {
                for (colIdx in 0..<bound.modX) {
                    val pos = Pos(colIdx, rowIdx)
                    val c = robotsByPosition.get(pos)?.size ?: '.'
                    print(c)
                }
                println()
            }
            println()
        }
    }

    private data class Robot(val p: Pos, val vx: Int, val vy: Int) {
        fun move(b: Bounds): Robot {
            return Robot(p.move(vx, vy, b), vx=vx, vy=vy)
        }
    }

    private data class Input(val robots: List<Robot>, val bound: Bounds)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val robots: MutableList<Robot> = mutableListOf()
        val regex = """p=(\d+),(\d+) v=([-|\d]+),([-|\d]+)""".toRegex()
        var bound = TEST_BOUND
        for (line: String in input) {
            val (px, py, vx, vy) = regex.matchEntire(line)?.destructured ?: throw IllegalArgumentException(line)
            val robot = Robot(Pos(px.toInt(), py.toInt()), vx.toInt(), vy.toInt())
            robots.add(robot)
            if (robot.p.x > bound.modX || robot.p.y > bound.modY) {
                bound = BOUND
            }
        }
        val createdInput = Input(robots, bound)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): Number {
        val input = createInput(inputLines, true)
        val bound: Bounds = input.bound
        var nextRobots = input.robots
        for (sec in 1..100) {
            nextRobots = nextRobots.map { it.move(bound) }
        }
//        Grid(robots = nextRobots, bound = bound).print()
        var solution: Long = groupByQuadrantAndCalcSafetyFactor(nextRobots, bound)
        return solution
    }

    private fun groupByQuadrantAndCalcSafetyFactor(robots: List<Robot>, bound: Bounds): Long {
        val quadrants = mutableListOf<Int>(0, 0, 0, 0)
        for (r in robots) {
            val qidx = r.p.getQuadrant(bound)
            if (qidx != MIDDLE) {
                quadrants[qidx] = quadrants[qidx]+1
            }
        }
        logger.info { "$quadrants" }
        return quadrants.reduce { a, b -> a * b }.toLong()
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines, true)
        val bound: Bounds = input.bound
        var nextRobots = input.robots
        var sec = 1L
        while (sec < 1000000) {
            nextRobots = nextRobots.map { it.move(bound) }
            val robotsByPosition: Map<Pos, List<Robot>> = nextRobots.groupBy { it.p }
            if (robotsByPosition.size == nextRobots.size){
                break
            }
            sec++
        }
        Grid(robots = nextRobots, bound = bound).print()
        var solution: Long = sec
        return solution
    }
}