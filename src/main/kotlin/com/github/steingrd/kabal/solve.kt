package com.github.steingrd.kabal

import com.github.steingrd.kabal.TrekkType.*
import kotlin.math.min

private val trekkKjørere = mapOf<TrekkType, (Kabal, Trekk)->Kabal>(
        TIL_MÅL to ::tilMålFraSpor,
        TIL_SPOR to ::tilSpor,
        SNU_KORT to ::snuKort,
        SNU_BUNKE to ::snuBunke,
        TREKK_BUNKE to ::trekkBunke,
        FRA_BUNKE_TIL_SPOR to ::fraBunkeTilSpor,
        FRA_BUNKE_TIL_MÅL to ::fraBunkeTilMål
)

fun kjørTrekk(kabal: Kabal, trekk: Trekk): Kabal {
    assert(trekkKjørere.containsKey(trekk.type))
    val handler = trekkKjørere[trekk.type]
    return handler?.invoke(kabal, trekk) ?: kabal
}

private fun snuBunke(kabal: Kabal, trekk: Trekk): Kabal {
    return Kabal(kabal.mål, Bunke(emptyList(), kabal.bunke.synlig.reversed(), true), kabal.spor)
}

private fun fraBunkeTilMål(kabal: Kabal, trekk: Trekk): Kabal {
    val kort = kabal.bunke.synlig.last()

    val bunke = Bunke(kabal.bunke.synlig.dropLast(1), kabal.bunke.usynlig, false)

    return Kabal(kabal.mål.motta(kort), bunke, kabal.spor)
}

private fun fraBunkeTilSpor(kabal: Kabal, trekk: Trekk): Kabal {
    val kort = kabal.bunke.synlig.last()

    val bunke = Bunke(kabal.bunke.synlig.dropLast(1), kabal.bunke.usynlig, false)

    val spor = listOf(
            leggKortFraBunkeTilSpor(kabal, trekk, 0, kort),
            leggKortFraBunkeTilSpor(kabal, trekk, 1, kort),
            leggKortFraBunkeTilSpor(kabal, trekk, 2, kort),
            leggKortFraBunkeTilSpor(kabal, trekk, 3, kort),
            leggKortFraBunkeTilSpor(kabal, trekk, 4, kort),
            leggKortFraBunkeTilSpor(kabal, trekk, 5, kort),
            leggKortFraBunkeTilSpor(kabal, trekk, 6, kort))

    return Kabal(kabal.mål, bunke, spor)
}

private fun trekkBunke(kabal: Kabal, trekk: Trekk): Kabal {
    val treKort = kabal.bunke.usynlig.takeLast(min(3, kabal.bunke.usynlig.size))
    val usynligEtterAtTreKortErTatt = kabal.bunke.usynlig.dropLast(3)
    val bunke = Bunke(kabal.bunke.synlig.plus(treKort), usynligEtterAtTreKortErTatt, kabal.bunke.urørt)
    return Kabal(kabal.mål, bunke, kabal.spor)
}

private fun tilSpor(kabal: Kabal, trekk: Trekk): Kabal {
    val spor = listOf(
            leggPåEllerFlyttKortIToppForTrekk(kabal, trekk, 0),
            leggPåEllerFlyttKortIToppForTrekk(kabal, trekk, 1),
            leggPåEllerFlyttKortIToppForTrekk(kabal, trekk, 2),
            leggPåEllerFlyttKortIToppForTrekk(kabal, trekk, 3),
            leggPåEllerFlyttKortIToppForTrekk(kabal, trekk, 4),
            leggPåEllerFlyttKortIToppForTrekk(kabal, trekk, 5),
            leggPåEllerFlyttKortIToppForTrekk(kabal, trekk, 6))

    return Kabal(kabal.mål, kabal.bunke, spor)
}

private fun tilMålFraSpor(kabal: Kabal, trekk: Trekk): Kabal {
    val kort = kabal.spor[trekk.source].topp.last()
    val mål = kabal.mål.motta(kort)

    val spor = listOf(
            fjernKortIToppForTrekk(kabal, trekk, 0),
            fjernKortIToppForTrekk(kabal, trekk, 1),
            fjernKortIToppForTrekk(kabal, trekk, 2),
            fjernKortIToppForTrekk(kabal, trekk, 3),
            fjernKortIToppForTrekk(kabal, trekk, 4),
            fjernKortIToppForTrekk(kabal, trekk, 5),
            fjernKortIToppForTrekk(kabal, trekk, 6))

    return Kabal(mål, kabal.bunke, spor)
}

private fun snuKort(kabal: Kabal, trekk: Trekk): Kabal {
    assert(kabal.spor[trekk.source].topp.isEmpty())
    assert(kabal.spor[trekk.source].bunn.isNotEmpty())

    return Kabal(kabal.mål, kabal.bunke, listOf(
            snuKortISporForTrekk(kabal, trekk, 0),
            snuKortISporForTrekk(kabal, trekk, 1),
            snuKortISporForTrekk(kabal, trekk, 2),
            snuKortISporForTrekk(kabal, trekk, 3),
            snuKortISporForTrekk(kabal, trekk, 4),
            snuKortISporForTrekk(kabal, trekk, 5),
            snuKortISporForTrekk(kabal, trekk, 6)))
}

fun leggKortFraBunkeTilSpor(kabal: Kabal, trekk: Trekk, sporIndex: Int, kort: Kort): Spor {
    val spor = kabal.spor[sporIndex]
    return if (trekk.dest == sporIndex) {
        Spor(spor.bunn, spor.topp.plus(kort))
    } else {
        spor
    }
}


private fun leggPåEllerFlyttKortIToppForTrekk(kabal: Kabal, trekk: Trekk, sporIndex: Int): Spor {
    val spor = kabal.spor[sporIndex]

    // TODO flytter foreløpig bare fulle topper

    return when (sporIndex) {
        trekk.source ->
            Spor(spor.bunn, emptyList())
        trekk.dest -> {
            val flyttes = kabal.spor[trekk.source].topp
            Spor(spor.bunn, spor.topp.plus(flyttes))
        }
        else -> spor
    }
}

private fun fjernKortIToppForTrekk(kabal: Kabal, trekk: Trekk, sporIndex: Int): Spor {
    val spor = kabal.spor[sporIndex]

    return if (trekk.source == sporIndex) {
        val topp = spor.topp.subList(0, spor.topp.size - 1)
        Spor(spor.bunn, topp)
    } else {
        spor
    }
}

private fun snuKortISporForTrekk(kabal: Kabal, trekk: Trekk, sporIndex: Int): Spor {
    val spor = kabal.spor[sporIndex]

    return if (trekk.source == sporIndex) {
        val topp = listOf(spor.bunn.last())
        val bunn = spor.bunn.dropLast(1)
        Spor(bunn, topp)
    } else {
        spor
    }
}