package com.github.steingrd.kabal

fun finnTrekk(kabal: Kabal): List<Trekk> {
    val trekk = listOf(
            finnFraSporTilMålTrekk(kabal),
            finnFraBunkeTilMålTrekk(kabal),
            finnFraSporTilSporTrekk(kabal),
            finnSnuKortTrekk(kabal),
            finnFraBunkeTilSporTrekk(kabal),
            finnFraSporTilMålMedOmveiTrekk(kabal)
    ).flatten().filter(::erNullTrekk)

    return when {
        trekk.isEmpty() && kabal.bunke.kanTrekkes() ->
            listOf(Trekk(TrekkType.TREKK_BUNKE, -1, -1))
        trekk.isEmpty() && kabal.bunke.kanSnus() && !kabal.bunke.urørt ->
            listOf(Trekk(TrekkType.SNU_BUNKE, -1, -1))
        else ->
            trekk
    }
}

private fun nullTrekk() = Trekk(TrekkType.NULL_TREKK, -1, -1)

private fun erNullTrekk(it: Trekk) = it.type != TrekkType.NULL_TREKK

private fun finnFraBunkeTilMålTrekk(kabal: Kabal): List<Trekk> {
    if (!kabal.bunke.harSynligeKort()) return emptyList()

    return when {
        kabal.mål.kanMotta(kabal.bunke.øversteKort()) ->
            listOf(Trekk(TrekkType.FRA_BUNKE_TIL_MÅL, -1, -1))
        else ->
            listOf(nullTrekk())
    }
}

private fun finnFraBunkeTilSporTrekk(kabal: Kabal): List<Trekk> {
    if (!kabal.bunke.harSynligeKort()) return emptyList()

    return kabal.spor.liste.map { e ->
        val spor = e.value
        if (spor.kanMotta(kabal.bunke.øversteKort())) {
            Trekk(TrekkType.FRA_BUNKE_TIL_SPOR, -1, e.key)
        } else {
            nullTrekk()
        }
    }
}

private fun finnSnuKortTrekk(kabal: Kabal): List<Trekk> {
    return kabal.spor.liste.map { e ->
        val spor = e.value
        if (spor.kanSnus()) {
            Trekk(TrekkType.SNU_KORT_I_SPOR, e.key, -1)
        } else {
            nullTrekk()
        }
    }
}

private fun finnFraSporTilSporTrekk(kabal: Kabal): List<Trekk> {
    return kabal.spor.liste.map { e ->
        val tilSporIndex = e.key
        val tilSpor = e.value
        kabal.spor.liste.map { f ->
            val fraSporIndex = f.key
            val fraSpor = f.value
            if (fraSpor.topp.isNotEmpty()
                    && fraSporIndex != tilSporIndex
                    && tilSpor.kanMotta(fraSpor.topp)
                    // så vi ikke flytter konge frem og tilbake mellom to spor:
                    && !(fraSpor.bunn.isEmpty() && fraSpor.topp.first().verdi == 13)) {
                Trekk(TrekkType.FRA_SPOR_TIL_SPOR, fraSporIndex, tilSporIndex)
            } else {
                nullTrekk()
            }
        }
    }.flatten()
}

private fun finnFraSporTilMålTrekk(kabal: Kabal): List<Trekk> {
    return kabal.spor.liste.map { e ->
        val spor = e.value
        if (spor.topp.isNotEmpty() && kabal.mål.kanMotta(spor.topp.last())) {
            Trekk(TrekkType.FRA_SPOR_TIL_MÅL, e.key, -1)
        } else {
            nullTrekk()
        }
    }
}

private fun finnFraSporTilMålMedOmveiTrekk(kabal: Kabal): List<Trekk> {
    return kabal.spor.liste.filter { it.value.topp.size > 1 }.map {
        val fraSporIndex = it.key
        val fraSpor = it.value

        val nedersteKort = fraSpor.topp[fraSpor.topp.size - 1]
        val nestNedersteKort = fraSpor.topp[fraSpor.topp.size - 2]

        if (kabal.mål.kanMotta(nestNedersteKort)) {

            kabal.spor.liste.map { t ->
                val tilSporIndex = t.key
                val tilSpor = t.value

                if (tilSpor.kanMotta(nedersteKort)) {
                    val nesteTrekk = Trekk(TrekkType.FRA_SPOR_TIL_MÅL, fraSporIndex, -1)
                    Trekk(TrekkType.FRA_SPOR_TIL_MÅL_MED_OMVEI, fraSporIndex, tilSporIndex, nesteTrekk)
                } else {
                    nullTrekk()
                }
            }

        } else {
            listOf(nullTrekk())
        }
    }.flatten()
}
