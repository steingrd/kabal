package com.github.steingrd.kabal

fun finnTrekk(kabal: Kabal): List<Trekk> {
    val trekk = listOf(
            finnFraSporTilMålTrekk(kabal),
            finnFraBunkeTilMålTrekk(kabal),
            finnFraSporTilSporTrekk(kabal),
            finnSnuKortTrekk(kabal),
            finnFraBunkeTilSporTrekk(kabal)
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

private fun finnFraBunkeTilMålTrekk(kabal: Kabal): List<Trekk> {
    return if (kabal.bunke.synlig.isEmpty()) {
        emptyList()
    } else {
        kabal.mål.mapIndexed { målIndex, mål ->
            when {
                mål.kanMotta(kabal.bunke.synlig.last()) ->
                    Trekk(TrekkType.FRA_BUNKE_TIL_MÅL, 0, målIndex)
                else -> nullTrekk()
            }
        }.filter(::erNullTrekk)
    }
}

private fun finnFraBunkeTilSporTrekk(kabal: Kabal): List<Trekk> {
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

private fun finnFraSporTilSporTrekk(kabal: Kabal): List<Trekk> {
    return kabal.spor.mapIndexed { tilSporIndex, tilSpor ->
        kabal.spor.mapIndexed { fraSporIndex, fraSpor ->
            if (fraSpor.topp.isNotEmpty()
                    && fraSporIndex != tilSporIndex
                    && tilSpor.kanMotta(fraSpor.topp.first())
                    // spesialcase så vi ikke flytter konge frem og tilbake mellom to spor
                    && !(fraSpor.bunn.isEmpty() && fraSpor.topp.first().verdi == 13)) {
                Trekk(TrekkType.TIL_SPOR, fraSporIndex, tilSporIndex)
            } else {
                nullTrekk()
            }
        }
    }.flatten().filter(::erNullTrekk)
}

private fun finnFraSporTilMålTrekk(kabal: Kabal): List<Trekk> {
    return kabal.spor.mapIndexed { sporIndeks, spor ->
        kabal.mål.mapIndexed { målIndex, mål ->
            when {
                spor.topp.isNotEmpty() && mål.kanMotta(spor.topp.last())
                -> Trekk(TrekkType.TIL_MÅL, source = sporIndeks, dest = målIndex)
                else
                -> nullTrekk()
            }
        }.filter((::erNullTrekk))

    }.flatten()
}