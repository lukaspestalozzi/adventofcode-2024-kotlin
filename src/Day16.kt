import com.mxgraph.layout.hierarchical.mxHierarchicalLayout
import com.mxgraph.layout.mxIGraphLayout
import com.mxgraph.util.mxCellRenderer
import org.jgrapht.Graph
import org.jgrapht.GraphPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.alg.shortestpath.EppsteinShortestPathIterator
import org.jgrapht.alg.shortestpath.YenKShortestPath
import org.jgrapht.ext.JGraphXAdapter
import org.jgrapht.graph.SimpleWeightedGraph
import java.awt.Color
import java.io.File
import java.io.FileWriter
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class Day16 : AbstractSolver("16", 7036, 45) {
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

    private data class Node(val pos: Pos, val dir: Direction)
    private data class Edge(val source: Pos, val target: Pos, val dir: Direction)

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

        fun findStart(): Pos {
            return positions().first { charAt(it) == 'S' }
        }

        fun findEnd(): Pos {
            return positions().first { charAt(it) == 'E' }
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

    override fun solvePart1(inputLines: List<String>): Number {
        val input = createInput(inputLines, true)
        val graph = createGraph(input)
        // logger.info { "$graph" }
//        viz(graph)
        val startPos = input.grid.findStart()
        val endPos = input.grid.findEnd()
        val endNodes = graph.vertexSet().filter { it.pos == endPos }
        val algo = DijkstraShortestPath(graph)
        val startNode = Node(startPos, Direction.RIGHT)
        var min = Double.MAX_VALUE
        val paths = algo.getPaths(startNode)
        for (endNode in endNodes) {
            val path = paths.getPath(endNode)
            logger.info { "w=${path.weight}" }
            min = if (min > path.weight) path.weight else min
        }

        var solution: Long = min.toLong()
        return solution
    }

    private fun createGraph(input: Input): Graph<Node, Edge> {
        val maze = input.grid
        val graph = SimpleWeightedGraph<Node, Edge>(Edge::class.java)
        val open = mutableSetOf<Node>()
        val seen = mutableSetOf<Node>()
        val startPos = maze.findStart()
        val endPos = maze.findEnd()
        open.add(Node(startPos, Direction.RIGHT))
        while (open.isNotEmpty()) {
            val curr: Node = open.first()
            open.remove(curr)
            if (seen.contains(curr)) {
                continue
            }
            seen.add(curr)
            addVerticesIfCanMoveInDir(maze, graph, curr, curr.dir)?.let { open.add(it) }
            if (curr.pos != endPos) {
                addVerticesIfCanMoveInDir(maze, graph, curr, curr.dir.rotateRight())?.let { open.add(it) }
                addVerticesIfCanMoveInDir(maze, graph, curr, curr.dir.rotateLeft())?.let { open.add(it) }
            }
        }
        return graph
    }

    private fun addVerticesIfCanMoveInDir(
        maze: Grid,
        graph: Graph<Node, Edge>,
        curr: Node,
        dir: Direction
    ): Node? {
        val nextInDir: Pos = curr.pos.move(dir)
        if (maze.charAt(nextInDir) == WALL) {
            return null
        }
        var prevV = curr
        if (dir != curr.dir) {
            // add node for turning
            val turnV = Node(curr.pos, dir)
            val edge = Edge(curr.pos, curr.pos, dir)
            prevV = turnV
            graph.addVertex(curr)
            graph.addVertex(turnV)
            if (graph.addEdge(curr, turnV, edge)) {
                graph.setEdgeWeight(edge, 1000.0)
            }

        }
        // add node for moving
        val moveV = Node(nextInDir, dir)
        val edge = Edge(prevV.pos, moveV.pos, dir)
        graph.addVertex(prevV)
        graph.addVertex(moveV)
        graph.addEdge(prevV, moveV, edge)
        graph.setEdgeWeight(edge, 1.0)
        return moveV
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines, true)
        val graph = createGraph(input)
        // logger.info { "$graph" }
        // viz(graph)
        val startPos = input.grid.findStart()
        val startNode = Node(startPos, Direction.RIGHT)
        val endPos = input.grid.findEnd()
        val endNodes = graph.vertexSet().filter { it.pos == endPos }
        var min = Double.MAX_VALUE
        val bestPaths: MutableList<GraphPath<Node, Edge>> = mutableListOf()
        val algo = YenKShortestPath(graph)
        for (endNode in endNodes) {
            val paths = algo.getPaths(startNode, endNode, 100)
            for (path in paths) {
                if (min > path.weight) {
                    min = path.weight
                    bestPaths.clear()
                }
                if (min == path.weight) {
                    bestPaths.add(path)
                }
                if (path.weight > min) {
                    continue
                }
                logger.info { "w=${path.weight}" }
            }
        }
        val positions = bestPaths.flatMap { path -> path.vertexList.map { it.pos } }.toSet()

        var solution: Long = positions.size.toLong()

        return solution
    }

    private fun viz(g: Graph<Node, Edge>) {
        val graphAdapter = JGraphXAdapter<Node, Edge>(g)
        val layout: mxIGraphLayout = mxHierarchicalLayout(graphAdapter)
        layout.execute(graphAdapter.defaultParent)

        val document =
            mxCellRenderer.createSvgDocument(graphAdapter, null, 1.0, Color.WHITE, null)

        val transformer: Transformer = TransformerFactory.newInstance().newTransformer()
        val source = DOMSource(document)
        val writer: FileWriter =
            FileWriter(File("/home/lu/gitrepos/adventofcode/adventofcode-2024-kotlin/tmp/graph.svg"))
        val result = StreamResult(writer)
        transformer.transform(source, result);
    }
}

