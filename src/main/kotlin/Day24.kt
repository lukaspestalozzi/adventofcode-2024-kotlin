import me.tongfei.progressbar.ProgressBar
import org.jgrapht.graph.SimpleDirectedGraph
import org.jgrapht.nio.graphml.GraphMLExporter
import java.io.FileWriter
import java.io.IOException
import kotlin.random.Random.Default.nextBoolean


class Day24 : AbstractSolver("24", "2024", "") {

    private enum class GateType {
        AND, OR, XOR
    }

    private data class WireValue(val name: String, val value: Boolean)
    private data class Gate(val in1: String, val in2: String, val out: String, val type: GateType)
    private data class Input(val gates: List<Gate>, val wires: List<WireValue>)
    private data class Swap(
        val idx1: Int,
        val idx2: Int,
        val origGate1: Gate,
        val origGate2: Gate,
        val swappedGate1: Gate,
        val swappedGate2: Gate
    )

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val gates: MutableList<Gate> = mutableListOf()
        val wires: MutableList<WireValue> = mutableListOf()
        var isGates = false
        for (line: String in input) {
            if (line.isBlank()) {
                isGates = true
                continue
            }
            if (isGates) {
                val inRegex = """(.*) ([A-Z]+) (.*) -> (.*)""".toRegex()
                val (in1, type, in2, out) = inRegex.matchEntire(line)?.destructured ?: throw IllegalArgumentException(
                    line
                )
                gates.add(Gate(in1, in2, out, GateType.valueOf(type)))
            } else {
                val split1 = line.split(": ")
                wires.add(WireValue(name = split1[0], value = split1[1].toInt() > 0))
            }
        }
        val createdInput = Input(gates, wires)
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    private fun evalGate(gate: Gate, wires: Map<String, Boolean>): Boolean {
        val in1 = wires[gate.in1]!!
        val in2 = wires[gate.in2]!!
        return when (gate.type) {
            GateType.AND -> in1 and in2
            GateType.OR -> in1 or in2
            GateType.XOR -> in1 xor in2
        }
    }

    private fun run(gates: List<Gate>, wires: List<WireValue>): Long {
        val wireValues: MutableMap<String, Boolean> = wires.associate { it.name to it.value }.toMutableMap()
        var changed = true
        while (changed) {
            changed = false
            for (gate in gates) {
                if (wireValues.keys.contains(gate.in1) && wireValues.keys.contains(gate.in2) && !wireValues.keys.contains(
                        gate.out
                    )
                ) {
                    wireValues[gate.out] = evalGate(gate, wireValues)
                    changed = true
                }
            }
        }
        val z = wireValues.keys.filter { it.startsWith("z") }.sorted().reversed()
//        logger.info { z }
        val num = z.map { wireValues[it] }.map { if (it!!) 1 else 0 }.joinToString("").toLong(2)
        return num
    }

    override fun solvePart1(inputLines: List<String>): String {
        val input = createInput(inputLines, true)
        val num = run(input.gates, input.wires)
        return num.toString()
    }

    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines)
//        toGraphMl(input)
        val x: Long = input.wires.filter { it.name.startsWith("x") }.sortedBy { it.name }.reversed().map { it.value }
            .map { if (it) 1 else 0 }.joinToString("").toLong(2)
        val y: Long = input.wires.filter { it.name.startsWith("y") }.sortedBy { it.name }.reversed().map { it.value }
            .map { if (it) 1 else 0 }.joinToString("").toLong(2)
        val expectedNum = x + y
        val num = run(input.gates, input.wires)
        logger.info { "$x + $y = $expectedNum but got $num" }

        val expectedBinary = expectedNum.toString(2)
        val numBinary = num.toString(2)
        val diff = (expectedNum xor num).toString(2)
        logger.info { expectedNum.toString(2) }
        logger.info { num.toString(2) }
        logger.info { diff }

        val initialScore = test(input.gates, input.wires)
        logger.info { "initialScore: $initialScore" }
        val gates = input.gates.toMutableList()
        val swap1Candidates: Map<Swap, Int> = findBestSwap(gates, input.wires, initialScore)
        val best = swap1Candidates.minByOrNull { it.value }
        logger.info { "best: $best" }
