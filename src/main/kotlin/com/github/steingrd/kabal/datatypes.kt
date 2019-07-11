package com.github.steingrd.kabal

import kotlin.math.min


enum class Farge {
    HJERTER,
    SPAR,
    RUTER,
    KLØVER;

    fun erMotsattAv(farge: Farge): Boolean {
        return when (farge) {
            HJERTER, RUTER -> this == SPAR || this == KLØVER
            SPAR, KLØVER -> this == HJERTER || this == RUTER
        }
    }
}

class Kortstokk {

    companion object {
        val alleKort = listOf<Kort>()
                .plus((0 until 13).map { Kort(Farge.HJERTER, it+1) })
                .plus((0 until 13).map { Kort(Farge.RUTER, it+1) })
                .plus((0 until 13).map { Kort(Farge.KLØVER, it+1) })
                .plus((0 until 13).map { Kort(Farge.SPAR, it+1) })
    }

}

class Kort(val farge: Farge, val verdi: Int) {

    override fun toString(): String =
            "${farge.name.toUpperCase().substring(0, 4)}_${verdi.toString().padStart(2, '0')}"

}

class Kabal(val mål: Mål, val bunke: Bunke, val spor: SporListe) {
    fun erFerdig(): Boolean = mål.erFerdig()
}

class Mål(val målSpor: Map<Farge, MålSpor>) {

    fun kanMotta(k: Kort): Boolean = målSpor.values.any { it.kanMotta(k) }

    fun motta(kort: Kort): Mål {
        val sporetSomMottar = målSpor[kort.farge] ?: error("Ugyldig farge ${kort.farge}")
        return Mål(målSpor.filterKeys { f -> f != kort.farge }.plus(kort.farge to sporetSomMottar.motta(kort)))
    }

    fun motta(bunke: Bunke): Mål = motta(bunke.øversteKort())

    fun erFerdig(): Boolean = målSpor.all { e -> e.value.kort.size == 13}
}

class MålSpor(val farge: Farge, val kort: List<Kort>) {
    fun kanMotta(k: Kort): Boolean =
            farge == k.farge
                    && ((kort.isEmpty() && k.verdi == 1)
                    || kort.isNotEmpty() && k.verdi == kort.last().verdi + 1)

    fun motta(k: Kort): MålSpor = MålSpor(farge, kort.plus(k))
}

class Bunke(private val synlig: List<Kort>, private val usynlig: List<Kort>, val urørt: Boolean) {

    fun trekkTreKort(): Bunke {
        val treKort = usynlig.takeLast(min(3, usynlig.size))
        val usynligEtterAtTreKortErTatt = usynlig.dropLast(3)
        return Bunke(synlig.plus(treKort), usynligEtterAtTreKortErTatt, urørt)
    }

    fun kanTrekkes(): Boolean = usynlig.isNotEmpty()

    fun kanSnus(): Boolean = usynlig.isEmpty() && synlig.isNotEmpty()

    fun snu(): Bunke = Bunke(emptyList(), synlig.reversed(), true)

    fun øversteKort(): Kort = synlig.last()

    fun taØversteKort(): Bunke = Bunke(synlig.dropLast(1), usynlig, false)

    fun harSynligeKort(): Boolean = synlig.isNotEmpty()

    override fun toString(): String {
        return (if (synlig.isEmpty()) "[-----]" else synlig.last().toString()) + " " + usynlig.size + " urørt: " + urørt
    }

}

class SporListe(val liste: Map<Int, Spor>) {

    fun motta(kort: Kort, tilIndex: Int): SporListe = motta(listOf(kort), tilIndex)

    private fun motta(kort: List<Kort>, tilIndex: Int): SporListe {
        val sporetSomMottar = liste[tilIndex] ?: error("Ugyldig index $tilIndex")
        return SporListe(liste.filterKeys { i ->  i != tilIndex }.plus(tilIndex to sporetSomMottar.motta(kort)))
    }

