package com.github.steingrd.kabal


enum class Farge {
    HJERTER,
    SPAR,
    RUTER,
    KLØVER;

    fun erMotsattAv(farge: Farge): Boolean {
        return when (farge) {
            HJERTER -> this == SPAR || this == KLØVER
            SPAR -> this == HJERTER || this == RUTER
            RUTER -> this == SPAR || this == KLØVER
            KLØVER -> this == HJERTER || this == RUTER
        }
    }
}

class Kortstokk {

    companion object {
        val alleKort = listOf(
                Kort(Farge.HJERTER, 1),
                Kort(Farge.HJERTER, 2),
                Kort(Farge.HJERTER, 3),
                Kort(Farge.HJERTER, 4),
                Kort(Farge.HJERTER, 5),
                Kort(Farge.HJERTER, 6),
                Kort(Farge.HJERTER, 7),
                Kort(Farge.HJERTER, 8),
                Kort(Farge.HJERTER, 9),
                Kort(Farge.HJERTER, 10),
                Kort(Farge.HJERTER, 11),
                Kort(Farge.HJERTER, 12),
                Kort(Farge.HJERTER, 13),

                Kort(Farge.RUTER, 1),
                Kort(Farge.RUTER, 2),
                Kort(Farge.RUTER, 3),
                Kort(Farge.RUTER, 4),
                Kort(Farge.RUTER, 5),
                Kort(Farge.RUTER, 6),
                Kort(Farge.RUTER, 7),
                Kort(Farge.RUTER, 8),
                Kort(Farge.RUTER, 9),
                Kort(Farge.RUTER, 10),
                Kort(Farge.RUTER, 11),
                Kort(Farge.RUTER, 12),
                Kort(Farge.RUTER, 13),

                Kort(Farge.KLØVER, 1),
                Kort(Farge.KLØVER, 2),
                Kort(Farge.KLØVER, 3),
                Kort(Farge.KLØVER, 4),
                Kort(Farge.KLØVER, 5),
                Kort(Farge.KLØVER, 6),
                Kort(Farge.KLØVER, 7),
                Kort(Farge.KLØVER, 8),
                Kort(Farge.KLØVER, 9),
                Kort(Farge.KLØVER, 10),
                Kort(Farge.KLØVER, 11),
                Kort(Farge.KLØVER, 12),
                Kort(Farge.KLØVER, 13),

                Kort(Farge.SPAR, 1),
                Kort(Farge.SPAR, 2),
                Kort(Farge.SPAR, 3),
                Kort(Farge.SPAR, 4),
                Kort(Farge.SPAR, 5),
                Kort(Farge.SPAR, 6),
                Kort(Farge.SPAR, 7),
                Kort(Farge.SPAR, 8),
                Kort(Farge.SPAR, 9),
                Kort(Farge.SPAR, 10),
                Kort(Farge.SPAR, 11),
                Kort(Farge.SPAR, 12),
                Kort(Farge.SPAR, 13))
    }

}

class Kort(val farge: Farge, val verdi: Int) {

    override fun toString(): String =
            "${farge.name.toUpperCase().substring(0, 4)}_${verdi.toString().padStart(2, '0')}"

}

class Kabal(val mål: Mål, val bunke: Bunke, val spor: List<Spor>)

class Mål(val målSpor: Map<Farge, MålSpor>) {

    fun kanMotta(k: Kort): Boolean = målSpor.values.any { it.kanMotta(k) }

    fun motta(k: Kort): Mål {
        val sporetSomMottar = målSpor[k.farge] ?: error("Ugyldig farge ${k.farge}")
        return Mål(målSpor.filterKeys { f -> f != k.farge }.plus(k.farge to sporetSomMottar.motta(k)))
    }
}

class MålSpor(val farge: Farge, val kort: List<Kort>) {
    fun kanMotta(k: Kort): Boolean =
            farge == k.farge
                    && ((kort.isEmpty() && k.verdi == 1)
                    || kort.isNotEmpty() && k.verdi == kort.last().verdi + 1)

    fun motta(k: Kort): MålSpor = MålSpor(farge, kort.plus(k))
}

class Bunke(val synlig: List<Kort>, val usynlig: List<Kort>, val urørt: Boolean)

class Spor(val bunn: List<Kort>, val topp: List<Kort>) {
    fun kanMotta(kort: Kort): Boolean {
        return if (bunn.isEmpty() && topp.isEmpty()) {
            kort.verdi == 13
        } else if (topp.isNotEmpty()) {
            kort.farge.erMotsattAv(topp.last().farge) && kort.verdi + 1 == topp.last().verdi
        } else {
            false
        }
    }
}

enum class TrekkType {
    NULL_TREKK,
    FRA_SPOR_TIL_MÅL,                // source: spor#    dest: målSpor#
    FRA_SPOR_TIL_SPOR,               // source: spor#    dest: spor#
    SNU_KORT_I_SPOR,               // source: spor#    dest:
    FRA_BUNKE_TIL_SPOR,    // source:          dest: spor#
    FRA_BUNKE_TIL_MÅL,      // source:         dest: målSpor#
    TREKK_BUNKE,            // source:         dest:
    SNU_BUNKE               // source:         dest:
}

data class Trekk(val type: TrekkType, val source: Int, val dest: Int) {
    companion object {
        val NULL_TREKK = Trekk(TrekkType.NULL_TREKK, -1, -1)
    }
}

