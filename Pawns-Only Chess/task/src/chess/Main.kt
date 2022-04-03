package chess
import kotlin.math.abs

class Position(private var state: String) {
    private val regex = "[ BW]".toRegex()

    fun getItState(): String {
        return state
    }

    fun setItState(value: String) {
        state = if (value.matches(regex)) value else "-"
    }
}

data class Player(val name: String,
                  val color: String,
                  var pawnsAmount: Int,
                  var stalemate: Boolean)

fun main() {
    val field = mutableListOf<MutableList<Position>>()
    var firstPlayerTurn = true
    var usersStep:String
    val regex = "[a-h][1-8][a-h][1-8]".toRegex()
    val enPassant = mutableListOf<Int>()

    println("Pawns-Only Chess")
    println("First Player's name:")
    val firstPlayer = Player(readLine()!!, "W", 8, false)
    println("Second Player's name:")
    val secondPlayer = Player(readLine()!!, "B", 8, false)
    createField(field)
    printField(field)

    var player: Player
    while (true) {

        player = if (firstPlayerTurn) firstPlayer else secondPlayer
        println("${player.name}'s turn:")
        usersStep = readLine()!!

        if (usersStep == "exit") {
            println("Bye!")
            return
        } else {
            if (usersStep.matches(regex)) {
                val letters = "abcdefgh"
                val step = listOf(
                    abs(usersStep[1].toString().toInt() - 8),
                    letters.indexOf(usersStep.first()),
                    abs(usersStep.last().toString().toInt() - 8),
                    letters.indexOf(usersStep[2]))

                val from = checkFrom(player, step, field, usersStep)
                val to = checkTo(player, step, field, enPassant)

                if ( from && to) {
                    val prevEnPassant = mutableListOf<Int>()
                    prevEnPassant.addAll(enPassant)

                    enPassant.clear()
                    checkEnPassant(player, step, field, enPassant)
                    makeStep(player, step, field, prevEnPassant)

                    if (checkWin(player, field)) {
                        println("Bye!")
                        return
                    }

                    val nextPlayer = if (!firstPlayerTurn) firstPlayer else secondPlayer

                    if (checkStalemate(nextPlayer, field, enPassant)) {
                        println("Bye!")
                        return
                    }

                    firstPlayerTurn = !firstPlayerTurn

                } else if (from && !to) println("Invalid Input")
            } else println("Invalid Input")
        }
    }
}

fun createField(field: MutableList<MutableList<Position>>) {
    field.add(MutableList(8) { Position(" ") })
    field.add(MutableList(8) { Position("B") })
    for (i in 0..3) {
        field.add(MutableList(8) { Position(" ") })
    }
    field.add(MutableList(8) { Position("W") })
    field.add(MutableList(8) { Position(" ") })
}


fun printField(field: MutableList<MutableList<Position>>) {
    val line = "  " + "+---".repeat(8) + "+"
    val labels = ('a'..'h').toMutableList()

    for (i in field.indices) {
        val currentLine = mutableListOf<String>()
        println(line)
        field[i].forEach { currentLine.add(it.getItState()) }

        println(
            currentLine.joinToString(prefix = "${abs(i - 8)} | ", separator = " | ", postfix = " |")
        )
    }
    println(line)
    println(labels.joinToString(prefix = "    ", separator = "   "))
}


fun checkFrom(player: Player,
              step: List<Int>,
              field: MutableList<MutableList<Position>>,
              usersStep: String): Boolean {

    return when (field[step[0]][step[1]].getItState()) {
        player.color -> true
        else -> {
            println(
                if (player.color == "W") {
                    "No white pawn at ${usersStep.dropLast(2)}"
                } else "No black pawn at ${usersStep.dropLast(2)}"
            )
            false
        }
    }
}


fun checkTo(player: Player,
            step: List<Int>,
            field: MutableList<MutableList<Position>>,
            enPassant: MutableList<Int>): Boolean {

    val fieldState = field[step[2]][step[3]].getItState()
    val stepsAmount = step[0] - step[2]

    if (step[1] == step[3] && fieldState == " ") {
        if (player.color == "B" && stepsAmount in -2..-1) {
            return !(step[0] != 1 && stepsAmount == -2)
        }
        if (player.color == "W" && stepsAmount in 1..2) {
            return !(step[0] != 6 && stepsAmount == 2)
        }
    }

    if ((step[1] == step[3] - 1 || step[1] == step[3] + 1)
        && fieldState != " ") {

        return ((stepsAmount == 1 && fieldState == "B") ||
                (stepsAmount == -1 && fieldState == "W"))
    }

    try {
        if (step[2] == enPassant[0] && step[3] == enPassant[1]) return true
    } catch (e: IndexOutOfBoundsException) {
    }

    return false
}

