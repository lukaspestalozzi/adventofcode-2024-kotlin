import me.tongfei.progressbar.ProgressBar
import org.jgrapht.alg.shortestpath.DijkstraManyToManyShortestPaths
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.SimpleGraph
import kotlin.math.abs

class Day20 : AbstractSolver("20", "", "") {
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
        // baseline
        val referencePath = DijkstraShortestPath(mazeGraph).getPath(startNode, endNode)
        val referencePathLength = referencePath.weight

        // shortcuts
        val shortcuts = findPossibleShortcuts(input.grid, referencePath.vertexList)
        var solution: Long = 0
        for ((sourcePos, targetPos) in ProgressBar.wrap(shortcuts, "shortcuts")) {
            val edge = Edge(sourcePos, targetPos)
            mazeGraph.addEdge(sourcePos, targetPos, edge)
            val algo = DijkstraShortestPath(mazeGraph)
            val w = algo.getPath(startNode, endNode).weight
            val saved = referencePathLength - w
            //logger.info { "path ${w} baseline: $noShortcut -> $saved" }
            if (saved >= 100) {
                solution++
            }
            mazeGraph.removeEdge(edge)
        }
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

    private fun findPossibleShortcuts(
        grid: Grid,
        referencePath: List<Pos>
    ): List<Pair<Pos, Pos>> {
        val shortcuts: MutableList<Pair<Pos, Pos>> = mutableListOf()
        for (pos in grid.positions()) {
            if (grid.charAt(pos) != WALL) {
                for (d in Direction.entries) {
                    val shouldBeWall = pos.move(d)
                    if (grid.charAt(shouldBeWall) == WALL) {
                        val shortcutTarget = shouldBeWall.move(d)
                        if (grid.charAt(shortcutTarget) != WALL) {
                            if (referencePath.indexOf(pos) < referencePath.indexOf(shortcutTarget)) {
                                shortcuts.add(Pair(pos, shortcutTarget))
                            }
                        }
                    }
                }
            }
        }
        return shortcuts
    }

    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines, true)
        var solution = 0
        val startNode = input.start
        val endNode = input.end
        val mazeGraph = createInitialGraph(input)
        val algo = DijkstraManyToManyShortestPaths(mazeGraph)
        // baseline
        val referencePath = algo.getPath(startNode, endNode)
        val referencePathLength = referencePath.weight
        logger.info { "baseline: $referencePathLength" }
        val MIN_SAVE = 100
        val positions = referencePath.vertexList
        val pb = ProgressBar("outer", (positions.size.toLong() * positions.size.toLong() - 1) / 2)
        for (idx in 0..<positions.size) {
            for (idx2 in (idx + MIN_SAVE)..<positions.size) {
                pb.step()
                val pos1 = positions[idx]
                val pos2 = positions[idx2]
                val distance = distance(pos1, pos2)
                if (distance <= 20) {
                    // valid Shortcut
                    val saved = (idx2 - idx) - distance
                    //logger.info { "$pos1 -> $pos2 => $saved (($idx2 - $idx) - $distance )" }
                    if (saved >= MIN_SAVE) {
                        solution++
                    }
                }
            }
        }

        return solution.toString()
    }

    private fun distance(pos1: Pos, pos2: Pos): Int {
        return abs(pos1.row - pos2.row) + abs(pos1.col - pos2.col)
    }
}

