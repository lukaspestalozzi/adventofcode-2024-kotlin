import java.util.Collections

class Day17 : AbstractSolver("17", 5730, 117440) {

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
            rA.value = result.toLong()
        }
    }

    private class I6() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val numerator = rA.value
            val denominator: Double = Math.pow(2.0, op.comboValue(rA, rB, rC).toDouble())
            val result = truncateToInt(numerator / denominator) // TODO trucated?
            rB.value = result.toLong()
        }
    }

    private class I7() : Instruction() {
        override fun exec(op: Operand, rA: Register, rB: Register, rC: Register, p: InstructionPointer) {
            val numerator = rA.value
            val denominator: Double = Math.pow(2.0, op.comboValue(rA, rB, rC).toDouble())
            val result = truncateToInt(numerator / denominator) // TODO trucated?
            rC.value = result.toLong()
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
        val result = runProgram(input)
        println("result=$result")
        var solution: Long = result.joinToString("").toLong()
        return solution
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        val valA = input.rA.value
        val valB = input.rB.value
        val valC = input.rC.value
        val target = input.program
        var tryA = -1L
        var result = Collections.emptyList<Int>()
        logger.info { "target=$target" }
        while (result != target) {
            // reset
            input.rA.value = ++tryA
            input.rB.value = valB
            input.rC.value = valC
            iOut.clear()
            // progress
            if(tryA%1000000 == 0L){
                println(tryA)
            }
            if(tryA < 0){
                return -1
            }
            // run
            result = runProgramPart2(input, target)
//            logger.info { "result=$result" }
        }
        var solution: Long = tryA.toLong()
        return solution
    }

    private fun runProgramPart2(input: Input, target: List<Int>): List<Int> {
        var targetIdx = 0
        val program = input.program
        val rA = input.rA
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
            // check and early exit
            if(iOut.out().size > targetIdx){
                if(iOut.out().last() != target[targetIdx]){
                    return iOut.out()
                }
                targetIdx++
            }
        }
        return iOut.out()
    }

    private fun runProgram(input: Input): List<Int> {
        val program = input.program
        val rA = input.rA
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

