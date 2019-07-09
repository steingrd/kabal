package com.github.steingrd.kabal

import com.github.steingrd.kabal.Farge.*

fun main(args: Array<String>) {
    var kabal = delUt()
    //var kabal = lesFraDisk()
    skrivTilDisk(kabal)

    skrivUt(kabal)

    var trekk = finnTrekk(kabal)
    var n = 0;

    while (trekk.isNotEmpty()) {
        println("$n: ${trekk[0]}")
        kabal = kjørTrekk(kabal,trekk[0])
        skrivUt(kabal)

        trekk = finnTrekk(kabal)

        n++;
    }

    skrivUt(kabal)
}

fun delUt(): Kabal {
    val stokk = Kortstokk.alleKort.shuffled()
    val (stokk1, spor1) = lagSpor(stokk, 0)
    val (stokk2, spor2) = lagSpor(stokk1, 1)
    val (stokk3, spor3) = lagSpor(stokk2, 2)
    val (stokk4, spor4) = lagSpor(stokk3, 3)
    val (stokk5, spor5) = lagSpor(stokk4, 4)
    val (stokk6, spor6) = lagSpor(stokk5, 5)
    val (stokkRest, spor7) = lagSpor(stokk6, 6)

    val løsninger = listOf(Mål(HJERTER, emptyList()), Mål(KLØVER, emptyList()), Mål(RUTER, emptyList()), Mål(SPAR, emptyList()))
    val spor = listOf(spor1, spor2, spor3, spor4, spor5, spor6, spor7)

    return Kabal(løsninger, Bunke(emptyList(), stokkRest, true), spor)
}

fun lagSpor(stokk: List<Kort>, antallBunn: Int): Pair<List<Kort>, Spor> {
    val bunn = stokk.takeLast(antallBunn)
    val topp = stokk.dropLast(antallBunn).last()
    return Pair(stokk.dropLast(antallBunn + 1), Spor(bunn, listOf(topp)))
}

