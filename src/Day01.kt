import kotlin.math.abs

data class Input(val list1: List<Int>, val list2: List<Int>)

fun createInput(input: List<String>): Input {
    val inRegex = """(\d+)\s+(\d+)""".toRegex()
    val list1: MutableList<Int> = mutableListOf()
    val list2: MutableList<Int> = mutableListOf()
    for (line: String in input) {
        val (i1, i2) = inRegex.matchEntire(line)?.destructured ?: throw IllegalArgumentException("${line}")
        list1.add(i1.toInt())
        list2.add(i2.toInt())
    }
    return Input(list1, list2)
}


fun main() {
    fun part1(input: List<String>): Int {
        val (list1, list2) = createInput(input)
        // println(list1)
        // println(list2)
        val sorted1 = list1.sorted()
        val sorted2 = list2.sorted()
        //  println(sorted1)
        //  println(sorted2)

        val solution = sorted1.zip(sorted2).map { (i1, i2) -> abs(i1 - i2) }.sum()

        return solution
    }

    fun part2(input: List<String>): Int {
        val (list1, list2) = createInput(input)
        val groupedList2 = list2.groupBy { i -> i }
        val solution = list1.map { i1 -> (groupedList2.get(i1)?.count() ?: 0) * i1 }.sum()

        return solution
    }

    // Test Input
    val testInput = readInput("Day01_test")
    println("Test Part1:")
    val testSolutionPart1 = part1(testInput)
    testSolutionPart1.println()
    check(testSolutionPart1 == 11)
    println("Test Part2:")
    val testSolutionPart2 = part2(testInput)
    testSolutionPart2.println()
    check(testSolutionPart2 == 31)

    // Solve both parts
    println("---------------")
    val input = readInput("Day01")
    println("Part1:")
    part1(input).println()
    println("Part2:")
    part2(input).println()
}