// Checking for "enPassnat". enPassant<empty cell><destination>
fun checkEnPassant(player: Player,
                   step: List<Int>,
                   field: MutableList<MutableList<Position>>,
                   enPassant: MutableList<Int>) {

    val stepsAmount = step[0] - step[2]

    if (stepsAmount == 2 && player.color == "W") {
        try {
            if (field[step[2]][step[3] - 1].getItState() == "B" ||
                field[step[2]][step[3] + 1].getItState() == "B") {

                enPassant.add(step[2] + 1)
                enPassant.add(step[3])
                enPassant.add(step[2])
                enPassant.add(step[3])
            }
        } catch (e: IndexOutOfBoundsException) {
        }
    }

    if (stepsAmount == -2 && player.color == "B") {

        try {
            if (field[step[2]][step[3] + 1].getItState() == "W" ||
                field[step[2]][step[3] - 1].getItState() == "W") {

                enPassant.add(step[2] - 1)
                enPassant.add(step[3])
                enPassant.add(step[2])
                enPassant.add(step[3])
            }
        } catch (e: IndexOutOfBoundsException) {
        }
    }
}


fun makeStep(player: Player,
             step: List<Int>,
             field: MutableList<MutableList<Position>>,
             enPassant: MutableList<Int>) {

    if (enPassant.size == 4 && step[1] != step[3]) {
        field[enPassant[2]][enPassant[3]].setItState(" ")
    }
    field[step[2]][step[3]].setItState(player.color)
    field[step[0]][step[1]].setItState(" ")
    printField(field)

}


fun checkWin(player: Player,
             field: MutableList<MutableList<Position>>): Boolean {

    var amountOfOpposite = 0

    if (player.color == "W") {

        for (i in field.indices) {
            amountOfOpposite += field[i].count { it.getItState() == "B"}
        }
        if (amountOfOpposite == 0) {
            println("White Wins!")
            return true
        }

        for (i in field[0].indices) {
            if (field[0][i].getItState() == "W") {
                println("White Wins!")
                return true
            }
        }
    }

    if (player.color == "B") {

        amountOfOpposite = 0

        for (i in field.indices) {
            amountOfOpposite += field[i].count { it.getItState() == "W"}
        }
        if (amountOfOpposite == 0) {
            println("Black Wins!")
            return true
        }

        for (i in field[7].indices) {
            if (field[7][i].getItState() == "B") {
                println("Black Wins!")
                return true
            }
        }
    }

    return false
}


fun checkStalemate(player: Player,
                   field: MutableList<MutableList<Position>>,
                   enPassant: MutableList<Int>): Boolean {

    var noValidMovieCounter = 0
    var playerPawnsCounter = 0
    var toLeft = false
    var toRight = false

    if (player.color == "W") {
        for (i in field.indices) {

            // Counting pawns that hasn't any valid movie
            for (j in field[i].indices) {

                if (field[i][j].getItState() == "W") {
                    playerPawnsCounter++

                    try {
                        if (field[i - 1][j - 1].getItState() == "B" ||
                            i - 1 == enPassant[0] && j - 1 == enPassant[1]) {
                            toLeft = true
                        }
                    } catch (e: IndexOutOfBoundsException) {
                    }

                    try {
                        if (field[i - 1][j + 1].getItState() == "B" ||
                            i - 1 == enPassant[0] && j + 1 == enPassant[1]) toRight = true
                    } catch (e: IndexOutOfBoundsException) {
                    }

                    if (field[i - 1][j].getItState() != " " &&
                        !toLeft && !toRight
                    ) {

                        noValidMovieCounter++
                    }
                }
            }
        }

        if (playerPawnsCounter == noValidMovieCounter) {
            println("Stalemate!")
            return true
        }
    }

    if (player.color == "B") {
        for (i in field.indices) {

            // Counting pawns that hasn't any valid movie
            for (j in field[i].indices) {

                if (field[i][j].getItState() == "B") {
                    playerPawnsCounter++

                    try {
                        if (field[i + 1][j - 1].getItState() == "W" ||
                            i + 1 == enPassant[0] && j - 1 == enPassant[1]) {
                            toLeft = true
                        }
                    } catch (e: IndexOutOfBoundsException) {
                    }

                    try {
                        if (field[i + 1][j + 1].getItState() == "W" ||
                            i + 1 == enPassant[0] && j + 1 == enPassant[1]) toRight = true
                    } catch (e: IndexOutOfBoundsException) {
                    }

                    if (field[i + 1][j].getItState() != " " &&
                        !toLeft && !toRight
                    ) {

                        noValidMovieCounter++
                    }
                }
            }
        }

        if (playerPawnsCounter == noValidMovieCounter) {
            println("Stalemate!")
            return true
        }
    }

    return false
}