package chess

import kotlin.math.abs

fun main() {
    val field = mutableListOf<MutableList<String>>()
    var firstPlayerTurn = true
    var move:String
    val regex = "[a-h][1-8][a-h][1-8]".toRegex()

    println("Pawns-Only Chess")
    println("First Player's name:")
    val firstPlayer = readLine()!!
    println("Second Player's name:")
    val secondPlayer = readLine()!!
    createField(field)
    printField(field)

    while (true) {
        if (firstPlayerTurn) {
            println("$firstPlayer's turn:")
        } else println("$secondPlayer's turn:")
        move = readLine()!!
        if (move == "exit") {
                println("Bye!")
                return
        } else {
            if (move.matches(regex)) {
                firstPlayerTurn = !firstPlayerTurn
            } else println("Invalid Input")
        }
    }
}


fun createField(field: MutableList<MutableList<String>>) {
    val gaps = MutableList(8) { " " }
    field.add(gaps)
    field.add(MutableList(8) { "B" })
    for (i in 0..3) {
        field.add(gaps)
    }
    field.add(MutableList(8) { "W" })
    field.add(gaps)
}

fun printField(field: MutableList<MutableList<String>>) {
    val line = "  " + "+---".repeat(8) + "+"
    val labels = ('a'..'h').toMutableList()
    for (i in field.indices) {
        println(line)
        println(
            field[i].joinToString(prefix = "${abs(i - 8)} | ", separator = " | ", postfix = " |")
        )
    }
    println(line)
    println(labels.joinToString(prefix = "    ", separator = "   "))
}