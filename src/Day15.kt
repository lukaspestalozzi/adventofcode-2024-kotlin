class Day15 : AbstractSolver("15", 10092, 0) {


    companion object {
        const val WALL = '#'
        const val BOX = 'O'
        const val FLOOR = '.'
        const val ROBOT = '@'

    }

    private enum class Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    private fun fromChar(c: Char): Direction {
        return when (c) {
            '^' -> Direction.UP
            '<' -> Direction.LEFT
            'v' -> Direction.DOWN
            '>' -> Direction.RIGHT
            else -> throw IllegalArgumentException("illegal char:$c")
        }
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

    private data class Grid(val cells: List<MutableList<Char>>, val outsideChar: Char = WALL) {
        private var robotPos: Pos;

        init {
            robotPos = positions().first { charAt(it) == ROBOT }
        }

        fun getRobotPos(): Pos {
            return robotPos
        }

        fun charAt(pos: Pos): Char {
            return try {
                cells[pos.row][pos.col]
            } catch (ex: IndexOutOfBoundsException) {
                outsideChar
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

        fun slide(pos1: Pos, direction: Direction) {
            val pos2 = pos1.move(direction)
            // can even slide? Only slide to a floor tile
            check(charAt(pos2) == FLOOR)
            // swap
            val c1 = charAt(pos1)
            val c2 = charAt(pos2)
            cells[pos1.row][pos1.col] = c2
            cells[pos2.row][pos2.col] = c1
            // update robot position
            if (c1 == ROBOT) {
                robotPos = pos2
            }
        }

        fun print() {
            for (rowIdx in cells.indices) {
                for (colIdx in cells[rowIdx].indices) {
                    val pos = Pos(rowIdx, colIdx)
                    val c = charAt(pos)
                    print(c)
                }
                println()
            }
            println()
        }
    }

    private data class Input(val grid: Grid, val moves: List<Direction>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val grid = mutableListOf<MutableList<Char>>()
        val iterator = input.iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            val charline = mutableListOf<Char>()
            if (line.isBlank()) {
                break
            }
            for (c in line) {
                charline.add(c)
            }
            grid.add(charline)
        }

        // moves
        val moves = mutableListOf<Direction>()
        while (iterator.hasNext()) {
            val line = iterator.next()
            for (c in line) {
                moves.add(fromChar(c))
            }
        }

        val createdInput = Input(Grid(grid), moves)
        if (printInput) {
            logger.info { createdInput }
            createdInput.grid.print()
        }
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): Number {
        val input = createInput(inputLines, true)
        val grid = input.grid
        for (move in input.moves) {
            moveRobot(grid, direction = move)
        }
        var solution: Long = sumGPS(grid)
        return solution
    }

    private fun moveRobot(grid: Grid, direction: Direction) {
        tryMove(grid, grid.getRobotPos(), direction)
    }

    /**
     * @return true if did move something, false if not.
     */
    private fun tryMove(grid: Grid, pos: Pos, direction: Direction): Boolean {
        if (grid.charAt(pos) == WALL) {
            // can never move
            return false
        }
        val nextPos = pos.move(direction)
        val nextTile: Char = grid.charAt(nextPos)
        if (nextTile == FLOOR) {
            // move and stop
            grid.slide(pos, direction)
            return true
        } else {
            // Box or Robot
            // try to move the next obj
            if (tryMove(grid, pos = nextPos, direction = direction)) {
                // then try to move itself again
                tryMove(grid, pos = pos, direction = direction)
                return true
            }
        }
        return false
    }

    private fun sumGPS(grid: Grid): Long {
        return grid.positions().filter { grid.charAt(it) == BOX }.sumOf { 100 * it.row + it.col }.toLong()
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution
    }
}

