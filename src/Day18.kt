import org.jgrapht.Graph
import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.SimpleWeightedGraph

class Day18 : AbstractSolver("18", "22", "6,1") {
    companion object {
        const val WALL = '#'
    }

    private data class Case(val pos: Pos)
    private data class Input(val cases: List<Case>, val dim: Int)
    private enum class Direction {
        UP, DOWN, LEFT, RIGHT;
    }

    private data class Pos(val x: Int, val y: Int) {

        fun move(direction: Direction): Pos {
            return when (direction) {
                Direction.UP -> Pos(x - 1, y)
                Direction.DOWN -> Pos(x + 1, y)
                Direction.LEFT -> Pos(x, y - 1)
                Direction.RIGHT -> Pos(x, y + 1)
            }
        }
    }

    private data class Edge(val source: Pos, val target: Pos)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val cases: MutableList<Case> = mutableListOf()
        var dim = 6 + 1
        for (line: String in input) {
            val split = line.split(",")
            val case = Case(Pos(x = split[0].toInt(), y = split[1].toInt()))
            if (case.pos.x > dim) {
                dim = 70 + 1
            }
            cases.add(case)
        }
        val createdInput = Input(cases, dim = dim)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    private fun print(graph: Graph<Pos, Edge>, dim: Int) {
        val algo = DijkstraShortestPath(graph)
        val startNode = Pos(0, 0)
        val endNode = Pos(dim - 1, dim - 1)
        val path = algo.getPath(startNode, endNode)
        val pathPos = path.vertexList.toSet()
        for (rowIdx in 0..<dim) {
            for (colIdx in 0..<dim) {
                val pos = Pos(rowIdx, colIdx)
                if (pathPos.contains(pos)) {
                    print('O')
                } else if (graph.containsVertex(pos)) {
                    print('.')
                } else {
                    print(WALL)
                }
            }
            println()
        }
        println()
    }

    private fun positions(dim: Int): List<Pos> {
        val poses = mutableListOf<Pos>()
        for (rowIdx in 0..<dim) {
            for (colIdx in 0..<dim) {
                poses.add(Pos(rowIdx, colIdx))
            }
        }
        return poses
    }

    override fun solvePart1(inputLines: List<String>): String {
        val input = createInput(inputLines)
        val graph = createGraph(input)
        val take = if (input.dim < 10) 12 else 1024
        for (bit in input.cases.take(take)) {
            graph.removeVertex(bit.pos)
        }
        val algo = DijkstraShortestPath(graph)
        val startNode = Pos(0, 0)
        val endNode = Pos(input.dim - 1, input.dim - 1)
        val path = algo.getPath(startNode, endNode)
        print(graph, dim = input.dim)

        var solution: Long = path.weight.toLong()
        return solution.toString()
    }

    private fun createGraph(input: Input): Graph<Pos, Edge> {
        val graph = SimpleWeightedGraph<Pos, Edge>(Edge::class.java)
        for (pos in positions(input.dim)) {
            graph.addVertex(pos)
            for (d in Direction.entries) {
                val nabo = pos.move(d)
                if (nabo.x >= 0 && nabo.x < input.dim && nabo.y >= 0 && nabo.y < input.dim) {
                    graph.addVertex(nabo)
                    graph.addEdge(pos, nabo, Edge(pos, nabo))
                }
            }
        }

        return graph
    }

    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines)
        val graph = createGraph(input)
        val take = if (input.dim < 10) 12 else 1024
        for (bit in input.cases.take(take)) {
            graph.removeVertex(bit.pos)
        }
        val algo = DijkstraShortestPath(graph)
        val startNode = Pos(0, 0)
        val endNode = Pos(input.dim - 1, input.dim - 1)
        var path = algo.getPath(startNode, endNode)
        var pathSet = path.vertexList.toSet()
        for (i in (take + 1)..<input.cases.size) {
            val bit = input.cases[i]
            graph.removeVertex(bit.pos)
            if (pathSet.contains(bit.pos)) {
                path = DijkstraShortestPath(graph).getPath(startNode, endNode)
                if (path == null) {
                    return "${bit.pos.x},${bit.pos.y}"
                }
                pathSet = path.vertexList.toSet()
            }
        }
        return ""
    }
}

