package com.github.steingrd.kabal

import com.github.steingrd.kabal.TrekkType.*

private val trekkKjørere = mapOf<TrekkType, (Kabal, Trekk)->Kabal>(
        FRA_SPOR_TIL_MÅL to ::fraSporTilMål,
        FRA_SPOR_TIL_SPOR to ::fraSporTilSporHeleSporet,
        SNU_KORT_I_SPOR to ::snuKortISpor,
        SNU_BUNKE to ::snuBunke,
        TREKK_BUNKE to ::trekkBunke,
        FRA_BUNKE_TIL_SPOR to ::fraBunkeTilSpor,
        FRA_BUNKE_TIL_MÅL to ::fraBunkeTilMål,
        FRA_SPOR_TIL_MÅL_MED_OMVEI to ::fraSporTilMålMedOmvei
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

private fun fraSporTilSporHeleSporet(kabal: Kabal, trekk: Trekk): Kabal {
    return Kabal(kabal.mål, kabal.bunke, kabal.spor.flyttTopp(trekk.source, trekk.dest))
}

private fun fraSporTilSporNedersteKortet(kabal: Kabal, trekk: Trekk): Kabal {
    return Kabal(kabal.mål, kabal.bunke, kabal.spor.flyttNedersteITopp(trekk.source, trekk.dest))
}

private fun fraSporTilMål(kabal: Kabal, trekk: Trekk): Kabal {
    val kort = kabal.spor.nedersteITopp(trekk.source)
    return Kabal(kabal.mål.motta(kort), kabal.bunke, kabal.spor.fjernNedersteITopp(trekk.source))
}

private fun snuKortISpor(kabal: Kabal, trekk: Trekk): Kabal {
    return Kabal(kabal.mål, kabal.bunke, kabal.spor.snuKortISpor(trekk.source))
}

private fun fraSporTilMålMedOmvei(kabal: Kabal, trekk: Trekk): Kabal {
    if (trekk.neste == null || trekk.ekstra == null)
        error("Mangler neste trekk og ekstra informasjon")

    val subspor = kabal.spor.toppFraIndex(trekk.source, trekk.ekstra as Int)

    val sporliste = kabal.spor
            .flyttToppFraIndex(trekk.source, trekk.ekstra as Int)
            .motta(subspor, trekk.dest)

    return Kabal(kabal.mål, kabal.bunke, sporliste).also {
        fraSporTilMål(it, trekk.neste)
    }
}

