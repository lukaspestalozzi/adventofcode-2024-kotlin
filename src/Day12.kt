import java.util.*
import me.tongfei.progressbar.*;

class Day12 : AbstractSolver("12", 1184, 368) {
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

    private data class Grid(val cells: List<List<Char>>, val outsideChar: Char = '!') {
        fun charAt(pos: Pos): Char {
            return try {
                cells[pos.row][pos.col]
            } catch (ex: IndexOutOfBoundsException) {
                outsideChar
            }
        }

        fun nabos(pos: Pos): List<Pos> {
            return Direction.entries.map { pos.move(it) }
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
            createdInput.grid.print()
        }
        return createdInput
    }

    private data class Region(val plots: Set<Pos>, val plant: Char)

    private val NULL_REGION = Region(plots = Collections.emptySet(), plant = '!')

    override fun solvePart1(inputLines: List<String>): Number {
        val input = createInput(inputLines, printInput = true)
        var solution: Long = 0
        for (region in ProgressBar.wrap(collectRegions(grid = input.grid), "calculate regions")) {
            val area = calcArea(region)
            val perimeter = calcPerimeter(region)
            solution += area * perimeter
        }
        return solution
    }

    private fun collectRegions(grid: Grid): List<Region> {
        val pb = ProgressBar("Collect Regions", 624)
        val poses: MutableList<Pos> = grid.positions().toMutableList()
        val regions = mutableListOf<Region>()
        while (poses.isNotEmpty()) {
            val curr = poses.removeFirst()
            val region = collectRegion(grid, curr)
            regions.add(region)
            pb.step()
            poses.removeAll(region.plots)
        }
        return regions
    }

    private fun collectRegion(grid: Grid, pos: Pos): Region {
        val plant = grid.charAt(pos)
        if (plant == grid.outsideChar) {
            return NULL_REGION
        }
        val plots = mutableSetOf<Pos>()
        val open = mutableSetOf(pos)
        val seen = mutableSetOf<Pos>()
        while (open.isNotEmpty()) {
            val curr = open.first()
            open.remove(curr)
            seen.add(curr)
            if (grid.charAt(curr) == plant) {
                plots.add(curr)
                val nabos = grid.nabos(curr).filter { !seen.contains(it) }
                open.addAll(nabos)
            }
        }
        return Region(plots = plots, plant = plant)
    }

    private fun calcArea(region: Region): Long {
        return region.plots.size.toLong()
    }

    private fun calcPerimeter(region: Region): Long {
        val plots = region.plots
        var perimeter = 0L
        for (pos in plots) {
            val nabos = Direction.entries.map { pos.move(it) }
            perimeter += nabos.count { !plots.contains(it) }
        }
        return perimeter
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines, printInput = true)
        var solution: Long = 0
        for (region in ProgressBar.wrap(collectRegions(grid = input.grid), "calculate regions")) {
            val area = calcArea(region)
            val sides = calcSides(region)
            solution += area * sides
        }
        return solution
    }

    private fun calcSides(region: Region): Long {
        // Nbr sides = Nbr edges
        // check each edge for each pos. eg upper-right is an edge if up and right are not in the region
        val plots = region.plots
        var outsideEdges = 0L
        var insideEdges = 0L
        for (pos in plots) {
            // outside edges
            for (d in Direction.entries) {
                if (!plots.contains(pos.move(d)) && !plots.contains(pos.move(d.rotateRight()))) {
                    outsideEdges++
                }
            }
            // inside edges
            for (d in Direction.entries) {
                val nabo = pos.move(d)
                if (!plots.contains(nabo)) {
                    val notEmpty1 = nabo.move(d.rotateRight())
                    val notEmpty2 = notEmpty1.move(d.rotateRight().rotateRight())
                    val isEdge = plots.contains(notEmpty1) && plots.contains(notEmpty2)
                    if (isEdge) {
                        insideEdges++
                    }
                }
            }
        }
        return outsideEdges + insideEdges
    }
}

