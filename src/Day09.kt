class Day09 : AbstractSolver("09", 1928, 0) {

    private data class Input(val memory: List<Any>)
    private data class Page(val id: Int, val size: Int) {
        init {
            check(size > 0) {"$id, $size"}
        }
    }

    private data class Free(val size: Int)

    private fun createInput(input: List<String>, printInput: Boolean = false): Input {
        check(input.size == 1)
        val case: List<Int> = input[0].map { c -> c.digitToInt() }
        val memory = mutableListOf<Any>()
        var idCounter = 0
        var currFree: Boolean = false
        for (n in case) {
            if (currFree) {
                memory.add(Free(size = n))
            } else {
                memory.add(Page(id = idCounter++, size = n))
            }
            currFree = !currFree
        }

        val createdInput = Input(memory)
        if (printInput) logger.debug("{}", createdInput)
        check(case.size == memory.size)
        return createdInput
    }

    override fun solvePart1(inputLines: List<String>): Number {
        val input = createInput(inputLines, printInput = true)
        val memory: MutableList<Any> = input.memory.toMutableList()
        val resultList = mutableListOf<Page>()
        var currIdx = 0
        while (currIdx < memory.size) {
            when (val pf = memory[currIdx]) {
                is Free -> {
                    val free: Free = pf
                    // replace with the page at the end of the memory
                    var freeSize = free.size
                    while (freeSize > 0) {
                        // find last page
                        var last: Any
                        do {
                            last = memory.removeLast()
                        } while (last is Free)
                        val lastPage: Page = if (last is Page) last else throw IllegalArgumentException("not a page")

                        // fit last page into the free slot
                        if (lastPage.size <= freeSize) {
                            resultList.add(lastPage)
                            freeSize -= lastPage.size
                        } else {
                            // split the last page
                            val fittedPage = Page(id = lastPage.id, size = freeSize)
                            val remainingPage = Page(id = lastPage.id, size = lastPage.size - fittedPage.size)
                            resultList.add(fittedPage)
                            memory.add(remainingPage)
                            freeSize = 0
                        }
                    }
                }

                is Page -> {
                    val page: Page = pf
                    resultList.add(page)
                }

                else -> {
                    throw Exception("Invalid elem $pf")
                }
            }
            currIdx++
        }

        logger.info { "result=${resultList}" }
        val solution = calculateChecksum(resultList)
        return solution
        // 6398872404185 too high
    }

    private fun calculateChecksum(list: List<Page>): Long {
        var checksum: Long = 0
        var idx = 0
        for (page in list) {
            for (_i in 0..<page.size) {
                logger.info { "$idx * ${page.id}" }
                checksum =  Math.addExact(checksum, (idx * page.id).toLong())
                idx++
            }
        }
        return checksum
    }

    override fun solvePart2(inputLines: List<String>): Number {
        val input = createInput(inputLines)
        var solution: Long = 0
        return solution
    }
}

