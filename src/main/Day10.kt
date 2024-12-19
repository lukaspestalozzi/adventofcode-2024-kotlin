import java.util.*

class Day10 : AbstractSolver("10", "36", "81") {
    private enum class Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    private data class Pos(val row: Int, val col: Int) {
        fun move(direction: Direction): Pos {
            return when (direction) {
                Direction.UP -> Pos(row - 1, col)
                Direction.DOWN -> Pos(row + 1, col)
                Direction.LEFT -> Pos(row, col - 1)
                Direction.RIGHT -> Pos(row, col + 1)
            }
        }
    }

    private data class Grid(val cells: List<List<Int>>, val outside: Int = -10) {
        fun at(pos: Pos): Int {
            return try {
                cells[pos.row][pos.col]
            } catch (ex: IndexOutOfBoundsException) {
                outside
            }
        }

        fun positions(): List<Pos> {
            val poses = mutableListOf<Pos>()
            for (rowIdx in cells.indices) {
                for (colIdx in cells[rowIdx].indices) {
                    poses.add(Pos(rowIdx, colIdx))
                }
            }
            return poses
        }

        fun print() {
            for (rowIdx in cells.indices) {
                for (colIdx in cells[rowIdx].indices) {
                    val pos = Pos(rowIdx, colIdx)
                    val c = at(pos)
                    print(c)
                }
                println()
            }
            println()
        }
    }

    private data class Input(val grid: Grid)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val grid = mutableListOf<List<Int>>()
        for (line in input) {
            val charline = mutableListOf<Int>()
            for (c in line) {
                charline.add(c.digitToInt())
            }
            grid.add(charline)
        }
        val createdInput = Input(Grid(cells = grid))
        if (printInput) {
            logger.info { createdInput }
        }
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): String {
        val grid = createInput(inputLines).grid
        var solution = 0
        for (pos in grid.positions()) {
            val trailends = collectTrailends(grid, pos, -1)
            solution += trailends.size
        }
        return solution.toString()
    }

    private fun collectTrailends(grid: Grid, currPos: Pos, lastHeight: Int): Set<Pos> {
        val currHeight = grid.at(currPos)
        val expectedHeight = lastHeight + 1
        if (currHeight != expectedHeight) {
            return Collections.emptySet()
        }
        if (currHeight == 9) {
            return Collections.singleton(currPos)
        }

        val concat = Direction.entries.flatMap { dir -> collectTrailends(grid, currPos.move(dir), currHeight) }.toSet()
        return concat
    }

    override fun solvePart2(inputLines: List<String>): String {
        val grid = createInput(inputLines).grid
        var solution = 0
        for (pos in grid.positions()) {
            solution += countTrails(grid, pos, -1)
        }
        return solution.toString()
    }

    private fun countTrails(grid: Grid, currPos: Pos, lastHeight: Int): Int {
        val currHeight = grid.at(currPos)
        val expectedHeight = lastHeight + 1
        if (currHeight != expectedHeight) {
            return 0
        }
        if (currHeight == 9) {
            return 1
        }

        val sum = Direction.entries.sumOf { dir -> countTrails(grid, currPos.move(dir), currHeight) }
        return sum
    }
}

