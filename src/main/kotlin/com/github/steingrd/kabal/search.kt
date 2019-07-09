package com.github.steingrd.kabal

fun finnTrekk(kabal: Kabal): List<Trekk> {
    val trekk = listOf(
            finnTilMålTrekk(kabal),
            finnTilSporTrekk(kabal),
            finnSnuKortTrekk(kabal),
            finnFraBunkeTrekk(kabal)
    ).flatten()

    return when {
        trekk.isEmpty() && kabal.bunke.usynlig.isNotEmpty() ->
            listOf(Trekk(TrekkType.TREKK_BUNKE, -1, -1))
        trekk.isEmpty() && kabal.bunke.usynlig.isEmpty() && !kabal.bunke.urørt ->
            listOf(Trekk(TrekkType.SNU_BUNKE, -1, -1))
        else ->
            trekk
    }
}

private fun nullTrekk() = Trekk.NULL_TREKK

private fun erNullTrekk(it: Trekk) = it != Trekk.NULL_TREKK

private fun finnFraBunkeTrekk(kabal: Kabal): List<Trekk> {
    if (kabal.bunke.synlig.isEmpty()) return emptyList()

    return kabal.spor.mapIndexed { tilSporIndex, tilSpor ->
        val kort = kabal.bunke.synlig.last()
        if (tilSpor.kanMotta(kort)) {
            Trekk(TrekkType.FRA_BUNKE_TIL_SPOR, -1, tilSporIndex)
        } else Trekk.NULL_TREKK
    }.filter(::erNullTrekk)
}

private fun finnSnuKortTrekk(kabal: Kabal): List<Trekk> {
    return kabal.spor.mapIndexed { sporIndex, spor ->
        if (spor.topp.isEmpty() && spor.bunn.isNotEmpty()) {
            Trekk(TrekkType.SNU_KORT, sporIndex, -1)
        } else Trekk.NULL_TREKK
    }.filter(::erNullTrekk)
}

private fun finnTilSporTrekk(kabal: Kabal): List<Trekk> {
    return kabal.spor.mapIndexed { tilSporIndex, tilSpor ->
        if (tilSpor.topp.isNotEmpty()) {
            kabal.spor.mapIndexed { fraSporIndex, fraSpor ->
                if (fraSpor.topp.isNotEmpty()
                        && fraSporIndex != tilSporIndex
                        && tilSpor.kanMotta(fraSpor.topp.first())) {
                    Trekk(TrekkType.TIL_SPOR, fraSporIndex, tilSporIndex)
                } else {
                    nullTrekk()
                }
            }.filter(::erNullTrekk)
        } else {
            listOf(nullTrekk())
        }
    }.flatten().filter(::erNullTrekk)
}

private fun finnTilMålTrekk(kabal: Kabal): List<Trekk> {
    return kabal.spor.mapIndexed { sporIndeks, spor ->
        kabal.mål.mapIndexed { målIndex, mål ->
            when {
                spor.topp.isNotEmpty() && mål.kanLeggesPå(spor.topp.last())
                -> Trekk(TrekkType.TIL_MÅL, source = sporIndeks, dest = målIndex)
                else
                -> nullTrekk()
            }
        }.filter((::erNullTrekk))

    }.flatten()
}