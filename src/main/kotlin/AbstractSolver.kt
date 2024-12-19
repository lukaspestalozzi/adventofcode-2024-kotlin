import mu.KotlinLogging
import kotlin.io.path.Path
import kotlin.io.path.readText

abstract class AbstractSolver(val dayNr: String, val expectedTestPart1: String, val expectedTestPart2: String) {

    val logger = KotlinLogging.logger {}

    abstract fun solvePart1(inputLines: List<String>): String

    abstract fun solvePart2(inputLines: List<String>): String

    /**
     * Reads lines from the given input txt file.
     */
    fun readInput(testInput: Boolean) =
        Path("src/main/kotlin/Day${dayNr}${if (testInput) "_test" else ""}.txt").readText().trim().lines()

}