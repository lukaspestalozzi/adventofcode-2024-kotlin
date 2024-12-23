import org.jgrapht.alg.clique.PivotBronKerboschCliqueFinder
import org.jgrapht.graph.SimpleGraph


class Day23 : AbstractSolver("23", "3", "co,de,ka,ta") {

    private data class Connection(val c1: String, val c2: String)
    private data class Input(val connections: List<Connection>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val cases: MutableList<Connection> = mutableListOf()
        for (line: String in input) {
            val splitLine = line.split("-")
            val case = Connection(splitLine[0], splitLine[1])
            cases.add(case)
        }
        val createdInput = Input(cases)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): String {
        val input = createInput(inputLines)
        val graph = createInitialGraph(input)

        val cliquesWithT: MutableSet<Set<String>> = mutableSetOf()
        val finder = PivotBronKerboschCliqueFinder<String, Connection>(graph)
        for (clique in finder.asIterable()) {
            if (clique.size >= 3) {
                logger.info { "clique: $clique" }
                val r = enumerate3CountWithTs(clique)
                cliquesWithT.addAll(r)
            }
        }
        return cliquesWithT.size.toString()
    }

    private fun enumerate3CountWithTs(clique: Set<String>): Set<Set<String>> {
        val seen: MutableSet<Set<String>> = mutableSetOf()
        for (c1 in clique) {
            for (c2 in clique) {
                for (c3 in clique) {
                    if (c1 != c2 && c2 != c3 && c1 != c3) {
                        if (c1.startsWith("t") || c2.startsWith("t") || c3.startsWith("t")) {
                            seen.add(setOf(c1, c2, c3))
                        }
                    }
                }
            }
        }
        return seen
    }

    private fun createInitialGraph(input: Input): SimpleGraph<String, Connection> {
        val graph = SimpleGraph<String, Connection>(Connection::class.java)
        for ((c1, c2) in input.connections) {
            graph.addVertex(c1)
            graph.addVertex(c2)
            graph.addEdge(c1, c2, Connection(c1, c2))
        }
        return graph
    }

    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines)
        val graph = createInitialGraph(input)

        val finder = PivotBronKerboschCliqueFinder(graph)
        val theClique: Set<String> = finder.maximumIterator().next()
        return theClique.toList().sorted().joinToString(",")
    }
}

