class DayGrid/*TODO add nbr*/ : AbstractSolver(TODO("add nbr as string"), "", "") {
    companion object {
        const val WALL = '#'
    }

    private enum class Direction {
        UP, DOWN, LEFT, RIGHT;

        fun rotateRight(): Direction {
            return when (this) {
                UP -> RIGHT
                RIGHT -> DOWN
                DOWN -> LEFT
                LEFT -> UP
            }
        }

        fun rotateLeft(): Direction {
            return when (this) {
                UP -> LEFT
                RIGHT -> UP
                DOWN -> RIGHT
                LEFT -> DOWN
            }
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

    private data class Grid(val cells: List<List<Char>>, val outsideChar: Char = WALL) {
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

        fun findUniqueChar(c: Char): Pos {
            return positions().first {charAt(it)==c}
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

    private data class Input(val grid: Grid)

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
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution.toString()
    }

    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution.toString()
    }
}

