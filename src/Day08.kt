class Day08 : AbstractSolver("08", 14, 34) {
    private data class Pos(val row: Int, val col: Int)
    private data class Grid(val cells: List<List<Char>>) {
        fun charAt(pos: Pos): Char {
            return try {
                cells[pos.row][pos.col]
            } catch (ex: IndexOutOfBoundsException) { // Default value
                '.'
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
                    val c = charAt(pos)
                    print(c)
                }
                println()
            }
            println()
        }
    }

    private data class Antinode(val frequency: Char, val row: Int, val col: Int)
    private data class Antenna(val frequency: Char, val pos: Pos) {

        fun antinode(other: Antenna): List<Antinode> {
            if (other.frequency != frequency) {
                return emptyList()
            }

            val diffRow = other.pos.row - pos.row
            val diffCol = other.pos.col - pos.col
            val antinode1 = Antinode(frequency, other.pos.row + diffRow, other.pos.col + diffCol)
            val antinode2 = Antinode(frequency, pos.row - diffRow, pos.col - diffCol)
            return listOf(antinode1, antinode2)
        }
    }

    private data class Input(val antennas: List<Antenna>, val maxRow: Int, val maxCol: Int)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val gridList = mutableListOf<List<Char>>()
        for (line in input) {
            val charline = mutableListOf<Char>()
            for (c in line) {
                charline.add(c)
            }
            gridList.add(charline)
        }
        val grid = Grid(cells = gridList)
        val antennas: MutableList<Antenna> = mutableListOf()
        for (pos in grid.positions()) {
            if (grid.charAt(pos) != '.') {
                antennas.add(Antenna(frequency = grid.charAt(pos), pos = pos))
            }
        }
        val createdInput =
            Input(antennas = antennas, maxRow = grid.cells.count() - 1, maxCol = grid.cells[0].count() - 1)
        if (printInput) {
            grid.print()
            logger.info { createdInput }
        }
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): Number {
        val input = createInput(inputLines, true)
        val antennas = input.antennas
        val antinodes: MutableSet<Antinode> = mutableSetOf()
        for (pair in antennas.combinations(2)) {
            check(pair.count() == 2)
            val antenna: Antenna = pair.first()
            antinodes.addAll(antenna.antinode(pair.last()))
        }
        logger.info { "antinodes: $antinodes" }

        // Check which antinodes are in the grid
        val inGrid = antinodes.filter {
            0 <= it.row && it.row <= input.maxRow //
                    && 0 <= it.col && it.col <= input.maxCol
        }
        logger.info { "inGrid: $inGrid" }
        return inGrid.map { Pos(it.row, it.col) }.toSet().count()
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution
    }
}