//        logger.info { "$swap1Candidates" }
        var solution: Long = 0
        return solution.toString()
    }

    // 1:
    // best: Swap(origGate1=Gate(in1=smt, in2=wpp, out=wts, type=XOR), origGate2=Gate(in1=jgw, in2=rhh, out=z37, type=OR), swappedGate1=Gate(in1=smt, in2=wpp, out=z37, type=XOR), swappedGate2=Gate(in1=jgw, in2=rhh, out=wts, type=OR))=479
    // smt XOR wpp -> wts swap with jgw OR rhh -> z37
    // 2:
    // best: Swap(origGate1=Gate(in1=qqw, in2=gkc, out=wpd, type=XOR), origGate2=Gate(in1=gkc, in2=qqw, out=z11, type=AND), swappedGate1=Gate(in1=qqw, in2=gkc, out=z11, type=XOR), swappedGate2=Gate(in1=gkc, in2=qqw, out=wpd, type=AND))=316
    // qqw XOR gkc -> wpd swap with gkc AND qqw -> z11

    private fun findBestSwap(initGates: List<Gate>, initWires: List<WireValue>, scoreToBeat: Int): Map<Swap, Int> {
        val swapCandidates: MutableMap<Swap, Int> = mutableMapOf()
        val pb = ProgressBar("testing swaps", 100)
        for (i in 0..<100) {
            pb.step()
            val wires = initWires.map { WireValue(it.name, nextBoolean()) }
            val gates = initGates.toMutableList()
            for (idx1 in gates.indices) {
                for (idx2 in idx1 + 1 until gates.size) {
                    val gate1 = gates[idx1]
                    val gate2 = gates[idx2]
                    // swap
                    gates[idx1] = Gate(gate1.in1, gate1.in2, gate2.out, gate1.type)
                    gates[idx2] = Gate(gate2.in1, gate2.in2, gate1.out, gate2.type)
                    val swap = Swap(
                        idx1, idx2,
                        origGate1 = gate1,
                        origGate2 = gate2,
                        swappedGate1 = gates[idx1],
                        swappedGate2 = gates[idx2]
                    )

                    val sc = test(gates, wires)
                    swapCandidates[swap] = swapCandidates.getOrDefault(swap, 0) + sc
//                    if (sc == 0) {
//                        logger.error { "Found perfect solution $swap" }
//                    }
//                if (sc < scoreToBeat) {
//                    logger.info { "swapped $idx1, $idx2: $gate1, $gate2" }
//                    logger.info { "Score: $sc" }
//                    swapCandidates[swap] = sc
//                    if (sc == 0) {
//                        logger.error { "Found perfect solution $swap" }
//                    }
//                }
                    // swap back
                    gates[idx1] = gate1
                    gates[idx2] = gate2
                }
            }
        }
        return swapCandidates
    }

    private fun scoreSwap(swap: Swap, gates: MutableList<Gate>, wires: List<WireValue>): Int {
        val idx1 = swap.idx1
        val idx2 = swap.idx2
        val gate1 = gates[idx1]
        val gate2 = gates[idx2]
        // swap
        gates[idx1] = Gate(gate1.in1, gate1.in2, gate2.out, gate1.type)
        gates[idx2] = Gate(gate2.in1, gate2.in2, gate1.out, gate2.type)

        val sc = test(gates, wires)
        // swap back
        gates[idx1] = gate1
        gates[idx2] = gate2
        return sc
    }

    private fun test(gates: List<Gate>, wires: List<WireValue>): Int {
        val x: Long = wires.filter { it.name.startsWith("x") }.sortedBy { it.name }.reversed().map { it.value }
            .map { if (it) 1 else 0 }.joinToString("").toLong(2)
        val y: Long = wires.filter { it.name.startsWith("y") }.sortedBy { it.name }.reversed().map { it.value }
            .map { if (it) 1 else 0 }.joinToString("").toLong(2)
        val expectedNum = x + y
        val num = run(gates, wires)
        //logger.info { "$x + $y = $expectedNum but got $num" }

//        val expectedBinary = expectedNum.toString(2)
//        val numBinary = num.toString(2)
        val diff = (expectedNum xor num).toString(2)
//        logger.info { expectedNum.toString(2)}
//        logger.info { num.toString(2)}
//        logger.info { diff}
        return diff.count { it == '1' }
    }

    private fun createsLoop(gates: List<Gate>, swap: Swap): Boolean {
        val gate1 = swap.origGate1
        val gate2 = swap.origGate2
        val open = mutableSetOf(gate2.out)
        val seen = mutableSetOf<String>()
        while (open.isNotEmpty()) {
            val wire = open.first()
            open.remove(wire)
            if (!seen.add(wire)) {
                return true
            }
            val g = gates.find { it.in1 == wire || it.in2 == wire }
            if (g != null) {
                open.add(g.out)
            }
        }
        return false
    }

    private data class Node(val name: String)
    private data class Edge(val wireName: String, val from: Node, val to: Node)

    private fun toGraphMl(input: Input) {
        val graph = SimpleDirectedGraph<Node, Edge>(Edge::class.java)
        val gateNodes =
            input.gates.associateWith { gate -> Node("${gate.in1} ${gate.type} ${gate.in2} -> ${gate.out}") }
        val wireNodes = input.wires.associate { wire -> wire.name to Node("${wire.name}") }.toMutableMap()
        for (wn in wireNodes.values) {
            graph.addVertex(wn)
        }
        for (gn in gateNodes.values) {
            graph.addVertex(gn)
        }

        for (gate in input.gates) {
            val in1Name = gate.in1
            val in2Name = gate.in2
            val outName = gate.out
            if (wireNodes.containsKey(in1Name)) {
                graph.addEdge(
                    wireNodes[in1Name]!!,
                    gateNodes[gate]!!,
                    Edge(in1Name, wireNodes[in1Name]!!, gateNodes[gate]!!)
                )
            } else {
                val ingate = input.gates.find { it.out == in1Name }
                graph.addEdge(
                    gateNodes[ingate]!!,
                    gateNodes[gate]!!,
                    Edge(in1Name, gateNodes[ingate]!!, gateNodes[gate]!!)
                )
            }
            if (wireNodes.containsKey(in2Name)) {
                graph.addEdge(
                    wireNodes[in2Name]!!,
                    gateNodes[gate]!!,
                    Edge(in2Name, wireNodes[in2Name]!!, gateNodes[gate]!!)
                )
            } else {
                val ingate = input.gates.find { it.out == in2Name }
                graph.addEdge(
                    gateNodes[ingate]!!,
                    gateNodes[gate]!!,
                    Edge(in2Name, gateNodes[ingate]!!, gateNodes[gate]!!)
                )
            }

            if (outName.startsWith("z")) {
                graph.addVertex(Node(outName))
                graph.addEdge(gateNodes[gate]!!, Node(outName), Edge(outName, gateNodes[gate]!!, Node(outName)))
            }

        }

        // Create a GraphML exporter
        val exporter: GraphMLExporter<Node, Edge> = GraphMLExporter { v -> v.name }
        exporter.setExportEdgeLabels(true)
        exporter.setExportVertexLabels(true)


// Export the graph to a file
        try {
            FileWriter("/home/lu/gitrepos/adventofcode/adventofcode-2024-kotlin/src/main/resources/graph.graphml").use { writer ->
                exporter.exportGraph(graph, writer)
            }
        } catch (e: IOException) {
            // Handle exception
        }
    }
}