    fun flyttTopp(fraIndex: Int, tilIndex: Int): SporListe {
        val sporetSomMottar = liste[tilIndex] ?: error("Ugyldig index $tilIndex")
        val sporetSomFlyttesFra = liste[fraIndex] ?: error("Ugyldig index $fraIndex")
        val kort = sporetSomFlyttesFra.topp

        return SporListe(liste.filterKeys { i -> i != fraIndex && i != tilIndex }
                .plus(fraIndex to sporetSomFlyttesFra.flyttTopp())
                .plus(tilIndex to sporetSomMottar.motta(kort)))
    }

    fun flyttNedersteITopp(fraIndex: Int, tilIndex: Int): SporListe {
        val sporetSomMottar = liste[tilIndex] ?: error("Ugyldig index $tilIndex")
        val sporetSomFlyttesFra = liste[fraIndex] ?: error("Ugyldig index $fraIndex")
        val kort = sporetSomFlyttesFra.topp.last()

        return SporListe(liste.filterKeys { i -> i != fraIndex && i != tilIndex }
                .plus(fraIndex to sporetSomFlyttesFra.fjernNedersteITopp())
                .plus(tilIndex to sporetSomMottar.motta(kort)))
    }

    fun snuKortISpor(sporIndex: Int): SporListe {
        val sporetSomSkalSnus = liste[sporIndex] ?: error("Ugyldig index $sporIndex")
        return SporListe(liste.filterKeys { i -> i != sporIndex }.plus(sporIndex to sporetSomSkalSnus.snuØverste()))
    }

    fun nedersteITopp(sporIndex: Int): Kort {
        val spor = liste[sporIndex] ?: error("Ugyldig index $sporIndex")
        assert(spor.topp.isNotEmpty())
        return spor.topp.last()
    }

    fun fjernNedersteITopp(sporIndex: Int): SporListe {
        val spor = liste[sporIndex] ?: error("Ugyldig index $sporIndex")
        return SporListe(liste.filterKeys { i -> i != sporIndex }.plus(sporIndex to spor.fjernNedersteITopp()))
    }

    fun kanMotta(kort: Kort): Boolean = liste.values.any { it.kanMotta(kort) }

}

class Spor(val bunn: List<Kort>, val topp: List<Kort>) {
    fun kanMotta(kort: List<Kort>): Boolean = kanMotta(kort[0])

    fun kanMotta(kort: Kort): Boolean {
        return if (bunn.isEmpty() && topp.isEmpty()) {
            kort.verdi == 13
        } else if (topp.isNotEmpty()) {
            kort.farge.erMotsattAv(topp.last().farge) && kort.verdi + 1 == topp.last().verdi
        } else {
            false
        }
    }

    fun kanSnus(): Boolean = topp.isEmpty() && bunn.isNotEmpty()

    fun motta(kort: Kort): Spor = motta(listOf(kort))

    fun motta(kort: List<Kort>): Spor = Spor(bunn, topp.plus(kort))

    fun flyttTopp(): Spor = Spor(bunn, emptyList())

    fun snuØverste(): Spor {
        assert(topp.isEmpty())
        assert(bunn.isNotEmpty())
        val kort = bunn.last()
        return Spor(bunn.dropLast(1), listOf(kort))
    }

    fun fjernNedersteITopp(): Spor {
        assert(topp.isNotEmpty())
        return Spor(bunn, topp.dropLast(1))
    }
}

enum class TrekkType {
    NULL_TREKK,
    FRA_SPOR_TIL_MÅL,
    FRA_SPOR_TIL_SPOR,
    SNU_KORT_I_SPOR,
    FRA_BUNKE_TIL_SPOR,
    FRA_BUNKE_TIL_MÅL,
    FRA_SPOR_TIL_MÅL_MED_OMVEI,
    TREKK_BUNKE,
    SNU_BUNKE
}

data class Trekk(val type: TrekkType, val source: Int, val dest: Int, val neste: Trekk? = null) {

}

