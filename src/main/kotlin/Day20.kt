import me.tongfei.progressbar.ProgressBar
import org.jgrapht.Graph
import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.alg.shortestpath.YenShortestPathIterator
import org.jgrapht.graph.DefaultDirectedWeightedGraph

class Day20 : AbstractSolver("20", "1497", "") {
    companion object {
        const val WALL = '#'
        const val FLOOR = '.'
        const val L1: Short = 1
        const val L2: Short = 2
        const val LConnect: Short = 0
    }

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

    private data class Node(val pos: Pos, val level: Short)
    private data class Edge(val source: Pos, val target: Pos, val level: Short)

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
        val mazeGraph = createInitialGraph(input)
        val shortcuts = findPossibleShortcuts(input.grid, mazeGraph)
        val startNode = Node(input.start, L1)
        // baseline
        val noShortcut = DijkstraShortestPath(mazeGraph).getPath(startNode, Node(input.end, L1)).weight
        val endNode = Node(input.end, L2)
        var solution: Long = 0
        for ((sourcePos, targetPos) in ProgressBar.wrap(shortcuts, "shortcuts")) {
            val sNode = Node(sourcePos, L1)
            val tNode = Node(targetPos, L1)
            val edge = Edge(sourcePos, targetPos, L1)
            mazeGraph.addEdge(sNode, tNode, edge)
            val algo = DijkstraShortestPath(mazeGraph)
            val w = algo.getPath(startNode, Node(input.end, L1)).weight
            val saved = noShortcut - w
            //logger.info { "path ${w} baseline: $noShortcut -> $saved" }
            if (saved >= 100) {
                solution++
            }
            mazeGraph.removeEdge(edge)
        }
        return solution.toString()
        // connect the lower and upper graph
//        for ((sourcePos, targetPos) in shortcuts) {
//            val sNode = Node(sourcePos, L1)
//            val tNode = Node(targetPos, L2)
//            val edge = Edge(sourcePos, targetPos, LConnect)
//            check(mazeGraph.addEdge(sNode, tNode, edge))
//        }
//
//
//        val algo = YenShortestPathIterator(mazeGraph, startNode, endNode)
//        val paths = mutableSetOf<GraphPath<Node, Edge>>()
//        while (algo.hasNext()) {
//            val path = algo.next()
//            paths.add(path)
//            val saved = noShortcut - path.weight
//            logger.info { "path ${path.weight} baseline: $noShortcut -> $saved" }
//            if (saved > 100) {
//                solution++
//            } else {
//                return solution.toString()
//            }
//        }
//        return solution.toString()
    }

    private fun createInitialGraph(input: Input): DefaultDirectedWeightedGraph<Node, Edge> {
        val grid = input.grid
        val graph = DefaultDirectedWeightedGraph<Node, Edge>(Edge::class.java)
        for (pos in grid.positions()) {
            if (grid.charAt(pos) != WALL) {
                val nodeLevel1 = Node(pos, L1)
                graph.addVertex(nodeLevel1)
                val nodeLevel2 = Node(pos, L2)
                graph.addVertex(nodeLevel2)
                for (d in Direction.entries) {
                    val nabo = pos.move(d)
                    if (grid.charAt(nabo) != WALL) {
                        val naboLevel1 = Node(nabo, L1)
                        graph.addVertex(naboLevel1)
                        graph.addEdge(nodeLevel1, naboLevel1, Edge(source = pos, target = nabo, level = L1))
                        graph.addEdge(naboLevel1, nodeLevel1, Edge(source = nabo, target = pos, level = L1))
                        val naboLevel2 = Node(nabo, L2)
                        graph.addVertex(naboLevel2)
                        graph.addEdge(nodeLevel2, naboLevel2, Edge(source = pos, target = nabo, level = L2))
                        graph.addEdge(naboLevel2, nodeLevel2, Edge(source = nabo, target = pos, level = L2))
                    }
                }
            }
        }
        return graph
    }

    private fun findPossibleShortcuts(
        grid: Grid,
        graph: DefaultDirectedWeightedGraph<Node, Edge>
    ): List<Pair<Pos, Pos>> {
        val shortcuts: MutableList<Pair<Pos, Pos>> = mutableListOf()
        for (pos in grid.positions()) {
            if (grid.charAt(pos) != WALL) {
                for (d in Direction.entries) {
                    val shouldBeWall = pos.move(d)
                    if (grid.charAt(shouldBeWall) == WALL) {
                        val shortcutTarget = shouldBeWall.move(d)
                        if (grid.charAt(shortcutTarget) != WALL) {
                            shortcuts.add(Pair(pos, shortcutTarget))
                        }
                    }
                }
            }
        }
        return shortcuts
    }

    private fun print(graph: Graph<Node, Edge>, dim: Int, level: Short) {
        println("Level: $level")
        for (rowIdx in 0..<dim) {
            for (colIdx in 0..<dim) {
                val nodeL1 = Node(Pos(rowIdx, colIdx), level)
                if (graph.containsVertex(nodeL1)) {
                    print('.')
                } else {
                    print(WALL)
                }
            }
            println()
        }
        println()
    }

    private fun printConnections(graph: Graph<Node, Edge>, dim: Int) {
        for (rowIdx in 0..<dim) {
            for (colIdx in 0..<dim) {
                val pos: Pos = Pos(rowIdx, colIdx)
                val nodeL1 = Node(pos, L1)
                val eStart = graph.edgeSet().filter { it.level == LConnect && it.source == pos }.firstOrNull()
                if (eStart != null) {
                    print('1')
                }
                val eEnd = graph.edgeSet().filter { it.level == LConnect && it.target == pos }.firstOrNull()
                if (eEnd != null) {
                    print('2')
                } else if (graph.containsVertex(nodeL1)) {
                    print('.')
                } else {
                    print(WALL)
                }
            }
            println()
        }
        println()
    }

    override fun solvePart2(inputLines: List<String>): String {
        return ""
        val input = createInput(inputLines, true)
        val mazeGraph = createInitialGraph(input)
        val shortcuts = findPossibleShortcuts(input.grid, mazeGraph)
        val startNode = Node(input.start, L1)
        // baseline
        val noShortcut = DijkstraShortestPath(mazeGraph).getPath(startNode, Node(input.end, L1)).weight
        val endNode = Node(input.end, L2)
        var solution: Long = 0
        for ((sourcePos, targetPos) in ProgressBar.wrap(shortcuts, "shortcuts")) {
            val sNode = Node(sourcePos, L1)
            val tNode = Node(targetPos, L1)
            val edge = Edge(sourcePos, targetPos, L1)
            mazeGraph.addEdge(sNode, tNode, edge)
            val algo = DijkstraShortestPath(mazeGraph)
            val w = algo.getPath(startNode, Node(input.end, L1)).weight
            val saved = noShortcut - w
            //logger.info { "path ${w} baseline: $noShortcut -> $saved" }
            if (saved >= 100) {
                solution++
            }
            mazeGraph.removeEdge(edge)
        }
        return solution.toString()
        // connect the lower and upper graph
//        for ((sourcePos, targetPos) in shortcuts) {
//            val sNode = Node(sourcePos, L1)
//            val tNode = Node(targetPos, L2)
//            val edge = Edge(sourcePos, targetPos, LConnect)
//            check(mazeGraph.addEdge(sNode, tNode, edge))
//        }
//
//
//        val algo = YenShortestPathIterator(mazeGraph, startNode, endNode)
//        val paths = mutableSetOf<GraphPath<Node, Edge>>()
//        while (algo.hasNext()) {
//            val path = algo.next()
//            paths.add(path)
//            val saved = noShortcut - path.weight
//            logger.info { "path ${path.weight} baseline: $noShortcut -> $saved" }
//            if (saved > 100) {
//                solution++
//            } else {
//                return solution.toString()
//            }
//        }
//        return solution.toString()
    }
}

