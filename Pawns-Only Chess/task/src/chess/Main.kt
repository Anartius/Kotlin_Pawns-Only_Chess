package chess

import kotlin.math.abs

fun main() {
    val field = mutableListOf<MutableList<String>>()
    println("Pawns-Only Chess")
    createField(field)
    printField(field)
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