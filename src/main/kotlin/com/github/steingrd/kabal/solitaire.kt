package com.github.steingrd.kabal

import com.github.steingrd.kabal.Farge.*

fun main(args: Array<String>) {
    var kabal = delUt()
    skrivTilDisk(kabal)
    //var kabal = lesFraDisk()

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
    val (stokk1, spor0) = lagSpor(stokk, 0)
    val (stokk2, spor1) = lagSpor(stokk1, 1)
    val (stokk3, spor2) = lagSpor(stokk2, 2)
    val (stokk4, spor3) = lagSpor(stokk3, 3)
    val (stokk5, spor4) = lagSpor(stokk4, 4)
    val (stokk6, spor5) = lagSpor(stokk5, 5)
    val (stokkRest, spor6) = lagSpor(stokk6, 6)

    val målSpor = mapOf(
            HJERTER to MålSpor(HJERTER, emptyList()),
            KLØVER to MålSpor(KLØVER, emptyList()),
            RUTER to MålSpor(RUTER, emptyList()),
            SPAR to MålSpor(SPAR, emptyList()))

    return Kabal(Mål(målSpor), Bunke(emptyList(), stokkRest, true), SporListe(mapOf(
            0 to spor0,
            1 to spor1,
            2 to spor2,
            3 to spor3,
            4 to spor4,
            5 to spor5,
            6 to spor6
    )))
}

fun lagSpor(stokk: List<Kort>, antallBunn: Int): Pair<List<Kort>, Spor> {
    val bunn = stokk.takeLast(antallBunn)
    val topp = stokk.dropLast(antallBunn).last()
    return Pair(stokk.dropLast(antallBunn + 1), Spor(bunn, listOf(topp)))
}

