class Day04 : AbstractSolver("04", "18", "9") {

    private data class Pos(val row: Int, val col: Int) {
        fun move(direction: Direction): Pos {
            return when (direction) {
                Direction.UP -> Pos(row - 1, col)
                Direction.DOWN -> Pos(row + 1, col)
                Direction.LEFT -> Pos(row, col - 1)
                Direction.RIGHT -> Pos(row, col + 1)
                Direction.DIAG_UP_LEFT -> move(Direction.UP).move(Direction.LEFT)
                Direction.DIAG_UP_RIGHT -> move(Direction.UP).move(Direction.RIGHT)
                Direction.DIAG_DOWN_LEFT -> move(Direction.DOWN).move(Direction.LEFT)
                Direction.DIAG_DOWN_RIGHT -> move(Direction.DOWN).move(Direction.RIGHT)
            }
        }
    }

    private data class Grid(val cells: List<List<Char>>) {
        fun charAt(pos: Pos): Char {
            return try {
                cells[pos.row][pos.col]
            } catch (ex: IndexOutOfBoundsException) { // Default value
                '-'
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
    }

    private data class Input(val grid: Grid)
    private enum class Direction { UP, DOWN, LEFT, RIGHT, DIAG_UP_LEFT, DIAG_UP_RIGHT, DIAG_DOWN_LEFT, DIAG_DOWN_RIGHT }

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val grid = mutableListOf<List<Char>>()
        for (line in input) {
            val charline = mutableListOf<Char>()
            for (c in line) {
                charline.add(c)
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
            solution += countXMAS(grid, pos)
        }

        return solution.toString()
    }

    private fun countXMAS(grid: Grid, pos: Pos): Int {
        return Direction.entries.sumOf { d -> countString(grid, pos, "XMAS", 0, d) }
    }

    private fun countString(grid: Grid, pos: Pos, expectedString: String, currIdx: Int, direction: Direction): Int {
        if (currIdx == expectedString.length) {
            return 1
        }

        val expectedChar: Char = expectedString[currIdx]
        val c: Char = grid.charAt(pos)
        if (c != expectedChar) {
            return 0
        }
        // find next position
        val nextPos = pos.move(direction)
        return countString(grid, nextPos, expectedString, currIdx + 1, direction)
    }

    override fun solvePart2(inputLines: List<String>): String {
        val grid = createInput(inputLines).grid
        var solution = 0
        for (pos in grid.positions()) {
            solution += countX_MAS(grid, pos)
        }

        return solution.toString()
    }

    private fun countX_MAS(grid: Grid, pos: Pos): Int {
        val c: Char = grid.charAt(pos)
        if (c != 'A') {
            return 0
        }
        val startUpLeft = countString(grid, pos.move(Direction.DIAG_UP_LEFT), "MAS", 0, Direction.DIAG_DOWN_RIGHT)
        val startUpRight = countString(grid, pos.move(Direction.DIAG_UP_RIGHT), "MAS", 0, Direction.DIAG_DOWN_LEFT)
        val startDownRight = countString(grid, pos.move(Direction.DIAG_DOWN_RIGHT), "MAS", 0, Direction.DIAG_UP_LEFT)
        val startDownLeft = countString(grid, pos.move(Direction.DIAG_DOWN_LEFT), "MAS", 0, Direction.DIAG_UP_RIGHT)

        return when (startUpLeft + startUpRight + startDownRight + startDownLeft) {
            0, 1 -> 0
            2 -> 1
            else -> throw IllegalArgumentException()
        }
    }
}

