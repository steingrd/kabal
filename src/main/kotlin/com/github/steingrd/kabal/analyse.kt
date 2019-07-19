package com.github.steingrd.kabal

import kotlin.math.roundToInt

fun main(args: Array<String>) {

    val n = 100000
    val gikkOpp = mutableMapOf(true to 0, false to 0)
    val antallTrekkGikkOpp = mutableListOf<Int>()
    val antallTrekkGikkIkkeOpp = mutableListOf<Int>()

    repeat((0 until n).count()) {
        if (it % 1000 == 0) println("Kjører kabal #${it}")
        val resultat = delUtOgSpillKabal(false)
        gikkOpp[resultat.gikkOpp] = gikkOpp[resultat.gikkOpp]!! + 1

        if (resultat.gikkOpp)
            antallTrekkGikkOpp.add(resultat.trekk)
        else
            antallTrekkGikkIkkeOpp.add(resultat.trekk)
    }

    println("       Kabaler: $gikkOpp")
    println("      Gikk opp: " + (gikkOpp[true]?.div(n*1.0) ?: 0))
    println(" Gikk ikke opp: " + (gikkOpp[false]?.div(n*1.0) ?: 0))
    println(" ---- Gikk opp ----")
    println("Gj.snitt trekk: " + antallTrekkGikkOpp.average())
    println("  Lavest trekk: " + antallTrekkGikkOpp.min())
    println("  Høyest trekk: " + antallTrekkGikkOpp.max())
    println(" Gikk ikke opp ----")
    println("Gj.snitt trekk: " + antallTrekkGikkIkkeOpp.average().roundToInt())
    println("  Lavest trekk: " + antallTrekkGikkIkkeOpp.min())
    println("  Høyest trekk: " + antallTrekkGikkIkkeOpp.max())

}