import mu.KotlinLogging
import kotlin.io.path.Path
import kotlin.io.path.readText

abstract class AbstractSolver(val dayNr: String, val expectedTestPart1: Number, val expectedTestPart2: Number) {

    val logger = KotlinLogging.logger {}

    abstract fun solvePart1(inputLines: List<String>): Number

    abstract fun solvePart2(inputLines: List<String>): Number

    /**
     * Reads lines from the given input txt file.
     */
    fun readInput(testInput: Boolean) =
        Path("src/Day${dayNr}${if (testInput) "_test" else ""}.txt").readText().trim().lines()

}