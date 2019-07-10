package com.github.steingrd.kabal

fun main(args: Array<String>) {

    val n = 100000
    val a = mutableMapOf(true to 0, false to 0)

    repeat((0 until n).count()) {
        if (it % 1000 == 0) println("Kjører kabal #${it}")
        val gikkOpp = spillKabal(false)
        a[gikkOpp] = a[gikkOpp]!! + 1
    }

    println("      Kabaler: $a")
    println("     Gikk opp: " + (a[true]?.div(n*1.0) ?: 0))
    println("Gikk ikke opp: " + (a[false]?.div(n*1.0) ?: 0))

}