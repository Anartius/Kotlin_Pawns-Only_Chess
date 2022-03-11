import org.hyperskill.hstest.dynamic.DynamicTest
import org.hyperskill.hstest.stage.StageTest
import org.hyperskill.hstest.testcase.CheckResult
import org.hyperskill.hstest.testing.TestedProgram

val pawnsWhite = List<Pair<Int, Int>>(8) {index -> Pair(1,index) }
val pawnsBlack = List<Pair<Int, Int>>(8) {index -> Pair(6,index) }

class PawnsOnlyChessTest : StageTest<Any>() {
    @DynamicTest
    fun chessboardTest(): CheckResult {
        val main = TestedProgram()
        val outputString = main.start().trim()
        var position = checkOutput(outputString.toLowerCase(), 0, "pawns-only chess")
        if ( position  == -1 ) return CheckResult(false, "Missing program title")
        position = checkChessboard(outputString, position, pawnsWhite, pawnsBlack)
        if ( position  == -1 ) return CheckResult(false, "Wrong chessboard printout")

        return CheckResult.correct()
    }
}

fun checkChessboard(outputString: String, searchPos: Int, pawnsWhite: List<Pair<Int, Int>>, pawnsBlack: List<Pair<Int, Int>>): Int {
    fun createChessboardStringList(pawnsWhite: List<Pair<Int, Int>>, pawnsBlack: List<Pair<Int, Int>>): List<String> {
        var chessboard = "  +---+---+---+---+---+---+---+---+\n"
        for (i in 7 downTo 0) {
            chessboard += "${i + 1} |"
            for (j in 0..7) {
                val square = when {
                    pawnsWhite.contains(Pair(i, j)) -> 'W'
                    pawnsBlack.contains(Pair(i, j)) -> 'B'
                    else -> ' '
                }
                chessboard += " $square |"
            }
            chessboard += "\n  +---+---+---+---+---+---+---+---+\n"
        }
        chessboard += "    a   b   c   d   e   f   g   h\n"
        return chessboard.trim().split("\n").map { it.trim() }
    }
    val chessboardStringList = createChessboardStringList(pawnsWhite, pawnsBlack)
    return checkOutput(outputString, searchPos, * chessboardStringList.toTypedArray())
}

fun checkOutput(outputString: String, searchPos: Int, vararg checkStr: String): Int {
    var searchPosition = searchPos
    for (str in checkStr) {
        val findPosition = outputString.indexOf(str, searchPosition)
        if (findPosition == -1) return -1
        if ( outputString.substring(searchPosition until findPosition).isNotBlank() ) return -1
        searchPosition = findPosition + str.length
    }
    return searchPosition
}


