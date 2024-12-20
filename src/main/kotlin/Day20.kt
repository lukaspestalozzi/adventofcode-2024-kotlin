import me.tongfei.progressbar.ProgressBar
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.SimpleGraph
import kotlin.math.abs

class Day20 : AbstractSolver(
    "20", "1497", "1030809" // Note: those are the expected solutions on the real input
) {
    companion object {
        const val WALL = '#'
    }


    private enum class Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    private data class Shortcut(val source: Pos, val target: Pos) {

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
            return positions().first { charAt(it) == c }
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

    private data class Edge(val source: Pos, val target: Pos)
    private data class Input(val grid: Grid, val start: Pos, val end: Pos)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val cells = mutableListOf<List<Char>>()
        for (line in input) {
            val charline = mutableListOf<Char>()
            for (c in line) {
                charline.add(c)
            }
            cells.add(charline)
        }
        val grid = Grid(cells = cells)
        val start: Pos = grid.findUniqueChar('S')
        val end: Pos = grid.findUniqueChar('E')
        val createdInput = Input(grid, start, end)
        if (printInput) {
            logger.info { createdInput }
            grid.print()
        }
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): String {
        val input = createInput(inputLines, true)
        val startNode = input.start
        val endNode = input.end
        val mazeGraph = createInitialGraph(input)
        val algo = DijkstraShortestPath(mazeGraph)
        val referencePath = algo.getPath(startNode, endNode)
        val solution = countShortcuts(referencePath.vertexList, 100, 2)
        return solution.toString()
    }

    private fun createInitialGraph(input: Input): SimpleGraph<Pos, Edge> {
        val grid = input.grid
        val graph = SimpleGraph<Pos, Edge>(Edge::class.java)
        for (pos in grid.positions()) {
            if (grid.charAt(pos) != WALL) {
                graph.addVertex(pos)
                for (d in Direction.entries) {
                    val nabo = pos.move(d)
                    if (grid.charAt(nabo) != WALL) {
                        graph.addVertex(nabo)
                        graph.addEdge(pos, nabo, Edge(source = pos, target = nabo))
                    }
                }
            }
        }
        return graph
    }

    private fun countShortcuts(referencePath: List<Pos>, minRequiredSave: Int, maxDistance: Int): Int {
        val referencePathLength = referencePath.size
        var solution = 0
        logger.info { "baseline: $referencePathLength" }
        val pb = ProgressBar("outer", (referencePath.size.toLong() * referencePath.size.toLong() - 1) / 2)
        for (idx in referencePath.indices) {
            for (idx2 in (idx + minRequiredSave)..<referencePath.size) {
                pb.step()
                val pos1 = referencePath[idx]
                val pos2 = referencePath[idx2]
                val distance = distance(pos1, pos2)
                if (distance <= maxDistance) {
                    // valid Shortcut
                    val saved = (idx2 - idx) - distance
                    //logger.info { "$pos1 -> $pos2 => $saved (($idx2 - $idx) - $distance )" }
                    if (saved >= minRequiredSave) {
                        solution++
                    }
                }
            }
        }
        return solution
    }

    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines, true)
        val startNode = input.start
        val endNode = input.end
        val mazeGraph = createInitialGraph(input)
        val algo = DijkstraShortestPath(mazeGraph)
        val referencePath = algo.getPath(startNode, endNode)
        val solution = countShortcuts(referencePath.vertexList, 100, 20)
        return solution.toString()
    }

    private fun distance(pos1: Pos, pos2: Pos): Int {
        return abs(pos1.row - pos2.row) + abs(pos1.col - pos2.col)
    }
}

