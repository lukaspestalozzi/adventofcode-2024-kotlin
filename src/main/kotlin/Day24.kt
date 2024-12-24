class Day24 : AbstractSolver("24", "2024", "") {

    private enum class GateType{
        AND, OR, XOR
    }

    private data class WireValue(val name: String, val value: Boolean)
    private data class Gate(val in1: String,val in2: String, val out: String, val type: GateType)
    private data class Input(val gates: List<Gate>, val wires: List<WireValue>)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val gates: MutableList<Gate> = mutableListOf()
        val wires: MutableList<WireValue> = mutableListOf()
        var isGates = false
        for (line: String in input) {
            if(line.isBlank()){
                isGates = true
                continue
            }
            if(isGates){
                val inRegex = """(.*) ([A-Z]+) (.*) -> (.*)""".toRegex()
                val (in1, type, in2, out) =  inRegex.matchEntire(line)?.destructured ?: throw IllegalArgumentException(line)
                gates.add(Gate(in1, in2, out, GateType.valueOf(type)))
            }else{
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
        return when(gate.type){
            GateType.AND -> in1 and in2
            GateType.OR -> in1 or in2
            GateType.XOR -> in1 xor in2
        }
    }

    override fun solvePart1(inputLines: List<String>): String {
        val input = createInput(inputLines, true)
        val wireValues: MutableMap<String, Boolean> = input.wires.associate { it.name to it.value }.toMutableMap()
        var changed = true
        while(changed){
            changed = false
            for(gate in input.gates){
                if(wireValues.keys.contains(gate.in1) && wireValues.keys.contains(gate.in2) && !wireValues.keys.contains(gate.out)){
                    wireValues[gate.out] = evalGate(gate, wireValues)
                    changed = true
                }
            }
        }
        val z = wireValues.keys.filter { it.startsWith("z") }.sorted().reversed()
        logger.info { z }
        val num = z.map { wireValues[it] }.map { if(it!!) 1 else 0 }.joinToString("").toLong(2)
        return num.toString()
    }
    
    override fun solvePart2(inputLines: List<String>): String {
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution.toString()
    }
}

