package com.github.steingrd.kabal

import com.github.steingrd.kabal.TrekkType.*

private val trekkKjørere = mapOf<TrekkType, (Kabal, Trekk)->Kabal>(
        FRA_SPOR_TIL_MÅL to ::fraSporTilMål,
        FRA_SPOR_TIL_SPOR to ::fraSporTilSpor,
        SNU_KORT_I_SPOR to ::snuKortISpor,
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
    return Kabal(kabal.mål, kabal.bunke.snu(), kabal.spor)
}

private fun fraBunkeTilMål(kabal: Kabal, trekk: Trekk): Kabal {
    return Kabal(kabal.mål.motta(kabal.bunke), kabal.bunke.taØversteKort(), kabal.spor)
}

private fun fraBunkeTilSpor(kabal: Kabal, trekk: Trekk): Kabal {
    val kort = kabal.bunke.øversteKort()
    return Kabal(kabal.mål, kabal.bunke.taØversteKort(), kabal.spor.motta(kort, trekk.dest))
}

private fun trekkBunke(kabal: Kabal, trekk: Trekk): Kabal {
    return Kabal(kabal.mål, kabal.bunke.trekkTreKort(), kabal.spor)
}

private fun fraSporTilSpor(kabal: Kabal, trekk: Trekk): Kabal {
    // TODO flytter foreløpig bare fulle topper
    return Kabal(kabal.mål, kabal.bunke, kabal.spor.flyttTopp(trekk.source, trekk.dest))
}

private fun fraSporTilMål(kabal: Kabal, trekk: Trekk): Kabal {
    val kort = kabal.spor.nedersteITopp(trekk.source)
    return Kabal(kabal.mål.motta(kort), kabal.bunke, kabal.spor.fjernNedersteITopp(trekk.source))
}

private fun snuKortISpor(kabal: Kabal, trekk: Trekk): Kabal {
    return Kabal(kabal.mål, kabal.bunke, kabal.spor.snuKortISpor(trekk.source))
}
