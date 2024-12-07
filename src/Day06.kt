class Day06 : AbstractSolver("06", 41, 6) {

    companion object {
        const val OBSTACLE = '#'
        const val FLOOR = '.'
        const val OUTSIDE = '-'
        const val START = '^'
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

    private data class Grid(val cells: List<List<Char>>, var additionalObstacle: Pos? = null) {
        fun charAt(pos: Pos): Char {
            if (additionalObstacle != null && pos == additionalObstacle) {
                return OBSTACLE
            }
            return try {
                cells[pos.row][pos.col]
            } catch (ex: IndexOutOfBoundsException) { // Default value
                OUTSIDE
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

        fun startPos(): Pos {
            return positions().first { charAt(it) == START }
        }

        fun print() {
            for (rowIdx in cells.indices) {
                for (colIdx in cells[rowIdx].indices) {
                    val pos = Pos(rowIdx, colIdx)
                    val c = if (pos == additionalObstacle) 'O' else charAt(pos)
                    print(c)
                }
                println()
            }
            println()
        }
    }

    private data class Input(val grid: Grid)
    private enum class Direction {
        UP, DOWN, LEFT, RIGHT;

        fun rotate(): Direction {
            return when (this) {
                UP -> RIGHT
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
            }
        }
    }

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

    override fun solvePart1(inputLines: List<String>): Int {
        val grid = createInput(inputLines).grid
        val path = determinePath(grid)
        val solution = path.toSet().count()
        return solution
    }

    private fun determinePath(grid: Grid): List<Pos> {
        var currPos = grid.startPos()
        var currDir = Direction.UP
        val path = mutableListOf<Pos>()
        val visited = mutableSetOf<Pair<Pos, Direction>>()
        while (grid.charAt(currPos) != OUTSIDE) {
            path.add(currPos)
            val visit = Pair(currPos, currDir)
            if (visited.contains(visit)) {
                // Loop
//                grid.print()
                return emptyList()
            }
            visited.add(Pair(currPos, currDir))
            var nextPos = currPos.move(currDir)
            while (grid.charAt(nextPos) == OBSTACLE) {
                currDir = currDir.rotate()
                nextPos = currPos.move(currDir)
            }
            currPos = nextPos
        }
        return path
    }

    override fun solvePart2(inputLines: List<String>): Int {
        val grid = createInput(inputLines).grid
        val path = determinePath(grid)
        val solution = mutableSetOf<Pos>()
        for (pos in path.toSet()) {
            if (grid.charAt(pos) == START) {
                continue
            }
            grid.additionalObstacle = pos
            val newPath = determinePath(grid)
            if (newPath.isEmpty()) {
                solution.add(pos)
            }
        }

        return solution.count()
    }

}

