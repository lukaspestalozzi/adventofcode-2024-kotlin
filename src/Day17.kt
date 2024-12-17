import java.lang.Math.pow

class Day17 : AbstractSolver("17", 5730, 0) {

    private data class Register(var value: Long)
    private data class InstructionPointer(var p: Int) {
        fun jump() {
            p += 2
        }
    }

    private data class Operand(val code: Int) {
        fun literalValue(): Int {
            return code
        }

        fun comboValue(rA: Register, rB: Register, rC: Register): Long {
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
        fun truncate(n: Number): Long {
            return n.toLong()
        }

        override fun toString(): String {
            return this.javaClass.simpleName
        }
    }

    private class I0() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val numerator = rA.value
            val denominator: Double = Math.pow(2.0, op.comboValue(rA, rB, rC).toDouble())
            val result = truncate(numerator / denominator)
            rA.value = result
        }
    }

    private class I6() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val numerator = rA.value
            val denominator: Double = Math.pow(2.0, op.comboValue(rA, rB, rC).toDouble())
            val result = truncate(numerator / denominator)
            rB.value = result
        }
    }

    private class I7() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val numerator = rA.value
            val denominator: Double = Math.pow(2.0, op.comboValue(rA, rB, rC).toDouble())
            val result = truncate(numerator / denominator)
            rC.value = result
        }
    }

    private class I1() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val result = rB.value xor op.literalValue().toLong()
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
            if (rA.value != 0L) {
                p.p = op.literalValue()
            }
        }
    }

    private class I5Out() : Instruction() {
        private val out = mutableListOf<Int>()
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val result = op.comboValue(rA, rB, rC) % 8
            out.add(result.toInt())
        }

        fun out(): List<Int> {
            return out
        }

        fun clear() {
            out.clear()
        }
    }


    private data class Input(val program: List<Int>, val rA: Register, val rB: Register, val rC: Register)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        val inputRegisterA: Long = input[0].split(": ")[1].toLong()
        val inputRegisterB: Long = input[1].split(": ")[1].toLong()
        val inputRegisterC: Long = input[2].split(": ")[1].toLong()
        val inputProgram: List<Int> = input[4].split("Program: ")[1].split(",").map { it.toInt() }


        val createdInput = Input(
            program = inputProgram,
            rA = Register(inputRegisterA),
            rB = Register(inputRegisterB),
            rC = Register(inputRegisterC)
        )
        if (printInput) logger.debug("{}", createdInput)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        val result = runProgram(input, valA = input.rA.value)
        println("result=$result")
        var solution: Long = result.joinToString("").toLong()
        return solution
    }

    private fun find(input: Input, valB: Long, valC: Long, a: Long, idx: Int): Long {
        val candidates = mutableListOf<Long>(Long.MAX_VALUE)
        if (idx < 0) {
            return a
        }
        for (i in 0..<8) {
            val tryA: Long = a + i * pow(8.0, idx.toDouble()).toLong()
            val result = runProgram(input, tryA)
            if (result.size < input.program.size) {
                continue
            }
            if (result[idx] == input.program[idx]) {
                val candidate = find(input, valB, valC, tryA, idx - 1)
                candidates.add(candidate)
            }
        }
        return candidates.min()
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        val valB = input.rB.value
        val valC = input.rC.value
        val target = input.program
        val num = find(input, valB, valC, 0, target.size - 1)
        return num
    }

    private fun runProgram(input: Input, valA: Long): List<Int> {
        val program = input.program
        iOut.clear()
        val rA = input.rA
        rA.value = valA
        val rB = input.rB
        val rC = input.rC
        val pointer = InstructionPointer(0)
        while (pointer.p < program.size - 1) {
            val pointerBefore = pointer.p
            val instructionIdx = program[pointer.p]
            val opIdx = program[pointer.p + 1]
            val instruction = instructions[instructionIdx]
            val op = operands[opIdx]
            instruction.exec(op, rA, rB, rC, pointer)
            if (pointerBefore == pointer.p) {
                pointer.jump()
            }
        }
        return iOut.out()
    }
}

