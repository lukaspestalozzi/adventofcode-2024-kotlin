class Day17 : AbstractSolver("17", 1, 0) {

    private data class Register(var value: Int, val name: String)
    private data class InstructionPointer(var p: Int) {
        fun jump() {
            p += 2
        }
    }

    private data class Operand(val code: Int) {
        fun literalValue(): Int {
            return code
        }

        fun comboValue(rA: Register, rB: Register, rC: Register): Int {
            return when (code) {
                0 -> 0
                1 -> 1
                2 -> 2
                3 -> 3
                4 -> rA.value
                5 -> rB.value
                6 -> rC.value
                7 -> throw IllegalArgumentException("$code")
                else -> throw IllegalArgumentException("$code")
            }
        }
    }

    private val operands = listOf(
        Operand(0), Operand(1), Operand(2), Operand(3), Operand(4), Operand(5), Operand(6), Operand(7)
    )
    private val iOut = I5Out()
    private val instructions = listOf(
        I0(), I1(), I2(), I3(), I4(), iOut, I6(), I7()
    )

    private abstract class Instruction {
        abstract fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer)
        fun truncateToInt(n: Number): Int {
            return n.toInt()
        }

        override fun toString(): String {
            return this.javaClass.simpleName
        }
    }

    private class I0() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val numerator = rA.value
            val denominator: Double = Math.pow(2.0, op.comboValue(rA, rB, rC).toDouble())
            val result = truncateToInt(numerator / denominator) // TODO trucated?
            rA.value = result
        }
    }

    private class I6() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val numerator = rA.value
            val denominator: Double = Math.pow(2.0, op.comboValue(rA, rB, rC).toDouble())
            val result = truncateToInt(numerator / denominator) // TODO trucated?
            rB.value = result
        }
    }

    private class I7() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val numerator = rA.value
            val denominator: Double = Math.pow(2.0, op.comboValue(rA, rB, rC).toDouble())
            val result = truncateToInt(numerator / denominator) // TODO trucated?
            rC.value = result
        }
    }

    private class I1() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val result = rB.value xor op.literalValue()
            rB.value = result
        }
    }

    private class I4() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val result = rB.value xor rC.value
            rB.value = result
        }
    }

    private class I2() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val result = op.comboValue(rA, rB, rC) % 8
            rB.value = result
        }
    }

    private class I3() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            if (rA.value != 0) {
                p.p = op.literalValue()
            }
        }
    }

    private class I5Out() : Instruction() {
        private val out = mutableListOf<Int>()
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val result = op.comboValue(rA, rB, rC) % 8
            out.add(result)
        }

        fun joinOutAndClear(): String {
            val s = out.joinToString(",")
            out.clear()
            return s
        }
    }


    private data class Input(val program: List<Int>, val rA: Register, val rB: Register, val rC: Register)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val inputRegisterA: Int = input[0].split(": ")[1].toInt()
        val inputRegisterB: Int = input[1].split(": ")[1].toInt()
        val inputRegisterC: Int = input[2].split(": ")[1].toInt()
        val inputProgram: List<Int> = input[4].split("Program: ")[1].split(",").map { it.toInt() }


        val createdInput = Input(
            program = inputProgram,
            rA = Register(inputRegisterA, "A"),
            rB = Register(inputRegisterB, "B"),
            rC = Register(inputRegisterC, "C")
        )
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): Number {
        val input = createInput(inputLines, true)
        val program = input.program
        val rA = input.rA
        val rB = input.rB
        val rC = input.rC
        val pointer = InstructionPointer(0)
        while (pointer.p < program.size - 1) {
            val pointerBefore = pointer.p
            logger.info { "$pointer" }
            val instructionIdx = program[pointer.p]
            val opIdx = program[pointer.p+1]
            val instruction = instructions[instructionIdx]
            val op = operands[opIdx]
            logger.info { "$instructionIdx -> $instruction with $opIdx -> $op" }
            instruction.exec(op, rA, rB, rC, pointer)
            if (pointerBefore == pointer.p) {
                pointer.jump()
                logger.info { "jump -> $pointer" }
            }else{
                logger.info { "no jump -> $pointer" }
            }
        }
        logger.info { "halt: $pointer" }
        println("result=${ iOut.joinOutAndClear()}")

        var solution: Long = 1
        return solution
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution
    }
}

