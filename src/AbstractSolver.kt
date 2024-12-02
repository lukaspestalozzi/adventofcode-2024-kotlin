import mu.KotlinLogging
import kotlin.io.path.Path
import kotlin.io.path.readText

abstract class AbstractSolver(val dayNr: String, val expectedTestPart1: Int, val expectedTestPart2: Int) {

    val logger = KotlinLogging.logger {}

    abstract fun solvePart1(inputLines: List<String>): Int

    abstract fun solvePart2(inputLines: List<String>): Int

    /**
     * Reads lines from the given input txt file.
     */
    fun readInput(testInput: Boolean) =
        Path("src/Day${dayNr}${if (testInput) "_test" else ""}.txt").readText().trim().lines()

}